package com.RinconDelSur.usuario.controller;

import com.RinconDelSur.usuario.dto.ActualizarUsuarioRequest;
import com.RinconDelSur.usuario.dto.CambiarContrasenaRequest;
import com.RinconDelSur.usuario.dto.LoginRequest;
import com.RinconDelSur.usuario.dto.PrimerIngresoRequest;
import com.RinconDelSur.usuario.dto.RecuperarPreguntaRequest;
import com.RinconDelSur.usuario.dto.RegistroRequest;
import com.RinconDelSur.usuario.dto.RestablecerContrasenaRequest;
import com.RinconDelSur.usuario.dto.UsuarioResponse;
import com.RinconDelSur.usuario.dto.VerificarRespuestaRequest;
import com.RinconDelSur.usuario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "Gestión de cuentas, autenticación, recuperación de acceso y avatares en Tienda Rincón del Sur")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene todos los usuarios ordenados alfabéticamente, con filtro opcional por nombre o correo")
    @ApiResponse(responseCode = "200", description = "Listado de usuarios obtenido correctamente")
    public List<UsuarioResponse> listar(
            @Parameter(description = "Texto opcional para filtrar por nombre o correo electrónico", example = "Camila")
            @RequestParam(required = false) String buscar) {
        log.debug("GET /api/usuarios - buscar: '{}'", buscar);
        return usuarioService.listar(buscar);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna el detalle completo de un usuario registrado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado con el ID especificado")
    })
    public UsuarioResponse obtener(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/usuarios/{}", id);
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar nuevo usuario", description = "Crea una cuenta nueva en el sistema asignando rol por defecto si no se especifica")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o rol inexistente"),
            @ApiResponse(responseCode = "409", description = "El correo electrónico ya se encuentra registrado")
    })
    public UsuarioResponse registrar(@RequestBody RegistroRequest datos) {
        log.info("POST /api/usuarios - Registrando usuario con correo: {}", datos.correo());
        return usuarioService.registrar(datos);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales por correo o RUT y verifica que la cuenta se encuentre activa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas o cuenta inhabilitada")
    })
    public UsuarioResponse iniciarSesion(@RequestBody LoginRequest datos) {
        log.info("POST /api/usuarios/login - Identificador: {}", datos.correo());
        return usuarioService.iniciarSesion(datos);
    }

    @PutMapping("/{id}/primer-ingreso")
    @Operation(summary = "Completar primer ingreso", description = "Actualiza la contraseña inicial y configura la pregunta y respuesta de seguridad del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Configuración de primer ingreso completada"),
            @ApiResponse(responseCode = "400", description = "La nueva contraseña no cumple el largo mínimo"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UsuarioResponse completarPrimerIngreso(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id,
            @RequestBody PrimerIngresoRequest datos) {
        log.info("PUT /api/usuarios/{}/primer-ingreso", id);
        return usuarioService.completarPrimerIngreso(id, datos);
    }

    @GetMapping("/preguntas")
    @Operation(summary = "Listar preguntas de seguridad", description = "Obtiene el listado de preguntas de seguridad disponibles en el sistema")
    @ApiResponse(responseCode = "200", description = "Listado de preguntas obtenido correctamente")
    public List<String> listarPreguntasSeguridad() {
        log.debug("GET /api/usuarios/preguntas");
        return usuarioService.listarPreguntasSeguridad();
    }

    @PostMapping("/recuperar-pregunta")
    @Operation(summary = "Obtener pregunta de seguridad", description = "Recupera la pregunta de seguridad asociada al RUT o correo del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pregunta de seguridad obtenida"),
            @ApiResponse(responseCode = "400", description = "El usuario no tiene pregunta de seguridad configurada"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public Map<String, String> obtenerPreguntaSeguridad(@RequestBody RecuperarPreguntaRequest datos) {
        log.debug("POST /api/usuarios/recuperar-pregunta - RUT/Correo: {}", datos.rutOCorreo());
        String pregunta = usuarioService.obtenerPreguntaSeguridad(datos.rutOCorreo());
        return Map.of("pregunta", pregunta);
    }

    @PostMapping("/verificar-respuesta")
    @Operation(summary = "Verificar respuesta de seguridad", description = "Comprueba si la pregunta seleccionada y la respuesta ingresada coinciden con las registradas")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultado de la validación devuelto"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public Map<String, Boolean> verificarRespuesta(@RequestBody VerificarRespuestaRequest datos) {
        log.debug("POST /api/usuarios/verificar-respuesta - RUT/Correo: {}", datos.rutOCorreo());
        boolean valida = usuarioService.verificarRespuesta(
                datos.rutOCorreo(),
                datos.pregunta(),
                datos.respuesta()
        );
        return Map.of("valida", valida);
    }

    @PutMapping("/restablecer-contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Restablecer contraseña", description = "Asigna una nueva contraseña al usuario identificado por RUT o correo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña restablecida correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public void restablecerContrasena(@RequestBody RestablecerContrasenaRequest datos) {
        log.info("PUT /api/usuarios/restablecer-contrasena - RUT/Correo: {}", datos.rutOCorreo());
        usuarioService.restablecerContrasena(datos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario", description = "Modifica los datos de perfil, rol y estado activo de un usuario existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos obligatorios faltantes o rol inválido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "El correo ya pertenece a otro usuario")
    })
    public UsuarioResponse actualizar(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id,
            @RequestBody ActualizarUsuarioRequest datos) {
        log.info("PUT /api/usuarios/{}", id);
        return usuarioService.actualizar(id, datos);
    }

    @PutMapping("/{id}/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cambiar contraseña", description = "Permite al usuario cambiar su contraseña validando previamente la contraseña actual")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contraseña actualizada correctamente"),
            @ApiResponse(responseCode = "400", description = "La nueva contraseña no cumple el largo mínimo"),
            @ApiResponse(responseCode = "401", description = "La contraseña actual es incorrecta"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public void cambiarContrasena(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id,
            @RequestBody CambiarContrasenaRequest datos) {
        log.info("PUT /api/usuarios/{}/contrasena", id);
        usuarioService.cambiarContrasena(id, datos);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir avatar de usuario", description = "Carga y almacena una nueva imagen de perfil reemplazando la anterior")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Avatar actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Archivo vacío o formato MIME no válido"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public UsuarioResponse subirAvatar(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Archivo de imagen (JPEG/PNG)")
            @RequestParam("archivo") MultipartFile archivo) {
        log.info("POST /api/usuarios/{}/avatar", id);
        return usuarioService.subirAvatar(id, archivo);
    }

    @GetMapping("/{id}/avatar")
    @Operation(summary = "Obtener avatar de usuario", description = "Descarga los bytes de la imagen de perfil almacenada para el usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Imagen obtenida correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado o sin foto de perfil")
    })
    public ResponseEntity<byte[]> obtenerAvatar(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id) {
        log.debug("GET /api/usuarios/{}/avatar", id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(usuarioService.obtenerAvatar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar usuario", description = "Elimina el registro del usuario y borra su avatar asociado en disco")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public void eliminar(
            @Parameter(description = "Identificador único del usuario", example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/usuarios/{}", id);
        usuarioService.eliminar(id);
    }
}