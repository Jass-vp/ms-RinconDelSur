package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.client.NotificacionClient;
import com.RinconDelSur.usuario.client.RolClient;
import com.RinconDelSur.usuario.dto.*;
import com.RinconDelSur.usuario.exception.ConflictoException;
import com.RinconDelSur.usuario.exception.NoAutorizadoException;
import com.RinconDelSur.usuario.exception.PeticionInvalidaException;
import com.RinconDelSur.usuario.exception.RecursoNoEncontradoException;
import com.RinconDelSur.usuario.model.Usuario;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolClient rolClient;

    @Mock
    private NotificacionClient notificacionClient;

    @Mock
    private AlmacenamientoService almacenamientoService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario crearUsuarioBase() {
        return Usuario.builder()
                .id(1L)
                .rut("18.456.789-2")
                .nombre("Camila Soto")
                .correo("camila@test.cl")
                .telefono("+56912345678")
                .contrasena("$2a$10$hashDePrueba")
                .cargo("Analista")
                .turno("Turno Diurno")
                .rolId(1L)
                .activo(true)
                .primerIngreso(false)
                .preguntaSeguridad("¿Nombre de tu mascota?")
                .respuestaSeguridad("$2a$10$hashRespuesta")
                .build();
    }

    // ==================== LISTAR Y OBTENER ====================

    @Test
    void listar_sinFiltro_debeRetornarTodosLosUsuarios() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(u));
        when(rolClient.listar()).thenReturn(List.of(new RolDto(1L, "Administrador", "admin", true)));

        List<UsuarioResponse> resultado = usuarioService.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("Camila Soto", resultado.get(0).nombre());
        assertEquals("admin", resultado.get(0).rolNombre());
    }

    @Test
    void listar_conFiltro_debeBuscarPorNombreOCorreo() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc("Camila", "Camila"))
                .thenReturn(List.of(u));
        when(rolClient.listar()).thenReturn(List.of());

        List<UsuarioResponse> resultado = usuarioService.listar("Camila");

        assertEquals(1, resultado.size());
        assertEquals("Observador", resultado.get(0).rolNombre());
    }

    @Test
    void obtener_cuandoExiste_debeRetornarUsuario() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "admin", true));

        UsuarioResponse resultado = usuarioService.obtener(1L);

        assertEquals(1L, resultado.id());
        assertEquals("camila@test.cl", resultado.correo());
    }

    @Test
    void obtener_cuandoNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtener(99L));
    }

    // ==================== REGISTRO ====================

    @Test
    void registrar_cuandoDatosSonValidos_debeGuardarYRetornar() {
        RegistroRequest req = new RegistroRequest(
                "18.456.789-2", "Camila Soto", "camila@test.cl", "+56912345678",
                "clave123", "Analista", "Turno Diurno", 1L, "¿Mascota?", "Firulais"
        );

        when(usuarioRepository.existsByCorreo("camila@test.cl")).thenReturn(false);
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "Admin", true));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(10L);
            return u;
        });

        UsuarioResponse resp = usuarioService.registrar(req);

        assertNotNull(resp);
        assertEquals(10L, resp.id());
        assertEquals("Camila Soto", resp.nombre());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void registrar_cuandoCamposOpcionalesSonNulos_debeGenerarClaveYValoresPorDefecto() {
        RegistroRequest req = new RegistroRequest(
                null, "Nuevo Usuario", "nuevo@test.cl", null,
                null, null, null, null, null, null
        );

        when(usuarioRepository.existsByCorreo("nuevo@test.cl")).thenReturn(false);
        when(rolClient.obtener(4L)).thenReturn(new RolDto(4L, "Observador", "Gestion terreno", true));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(11L);
            return u;
        });

        UsuarioResponse resp = usuarioService.registrar(req);

        assertEquals("Observador de terreno", resp.cargo());
        assertEquals("Turno Diurno", resp.turno());
    }

    @Test
    void registrar_cuandoNombreOCorreoVacio_debeLanzarPeticionInvalida() {
        RegistroRequest req = new RegistroRequest(null, "", "correo@test.cl", null, null, null, null, null, null, null);
        assertThrows(PeticionInvalidaException.class, () -> usuarioService.registrar(req));
    }

    @Test
    void registrar_cuandoCorreoYaExiste_debeLanzarConflicto() {
        RegistroRequest req = new RegistroRequest(null, "Camila", "camila@test.cl", null, null, null, null, null, null, null);
        when(usuarioRepository.existsByCorreo("camila@test.cl")).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.registrar(req));
    }

    @Test
    void registrar_cuandoRolNoExiste_debeLanzarPeticionInvalida() {
        RegistroRequest req = new RegistroRequest(null, "Camila", "camila@test.cl", null, null, null, null, 99L, null, null);
        when(usuarioRepository.existsByCorreo("camila@test.cl")).thenReturn(false);
        when(rolClient.obtener(99L)).thenReturn(null);

        assertThrows(PeticionInvalidaException.class, () -> usuarioService.registrar(req));
    }

    // ==================== INICIAR SESIÓN ====================

    @Test
    void iniciarSesion_conCredencialesValidasBCrypt_debeRetornarUsuario() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("clave123", "$2a$10$hashDePrueba")).thenReturn(true);
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "Gestion", true));

        UsuarioResponse resp = usuarioService.iniciarSesion(new LoginRequest("camila@test.cl", "clave123"));

        assertEquals("camila@test.cl", resp.correo());
    }

    @Test
    void iniciarSesion_conClaveTextoPlano_debeMigrarABCrypt() {
        Usuario u = crearUsuarioBase();
        u.setContrasena("clavePlana");
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("clavePlana")).thenReturn("$2a$10$nuevoHash");
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "Admin", true));

        usuarioService.iniciarSesion(new LoginRequest("camila@test.cl", "clavePlana"));

        verify(usuarioRepository).save(u);
    }

    @Test
    void iniciarSesion_cuandoUsuarioInactivo_debeLanzarNoAutorizado() {
        Usuario u = crearUsuarioBase();
        u.setActivo(false);
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("clave123", "$2a$10$hashDePrueba")).thenReturn(true);

        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("camila@test.cl", "clave123")));
    }

    @Test
    void iniciarSesion_cuandoClaveIncorrecta_debeLanzarNoAutorizado() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "$2a$10$hashDePrueba")).thenReturn(false);

        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("camila@test.cl", "mala")));
    }

    // ==================== SEGURIDAD Y RECUPERACIÓN ====================

    @Test
    void completarPrimerIngreso_cuandoValido_debeActualizar() {
        Usuario u = crearUsuarioBase();
        u.setPrimerIngreso(true);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hash");
        when(usuarioRepository.save(any())).thenReturn(u);
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "Admin", true));

        UsuarioResponse resp = usuarioService.completarPrimerIngreso(1L,
                new PrimerIngresoRequest("nueva123", "¿Color?", "Azul"));

        assertFalse(resp.primerIngreso());
    }

    @Test
    void completarPrimerIngreso_cuandoClaveCorta_debeLanzarExcepcion() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        assertThrows(PeticionInvalidaException.class,
                () -> usuarioService.completarPrimerIngreso(1L, new PrimerIngresoRequest("123", null, null)));
    }

    @Test
    void listarPreguntasSeguridad_debeRetornarLista() {
        when(usuarioRepository.obtenerPreguntasDisponibles()).thenReturn(List.of("¿Mascota?", "¿Ciudad?"));
        assertEquals(2, usuarioService.listarPreguntasSeguridad().size());
    }

    @Test
    void obtenerPreguntaSeguridad_cuandoExiste_debeRetornarPregunta() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));

        assertEquals("¿Nombre de tu mascota?", usuarioService.obtenerPreguntaSeguridad("camila@test.cl"));
    }

    @Test
    void obtenerPreguntaSeguridad_cuandoNoTienePregunta_debeLanzarExcepcion() {
        Usuario u = crearUsuarioBase();
        u.setPreguntaSeguridad(null);
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));

        assertThrows(PeticionInvalidaException.class,
                () -> usuarioService.obtenerPreguntaSeguridad("camila@test.cl"));
    }

    @Test
    void verificarRespuesta_debeValidarPreguntaYRespuestaCorrectamente() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("firulais", "$2a$10$hashRespuesta")).thenReturn(true);
        when(passwordEncoder.matches("incorrecta", "$2a$10$hashRespuesta")).thenReturn(false);

        assertTrue(usuarioService.verificarRespuesta("camila@test.cl", "¿Nombre de tu mascota?", "Firulais"));
        assertFalse(usuarioService.verificarRespuesta("camila@test.cl", "¿Nombre de tu mascota?", "Incorrecta"));
        assertFalse(usuarioService.verificarRespuesta("camila@test.cl", "¿Pregunta distinta?", "Firulais"));
    }

    @Test
    void verificarRespuesta_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findByCorreoOrRut("noexiste@test.cl", "noexiste@test.cl")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.verificarRespuesta("noexiste@test.cl", "¿Pregunta?", "Respuesta"));
    }

    @Test
    void restablecerContrasena_debeGuardarNuevaClave() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.encode("nuevaClave")).thenReturn("$2a$10$nueva");

        usuarioService.restablecerContrasena(new RestablecerContrasenaRequest("camila@test.cl", "nuevaClave"));

        verify(usuarioRepository).save(u);
    }

    @Test
    void enviarClaveTemporalRecuperacion_cuandoActivo_debeGenerarClaveYGuardar() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(u));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$temp");

        usuarioService.enviarClaveTemporalRecuperacion("camila@test.cl");

        assertTrue(u.getPrimerIngreso());
        verify(usuarioRepository).save(u);
    }

    @Test
    void enviarClaveTemporalRecuperacion_cuandoVacioOInactivo_debeLanzarExcepcion() {
        assertThrows(PeticionInvalidaException.class, () -> usuarioService.enviarClaveTemporalRecuperacion(""));

        Usuario inactivo = crearUsuarioBase();
        inactivo.setActivo(false);
        when(usuarioRepository.findByCorreoOrRut("camila@test.cl", "camila@test.cl")).thenReturn(Optional.of(inactivo));

        assertThrows(NoAutorizadoException.class, () -> usuarioService.enviarClaveTemporalRecuperacion("camila@test.cl"));
    }

    // ==================== ACTUALIZAR, CAMBIAR CLAVE, AVATAR Y ELIMINAR ====================

    @Test
    void actualizar_cuandoValido_debeActualizarCampos() {
        Usuario u = crearUsuarioBase();
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "Camila Editada", "camila@test.cl", "+56900000000", "Jefa", "Noche", 2L, true, "¿Ciudad?", "Santiago"
        );
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.existsByCorreoAndIdNot("camila@test.cl", 1L)).thenReturn(false);
        when(rolClient.obtener(2L)).thenReturn(new RolDto(2L, "Supervisor", "Supervisa conteos", true));
        when(passwordEncoder.encode("santiago")).thenReturn("$2a$10$resp");
        when(usuarioRepository.save(any())).thenReturn(u);

        UsuarioResponse resp = usuarioService.actualizar(1L, req);

        assertEquals("Camila Editada", resp.nombre());
    }

    @Test
    void actualizar_cuandoCorreoDuplicado_debeLanzarConflicto() {
        Usuario u = crearUsuarioBase();
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "Camila", "otro@test.cl", null, null, null, null, null, null, null
        );
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.existsByCorreoAndIdNot("otro@test.cl", 1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.actualizar(1L, req));
    }

    @Test
    void cambiarContrasena_cuandoValida_debeGuardar() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("actual123", "$2a$10$hashDePrueba")).thenReturn(true);
        when(passwordEncoder.encode("nueva123")).thenReturn("$2a$10$nueva");

        usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("actual123", "nueva123"));

        verify(usuarioRepository).save(u);
    }

    @Test
    void cambiarContrasena_cuandoActualIncorrecta_debeLanzarNoAutorizado() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "$2a$10$hashDePrueba")).thenReturn(false);

        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("mala", "nueva123")));
    }

    @Test
    void subirYObtenerAvatar_debeGestionarArchivoYUrl() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        Usuario u = crearUsuarioBase();
        u.setAvatar("viejo.png");
        MultipartFile archivo = mock(MultipartFile.class);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(almacenamientoService.guardar(archivo)).thenReturn("nuevo.png");
        when(usuarioRepository.save(any())).thenReturn(u);
        when(rolClient.obtener(1L)).thenReturn(new RolDto(1L, "Administrador", "Admin", true));
        when(almacenamientoService.leer("nuevo.png")).thenReturn(new byte[]{1, 2, 3});

        UsuarioResponse resp = usuarioService.subirAvatar(1L, archivo);
        byte[] bytes = usuarioService.obtenerAvatar(1L);

        assertTrue(resp.avatarUrl().contains("nuevo.png"));
        assertEquals(3, bytes.length);
        verify(almacenamientoService).eliminar("viejo.png");

        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void eliminar_debeBorrarUsuarioYAvatar() {
        Usuario u = crearUsuarioBase();
        u.setAvatar("foto.png");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        usuarioService.eliminar(1L);

        verify(usuarioRepository).delete(u);
        verify(almacenamientoService).eliminar("foto.png");
    }
    @Test
    void obtenerAvatar_cuandoUsuarioNoTieneAvatar_debeLanzarExcepcion() {
        Usuario u = crearUsuarioBase();
        u.setAvatar(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerAvatar(1L));
    }

    @Test
    void cambiarContrasena_cuandoNuevaEsMuyCorta_debeLanzarExcepcion() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("actual123", "$2a$10$hashDePrueba")).thenReturn(true);

        assertThrows(PeticionInvalidaException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("actual123", "123")));
    }

    @Test
    void actualizar_cuandoCamposObligatoriosVacios_debeLanzarExcepcion() {
        Usuario u = crearUsuarioBase();
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "", "camila@test.cl", null, null, null, null, null, null, null
        );

        assertThrows(PeticionInvalidaException.class, () -> usuarioService.actualizar(1L, req));
    }

    @Test
    void obtenerPreguntaSeguridad_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findByCorreoOrRut("no@test.cl", "no@test.cl")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.obtenerPreguntaSeguridad("no@test.cl"));
    }

    @Test
    void restablecerContrasena_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepository.findByCorreoOrRut("no@test.cl", "no@test.cl")).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.restablecerContrasena(new RestablecerContrasenaRequest("no@test.cl", "1234")));
    }
}