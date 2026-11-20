package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.client.RolClient;
import com.RinconDelSur.usuario.dto.*;
import com.RinconDelSur.usuario.exception.ConflictoException;
import com.RinconDelSur.usuario.exception.NoAutorizadoException;
import com.RinconDelSur.usuario.exception.PeticionInvalidaException;
import com.RinconDelSur.usuario.exception.RecursoNoEncontradoException;
import com.RinconDelSur.usuario.model.Usuario;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private static final Long ROL_DEFECTO = 4L;

    private final UsuarioRepository usuarioRepository;
    private final RolClient rolClient;
    private final AlmacenamientoService almacenamientoService;

    public List<UsuarioResponse> listar(String buscar) {
        log.debug("Listando usuarios con filtro: '{}'", buscar);
        List<Usuario> usuarios = (buscar == null || buscar.isBlank())
                ? usuarioRepository.findAllByOrderByNombreAsc()
                : usuarioRepository.findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(buscar, buscar);

        Map<Long, RolDto> rolesMap = rolClient.listar().stream()
                .collect(Collectors.toMap(RolDto::id, r -> r));

        return usuarios.stream()
                .map(usuario -> aRespuesta(usuario, rolesMap.get(usuario.getRolId())))
                .toList();
    }

    public UsuarioResponse obtener(Long id) {
        log.debug("Obteniendo usuario con ID: {}", id);
        return aRespuesta(buscar(id));
    }

    public UsuarioResponse registrar(RegistroRequest datos) {
        log.info("Iniciando registro de nuevo usuario con correo: {}", datos.correo());
        if (vacio(datos.nombre()) || vacio(datos.correo())) {
            log.warn("Registro fallido: nombre o correo vacío");
            throw new PeticionInvalidaException("Nombre y correo son obligatorios");
        }
        if (datos.contrasena() == null || datos.contrasena().length() < 4) {
            log.warn("Registro fallido para {}: contraseña menor a 4 caracteres", datos.correo());
            throw new PeticionInvalidaException("La contraseña debe tener mínimo 4 caracteres");
        }
        if (usuarioRepository.existsByCorreo(datos.correo())) {
            log.warn("Registro fallido: el correo {} ya está registrado", datos.correo());
            throw new ConflictoException("Ese correo ya está registrado");
        }

        Long rolId = datos.rolId() != null ? datos.rolId() : ROL_DEFECTO;
        validarRol(rolId);

        Usuario nuevo = Usuario.builder()
                .rut(datos.rut() != null ? datos.rut() : UUIDString())
                .nombre(datos.nombre())
                .correo(datos.correo())
                .telefono(datos.telefono())
                .contrasena(datos.contrasena())
                .cargo(datos.cargo() != null ? datos.cargo() : "Observador de terreno")
                .turno(datos.turno() != null ? datos.turno() : "Turno Diurno")
                .rolId(rolId)
                .activo(true)
                .primerIngreso(true)
                .preguntaSeguridad(datos.preguntaSeguridad())
                .respuestaSeguridad(datos.respuestaSeguridad())
                .build();

        Usuario guardado = usuarioRepository.save(nuevo);
        log.info("Usuario registrado exitosamente con ID: {} y RUT: {}", guardado.getId(), guardado.getRut());
        return aRespuesta(guardado);
    }

    public UsuarioResponse iniciarSesion(LoginRequest datos) {
        log.info("Intento de inicio de sesión para identificador: {}", datos.correo());
        Usuario usuario = usuarioRepository.findByCorreoOrRut(datos.correo(), datos.correo())
                .filter(u -> u.getContrasena().equals(datos.contrasena()))
                .orElseThrow(() -> {
                    log.warn("Inicio de sesión fallido: credenciales inválidas para {}", datos.correo());
                    return new NoAutorizadoException("Correo o contraseña incorrectos");
                });

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            log.warn("Inicio de sesión bloqueado: usuario ID {} ({}) se encuentra inhabilitado", usuario.getId(), usuario.getRut());
            throw new NoAutorizadoException("Tu usuario se encuentra inhabilitado en el sistema");
        }

        log.info("Inicio de sesión exitoso para usuario ID: {} ({})", usuario.getId(), usuario.getRut());
        return aRespuesta(usuario);
    }

    public UsuarioResponse completarPrimerIngreso(Long id, PrimerIngresoRequest datos) {
        log.info("Completando configuración de primer ingreso para usuario ID: {}", id);
        Usuario usuario = buscar(id);
        if (datos.nuevaContrasena() == null || datos.nuevaContrasena().length() < 4) {
            log.warn("Primer ingreso fallido para usuario ID {}: contraseña menor a 4 caracteres", id);
            throw new PeticionInvalidaException("La nueva contraseña debe tener al menos 4 caracteres");
        }
        usuario.setContrasena(datos.nuevaContrasena());
        if (datos.preguntaSeguridad() != null) usuario.setPreguntaSeguridad(datos.preguntaSeguridad());
        if (datos.respuestaSeguridad() != null) usuario.setRespuestaSeguridad(datos.respuestaSeguridad());
        usuario.setPrimerIngreso(false);

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Primer ingreso completado exitosamente para usuario ID: {}", id);
        return aRespuesta(actualizado);
    }

    public List<String> listarPreguntasSeguridad() {
        log.debug("Listando preguntas de seguridad disponibles en base de datos");
        return usuarioRepository.obtenerPreguntasDisponibles();
    }

    public String obtenerPreguntaSeguridad(String rutOCorreo) {
        log.debug("Verificando existencia y pregunta de seguridad para RUT/Correo: {}", rutOCorreo);
        Usuario usuario = usuarioRepository.findByCorreoOrRut(rutOCorreo, rutOCorreo)
                .orElseThrow(() -> {
                    log.warn("Pregunta de seguridad fallida: usuario no encontrado con {}", rutOCorreo);
                    return new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + rutOCorreo);
                });
        if (usuario.getPreguntaSeguridad() == null || usuario.getPreguntaSeguridad().isBlank()) {
            log.warn("Usuario ID {} no tiene pregunta de seguridad configurada", usuario.getId());
            throw new PeticionInvalidaException("El usuario no tiene pregunta de seguridad configurada");
        }
        return usuario.getPreguntaSeguridad();
    }

    public boolean verificarRespuesta(String rutOCorreo, String pregunta, String respuesta) {
        log.debug("Verificando pregunta y respuesta de seguridad para RUT/Correo: {}", rutOCorreo);
        Usuario usuario = usuarioRepository.findByCorreoOrRut(rutOCorreo, rutOCorreo)
                .orElseThrow(() -> {
                    log.warn("Verificación de respuesta fallida: usuario no encontrado con {}", rutOCorreo);
                    return new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + rutOCorreo);
                });

        boolean preguntaCoincide = usuario.getPreguntaSeguridad() != null
                && pregunta != null
                && usuario.getPreguntaSeguridad().trim().equalsIgnoreCase(pregunta.trim());

        boolean respuestaCoincide = usuario.getRespuestaSeguridad() != null
                && respuesta != null
                && usuario.getRespuestaSeguridad().trim().equalsIgnoreCase(respuesta.trim());

        boolean valida = preguntaCoincide && respuestaCoincide;
        log.info("Verificación de seguridad para {}: {}", rutOCorreo, valida ? "VÁLIDA" : "INVÁLIDA");
        return valida;
    }

    public void restablecerContrasena(RestablecerContrasenaRequest datos) {
        log.info("Restableciendo contraseña para RUT/Correo: {}", datos.rutOCorreo());
        Usuario usuario = usuarioRepository.findByCorreoOrRut(datos.rutOCorreo(), datos.rutOCorreo())
                .orElseThrow(() -> {
                    log.warn("Restablecimiento de contraseña fallido: usuario no encontrado con {}", datos.rutOCorreo());
                    return new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + datos.rutOCorreo());
                });
        usuario.setContrasena(datos.nuevaContrasena());
        usuarioRepository.save(usuario);
        log.info("Contraseña restablecida exitosamente para usuario ID: {}", usuario.getId());
    }

    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest datos) {
        log.info("Actualizando datos del usuario ID: {}", id);
        Usuario usuario = buscar(id);

        if (vacio(datos.nombre()) || vacio(datos.correo())) {
            log.warn("Actualización fallida para usuario ID {}: nombre o correo vacío", id);
            throw new PeticionInvalidaException("Nombre y correo son obligatorios");
        }
        if (usuarioRepository.existsByCorreoAndIdNot(datos.correo(), id)) {
            log.warn("Actualización fallida para usuario ID {}: correo {} ya en uso", id, datos.correo());
            throw new ConflictoException("Ese correo ya está registrado por otro usuario");
        }
        if (datos.rolId() != null) {
            validarRol(datos.rolId());
            usuario.setRolId(datos.rolId());
        }

        usuario.setNombre(datos.nombre());
        usuario.setCorreo(datos.correo());
        if (datos.telefono() != null) usuario.setTelefono(datos.telefono());
        if (datos.cargo() != null) usuario.setCargo(datos.cargo());
        if (datos.turno() != null) usuario.setTurno(datos.turno());
        if (datos.activo() != null) usuario.setActivo(datos.activo());
        if (datos.preguntaSeguridad() != null) usuario.setPreguntaSeguridad(datos.preguntaSeguridad());
        if (datos.respuestaSeguridad() != null) usuario.setRespuestaSeguridad(datos.respuestaSeguridad());

        Usuario actualizado = usuarioRepository.save(usuario);
        log.info("Usuario ID {} actualizado exitosamente", id);
        return aRespuesta(actualizado);
    }

    public void cambiarContrasena(Long id, CambiarContrasenaRequest datos) {
        log.info("Solicitud de cambio de contraseña para usuario ID: {}", id);
        Usuario usuario = buscar(id);

        if (!usuario.getContrasena().equals(datos.actual())) {
            log.warn("Cambio de contraseña fallido para usuario ID {}: contraseña actual incorrecta", id);
            throw new NoAutorizadoException("La contraseña actual es incorrecta");
        }
        if (datos.nueva() == null || datos.nueva().length() < 4) {
            log.warn("Cambio de contraseña fallido para usuario ID {}: nueva contraseña menor a 4 caracteres", id);
            throw new PeticionInvalidaException("La contraseña debe tener mínimo 4 caracteres");
        }

        usuario.setContrasena(datos.nueva());
        usuarioRepository.save(usuario);
        log.info("Contraseña actualizada exitosamente para usuario ID: {}", id);
    }

    public UsuarioResponse subirAvatar(Long id, MultipartFile archivo) {
        log.info("Subiendo nuevo avatar para usuario ID: {}", id);
        Usuario usuario = buscar(id);
        String anterior = usuario.getAvatar();

        usuario.setAvatar(almacenamientoService.guardar(archivo));
        Usuario guardado = usuarioRepository.save(usuario);

        almacenamientoService.eliminar(anterior);
        log.info("Avatar actualizado exitosamente para usuario ID: {}", id);
        return aRespuesta(guardado);
    }

    public byte[] obtenerAvatar(Long id) {
        log.debug("Obteniendo avatar del usuario ID: {}", id);
        Usuario usuario = buscar(id);
        if (usuario.getAvatar() == null) {
            log.warn("Usuario ID {} no tiene avatar asignado", id);
            throw new RecursoNoEncontradoException("El usuario no tiene foto de perfil");
        }
        return almacenamientoService.leer(usuario.getAvatar());
    }

    public void eliminar(Long id) {
        log.info("Eliminando usuario ID: {}", id);
        Usuario usuario = buscar(id);
        usuarioRepository.delete(usuario);
        almacenamientoService.eliminar(usuario.getAvatar());
        log.info("Usuario ID {} eliminado exitosamente", id);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado con ID: {}", id);
                    return new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id);
                });
    }

    private void validarRol(Long rolId) {
        if (rolClient.obtener(rolId) == null) {
            log.warn("Validación de rol fallida: el rol ID {} no existe", rolId);
            throw new PeticionInvalidaException("El rol indicado no existe en el sistema");
        }
    }

    private UsuarioResponse aRespuesta(Usuario usuario) {
        RolDto rol = rolClient.obtener(usuario.getRolId());
        return aRespuesta(usuario, rol);
    }

    private UsuarioResponse aRespuesta(Usuario usuario, RolDto rol) {
        String rolNombre = rol != null ? rol.descripcion() : "Observador";
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTelefono(),
                usuario.getCargo(),
                usuario.getTurno(),
                usuario.getRolId(),
                rolNombre,
                usuario.getActivo(),
                usuario.getPrimerIngreso() != null ? usuario.getPrimerIngreso() : false,
                urlAvatar(usuario)
        );
    }

    private String urlAvatar(Usuario usuario) {
        if (usuario.getAvatar() == null) {
            return null;
        }
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/usuarios/" + usuario.getId() + "/avatar")
                .queryParam("v", usuario.getAvatar())
                .toUriString();
    }

    private boolean vacio(String texto) {
        return texto == null || texto.isBlank();
    }

    private String UUIDString() {
        return java.util.UUID.randomUUID().toString().substring(0, 10);
    }
}