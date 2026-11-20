package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.client.RolClient;
import com.RinconDelSur.usuario.dto.*;
import com.RinconDelSur.usuario.exception.ConflictoException;
import com.RinconDelSur.usuario.exception.NoAutorizadoException;
import com.RinconDelSur.usuario.exception.PeticionInvalidaException;
import com.RinconDelSur.usuario.exception.RecursoNoEncontradoException;
import com.RinconDelSur.usuario.model.Usuario;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolClient rolClient;

    @Mock
    private AlmacenamientoService almacenamientoService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuarioEjemplo;
    private RolDto rolEjemplo;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        rolEjemplo = new RolDto(4L, "P-04", "Observador de terreno", true);
        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .rut("18.111.222-3")
                .nombre("Camila Soto")
                .correo("c.soto@rincondelsur.cl")
                .telefono("911223344")
                .contrasena("1234")
                .cargo("Observador de terreno")
                .turno("Turno Diurno")
                .rolId(4L)
                .activo(true)
                .primerIngreso(true)
                .preguntaSeguridad("¿Mascota?")
                .respuestaSeguridad("Toby")
                .avatar("avatar.jpg")
                .build();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void listar_sinFiltro_retornaTodosMapeados() {
        when(usuarioRepository.findAllByOrderByNombreAsc()).thenReturn(List.of(usuarioEjemplo));
        when(rolClient.listar()).thenReturn(List.of(rolEjemplo));

        List<UsuarioResponse> resultado = usuarioService.listar(null);

        assertEquals(1, resultado.size());
        assertEquals("Camila Soto", resultado.get(0).nombre());
        assertEquals("Observador de terreno", resultado.get(0).rolNombre());
        assertNotNull(resultado.get(0).avatarUrl());
    }

    @Test
    void listar_conFiltro_buscaPorNombreOCorreo() {
        when(usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc("Camila", "Camila"))
                .thenReturn(List.of(usuarioEjemplo));
        when(rolClient.listar()).thenReturn(List.of(rolEjemplo));

        List<UsuarioResponse> resultado = usuarioService.listar("Camila");

        assertEquals(1, resultado.size());
    }

    @Test
    void obtener_idExistente_retornaUsuario() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo);

        UsuarioResponse resp = usuarioService.obtener(1L);

        assertEquals(1L, resp.id());
        assertEquals("18.111.222-3", resp.rut());
    }

    @Test
    void obtener_idInexistente_lanzaNotFound() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtener(99L));
    }

    @Test
    void registrar_datosValidos_creaUsuario() {
        RegistroRequest req = new RegistroRequest(
                "19.222.333-4", "Mateo Silva", "m.silva@rincondelsur.cl", "955667788",
                "1234", "Observador de terreno", "Turno Diurno", 4L, "¿Ciudad?", "Valdivia");

        when(usuarioRepository.existsByCorreo("m.silva@rincondelsur.cl")).thenReturn(false);
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(2L);
            return u;
        });

        UsuarioResponse resp = usuarioService.registrar(req);

        assertEquals(2L, resp.id());
        assertEquals("Mateo Silva", resp.nombre());
        assertTrue(resp.primerIngreso());
    }

    @Test
    void registrar_validacionesFallidas_lanzaExcepciones() {
        RegistroRequest sinNombre = new RegistroRequest(null, "", "a@b.cl", null, "1234", null, null, null, null, null);
        assertThrows(PeticionInvalidaException.class, () -> usuarioService.registrar(sinNombre));

        RegistroRequest claveCorta = new RegistroRequest(null, "Ana", "a@b.cl", null, "12", null, null, null, null, null);
        assertThrows(PeticionInvalidaException.class, () -> usuarioService.registrar(claveCorta));

        RegistroRequest duplicado = new RegistroRequest(null, "Ana", "c.soto@rincondelsur.cl", null, "1234", null, null, null, null, null);
        when(usuarioRepository.existsByCorreo("c.soto@rincondelsur.cl")).thenReturn(true);
        assertThrows(ConflictoException.class, () -> usuarioService.registrar(duplicado));
    }

    @Test
    void registrar_rolInexistente_lanzaPeticionInvalida() {
        RegistroRequest req = new RegistroRequest(null, "Ana", "ana@rincondelsur.cl", null, "1234", null, null, 99L, null, null);
        when(usuarioRepository.existsByCorreo("ana@rincondelsur.cl")).thenReturn(false);
        when(rolClient.obtener(99L)).thenReturn(null);

        assertThrows(PeticionInvalidaException.class, () -> usuarioService.registrar(req));
    }

    @Test
    void iniciarSesion_credencialesValidas_retornaUsuario() {
        when(usuarioRepository.findByCorreoOrRut("c.soto@rincondelsur.cl", "c.soto@rincondelsur.cl"))
                .thenReturn(Optional.of(usuarioEjemplo));
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo);

        UsuarioResponse resp = usuarioService.iniciarSesion(new LoginRequest("c.soto@rincondelsur.cl", "1234"));

        assertEquals("Camila Soto", resp.nombre());
    }

    @Test
    void iniciarSesion_claveIncorrectaOUsuarioInactivo_lanzaNoAutorizado() {
        when(usuarioRepository.findByCorreoOrRut("c.soto@rincondelsur.cl", "c.soto@rincondelsur.cl"))
                .thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("c.soto@rincondelsur.cl", "wrong")));

        usuarioEjemplo.setActivo(false);
        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.iniciarSesion(new LoginRequest("c.soto@rincondelsur.cl", "1234")));
    }

    @Test
    void completarPrimerIngreso_actualizaClaveYPrimerIngreso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse resp = usuarioService.completarPrimerIngreso(1L,
                new PrimerIngresoRequest("5678", "¿Color?", "Azul"));

        assertFalse(resp.primerIngreso());
        assertEquals("5678", usuarioEjemplo.getContrasena());
    }

    @Test
    void completarPrimerIngreso_claveCorta_lanzaPeticionInvalida() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(PeticionInvalidaException.class,
                () -> usuarioService.completarPrimerIngreso(1L, new PrimerIngresoRequest("12", null, null)));
    }

    @Test
    void seguridadYRestablecimiento_flujoCompleto() {
        when(usuarioRepository.findByCorreoOrRut("18.111.222-3", "18.111.222-3"))
                .thenReturn(Optional.of(usuarioEjemplo));

        assertEquals("¿Mascota?", usuarioService.obtenerPreguntaSeguridad("18.111.222-3"));
        assertTrue(usuarioService.verificarRespuesta("18.111.222-3", " toby "));
        assertFalse(usuarioService.verificarRespuesta("18.111.222-3", "otro"));

        usuarioService.restablecerContrasena(new RestablecerContrasenaRequest("18.111.222-3", "9999"));
        assertEquals("9999", usuarioEjemplo.getContrasena());
        verify(usuarioRepository).save(usuarioEjemplo);
    }

    @Test
    void obtenerPreguntaSeguridad_sinPreguntaConfigurada_lanzaPeticionInvalida() {
        usuarioEjemplo.setPreguntaSeguridad(null);
        when(usuarioRepository.findByCorreoOrRut("18.111.222-3", "18.111.222-3"))
                .thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(PeticionInvalidaException.class, () -> usuarioService.obtenerPreguntaSeguridad("18.111.222-3"));
    }

    @Test
    void actualizar_datosValidos_actualizaUsuario() {
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "Camila Soto Updated", "camila.nueva@rincondelsur.cl", "999999999",
                "Supervisora", "Turno Vespertino", 3L, true, "¿Libro?", "Rayuela");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.existsByCorreoAndIdNot("camila.nueva@rincondelsur.cl", 1L)).thenReturn(false);
        when(rolClient.obtener(3L)).thenReturn(new RolDto(3L, "P-03", "Supervisora", true));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse resp = usuarioService.actualizar(1L, req);

        assertEquals("Camila Soto Updated", resp.nombre());
        assertEquals(3L, resp.rolId());
    }

    @Test
    void actualizar_correoDuplicado_lanzaConflicto() {
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(
                "Camila", "duplicado@rincondelsur.cl", null, null, null, null, null, null, null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(usuarioRepository.existsByCorreoAndIdNot("duplicado@rincondelsur.cl", 1L)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> usuarioService.actualizar(1L, req));
    }

    @Test
    void cambiarContrasena_flujoExitosoYErrores() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(NoAutorizadoException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("mal", "5678")));

        assertThrows(PeticionInvalidaException.class,
                () -> usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("1234", "12")));

        usuarioService.cambiarContrasena(1L, new CambiarContrasenaRequest("1234", "5678"));
        assertEquals("5678", usuarioEjemplo.getContrasena());
    }

    @Test
    void avatarYEliminar_gestionaAlmacenamiento() {
        MockMultipartFile file = new MockMultipartFile("archivo", "new.jpg", "image/jpeg", "img".getBytes());
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        when(almacenamientoService.guardar(file)).thenReturn("nuevo-avatar.jpg");
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        UsuarioResponse resp = usuarioService.subirAvatar(1L, file);
        assertTrue(resp.avatarUrl().contains("nuevo-avatar.jpg"));
        verify(almacenamientoService).eliminar("avatar.jpg");

        when(almacenamientoService.leer("nuevo-avatar.jpg")).thenReturn("img".getBytes());
        assertArrayEquals("img".getBytes(), usuarioService.obtenerAvatar(1L));

        usuarioService.eliminar(1L);
        verify(usuarioRepository).delete(usuarioEjemplo);
        verify(almacenamientoService).eliminar("nuevo-avatar.jpg");
    }

    @Test
    void obtenerAvatar_sinAvatar_lanzaNotFound() {
        usuarioEjemplo.setAvatar(null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));

        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerAvatar(1L));
    }
    @Test
    void registrar_sinOpcionales_asignaValoresPorDefecto() {
        RegistroRequest req = new RegistroRequest(
                null, "Pedro", "pedro@rincondelsur.cl", null, "1234",
                null, null, null, null, null);
        when(usuarioRepository.existsByCorreo("pedro@rincondelsur.cl")).thenReturn(false);
        when(rolClient.obtener(4L)).thenReturn(rolEjemplo, (RolDto) null);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> {
            Usuario u = inv.getArgument(0);
            u.setId(3L);
            return u;
        });

        UsuarioResponse resp = usuarioService.registrar(req);

        assertEquals("Observador", resp.rolNombre());
        assertEquals("Observador de terreno", resp.cargo());
        assertEquals("Turno Diurno", resp.turno());
        assertNotNull(resp.rut());
    }

    @Test
    void actualizar_datosInvalidosYUsuarioNoEncontrado_lanzaExcepciones() {
        ActualizarUsuarioRequest vacio = new ActualizarUsuarioRequest(
                "", "a@b.cl", null, null, null, null, null, null, null);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioEjemplo));
        assertThrows(PeticionInvalidaException.class, () -> usuarioService.actualizar(1L, vacio));

        when(usuarioRepository.findByCorreoOrRut("no@existe.cl", "no@existe.cl")).thenReturn(Optional.empty());
        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.obtenerPreguntaSeguridad("no@existe.cl"));
        assertThrows(RecursoNoEncontradoException.class, () -> usuarioService.verificarRespuesta("no@existe.cl", "x"));
        assertThrows(RecursoNoEncontradoException.class,
                () -> usuarioService.restablecerContrasena(new RestablecerContrasenaRequest("no@existe.cl", "1234")));
    }
}