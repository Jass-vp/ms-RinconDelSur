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
import lombok.RequiredArgsConstructor;
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
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioResponse> listar(@RequestParam(required = false) String buscar) {
        return usuarioService.listar(buscar);
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@RequestBody RegistroRequest datos) {
        return usuarioService.registrar(datos);
    }

    @PostMapping("/login")
    public UsuarioResponse iniciarSesion(@RequestBody LoginRequest datos) {
        return usuarioService.iniciarSesion(datos);
    }

    @PutMapping("/{id}/primer-ingreso")
    public UsuarioResponse completarPrimerIngreso(@PathVariable Long id, @RequestBody PrimerIngresoRequest datos) {
        return usuarioService.completarPrimerIngreso(id, datos);
    }

    @PostMapping("/recuperar-pregunta")
    public Map<String, String> obtenerPreguntaSeguridad(@RequestBody RecuperarPreguntaRequest datos) {
        String pregunta = usuarioService.obtenerPreguntaSeguridad(datos.rutOCorreo());
        return Map.of("pregunta", pregunta);
    }

    @PostMapping("/verificar-respuesta")
    public Map<String, Boolean> verificarRespuesta(@RequestBody VerificarRespuestaRequest datos) {
        boolean valida = usuarioService.verificarRespuesta(datos.rutOCorreo(), datos.respuesta());
        return Map.of("valida", valida);
    }

    @PutMapping("/restablecer-contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restablecerContrasena(@RequestBody RestablecerContrasenaRequest datos) {
        usuarioService.restablecerContrasena(datos);
    }

    @PutMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable Long id, @RequestBody ActualizarUsuarioRequest datos) {
        return usuarioService.actualizar(id, datos);
    }

    @PutMapping("/{id}/contrasena")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarContrasena(@PathVariable Long id, @RequestBody CambiarContrasenaRequest datos) {
        usuarioService.cambiarContrasena(id, datos);
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public UsuarioResponse subirAvatar(@PathVariable Long id, @RequestParam("archivo") MultipartFile archivo) {
        return usuarioService.subirAvatar(id, archivo);
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> obtenerAvatar(@PathVariable Long id) {
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(usuarioService.obtenerAvatar(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        usuarioService.eliminar(id);
    }
}
