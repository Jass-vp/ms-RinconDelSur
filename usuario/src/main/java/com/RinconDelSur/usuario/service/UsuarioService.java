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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private static final Long ROL_DEFECTO = 4L;

    private final UsuarioRepository usuarioRepository;
    private final RolClient rolClient;
    private final AlmacenamientoService almacenamientoService;

    public List<UsuarioResponse> listar(String buscar) {
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
        return aRespuesta(buscar(id));
    }

    public UsuarioResponse registrar(RegistroRequest datos) {
        if (vacio(datos.nombre()) || vacio(datos.correo())) {
            throw new PeticionInvalidaException("Nombre y correo son obligatorios");
        }
        if (datos.contrasena() == null || datos.contrasena().length() < 4) {
            throw new PeticionInvalidaException("La contraseña debe tener mínimo 4 caracteres");
        }
        if (usuarioRepository.existsByCorreo(datos.correo())) {
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
        return aRespuesta(usuarioRepository.save(nuevo));
    }

    public UsuarioResponse iniciarSesion(LoginRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreoOrRut(datos.correo(), datos.correo())
                .filter(u -> u.getContrasena().equals(datos.contrasena()))
                .orElseThrow(() -> new NoAutorizadoException("Correo o contraseña incorrectos"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new NoAutorizadoException("Tu usuario se encuentra inhabilitado en el sistema");
        }

        return aRespuesta(usuario);
    }

    public UsuarioResponse completarPrimerIngreso(Long id, PrimerIngresoRequest datos) {
        Usuario usuario = buscar(id);
        if (datos.nuevaContrasena() == null || datos.nuevaContrasena().length() < 4) {
            throw new PeticionInvalidaException("La nueva contraseña debe tener al menos 4 caracteres");
        }
        usuario.setContrasena(datos.nuevaContrasena());
        if (datos.preguntaSeguridad() != null) usuario.setPreguntaSeguridad(datos.preguntaSeguridad());
        if (datos.respuestaSeguridad() != null) usuario.setRespuestaSeguridad(datos.respuestaSeguridad());
        usuario.setPrimerIngreso(false);

        return aRespuesta(usuarioRepository.save(usuario));
    }

    public String obtenerPreguntaSeguridad(String rutOCorreo) {
        Usuario usuario = usuarioRepository.findByCorreoOrRut(rutOCorreo, rutOCorreo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + rutOCorreo));
        if (usuario.getPreguntaSeguridad() == null) {
            throw new PeticionInvalidaException("El usuario no tiene pregunta de seguridad configurada");
        }
        return usuario.getPreguntaSeguridad();
    }

    public boolean verificarRespuesta(String rutOCorreo, String respuesta) {
        Usuario usuario = usuarioRepository.findByCorreoOrRut(rutOCorreo, rutOCorreo)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + rutOCorreo));
        return usuario.getRespuestaSeguridad() != null && usuario.getRespuestaSeguridad().equalsIgnoreCase(respuesta.trim());
    }

    public void restablecerContrasena(RestablecerContrasenaRequest datos) {
        Usuario usuario = usuarioRepository.findByCorreoOrRut(datos.rutOCorreo(), datos.rutOCorreo())
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con RUT/Correo: " + datos.rutOCorreo()));
        usuario.setContrasena(datos.nuevaContrasena());
        usuarioRepository.save(usuario);
    }

    public UsuarioResponse actualizar(Long id, ActualizarUsuarioRequest datos) {
        Usuario usuario = buscar(id);

        if (vacio(datos.nombre()) || vacio(datos.correo())) {
            throw new PeticionInvalidaException("Nombre y correo son obligatorios");
        }
        if (usuarioRepository.existsByCorreoAndIdNot(datos.correo(), id)) {
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

        return aRespuesta(usuarioRepository.save(usuario));
    }

    public void cambiarContrasena(Long id, CambiarContrasenaRequest datos) {
        Usuario usuario = buscar(id);

        if (!usuario.getContrasena().equals(datos.actual())) {
            throw new NoAutorizadoException("La contraseña actual es incorrecta");
        }
        if (datos.nueva() == null || datos.nueva().length() < 4) {
            throw new PeticionInvalidaException("La contraseña debe tener mínimo 4 caracteres");
        }

        usuario.setContrasena(datos.nueva());
        usuarioRepository.save(usuario);
    }

    public UsuarioResponse subirAvatar(Long id, MultipartFile archivo) {
        Usuario usuario = buscar(id);
        String anterior = usuario.getAvatar();

        usuario.setAvatar(almacenamientoService.guardar(archivo));
        Usuario guardado = usuarioRepository.save(usuario);

        almacenamientoService.eliminar(anterior);
        return aRespuesta(guardado);
    }

    public byte[] obtenerAvatar(Long id) {
        Usuario usuario = buscar(id);
        if (usuario.getAvatar() == null) {
            throw new RecursoNoEncontradoException("El usuario no tiene foto de perfil");
        }
        return almacenamientoService.leer(usuario.getAvatar());
    }

    public void eliminar(Long id) {
        Usuario usuario = buscar(id);
        usuarioRepository.delete(usuario);
        almacenamientoService.eliminar(usuario.getAvatar());
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID: " + id));
    }

    private void validarRol(Long rolId) {
        if (rolClient.obtener(rolId) == null) {
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
