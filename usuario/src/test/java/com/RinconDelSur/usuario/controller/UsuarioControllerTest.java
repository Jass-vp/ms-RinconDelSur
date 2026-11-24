package com.RinconDelSur.usuario.controller;

import com.RinconDelSur.usuario.dto.*;
import com.RinconDelSur.usuario.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @Test
    void listar_debeRetornarListaDeUsuarios() {
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.listar("Camila")).thenReturn(List.of(response));

        List<UsuarioResponse> resultado = usuarioController.listar("Camila");

        assertEquals(1, resultado.size());
        verify(usuarioService).listar("Camila");
    }

    @Test
    void obtener_debeRetornarUsuario() {
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.obtener(1L)).thenReturn(response);

        UsuarioResponse resultado = usuarioController.obtener(1L);

        assertNotNull(resultado);
        verify(usuarioService).obtener(1L);
    }

    @Test
    void registrar_debeRetornarUsuarioCreado() {
        RegistroRequest request = mock(RegistroRequest.class);
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.registrar(any(RegistroRequest.class))).thenReturn(response);

        UsuarioResponse resultado = usuarioController.registrar(request);

        assertNotNull(resultado);
        verify(usuarioService).registrar(request);
    }

    @Test
    void iniciarSesion_debeRetornarUsuario() {
        LoginRequest request = new LoginRequest("camila.soto@rincondelsur.cl", "ClaveSegura123!");
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.iniciarSesion(any(LoginRequest.class))).thenReturn(response);

        UsuarioResponse resultado = usuarioController.iniciarSesion(request);

        assertNotNull(resultado);
        verify(usuarioService).iniciarSesion(request);
    }

    @Test
    void completarPrimerIngreso_debeRetornarUsuarioActualizado() {
        PrimerIngresoRequest request = mock(PrimerIngresoRequest.class);
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.completarPrimerIngreso(eq(1L), any(PrimerIngresoRequest.class))).thenReturn(response);

        UsuarioResponse resultado = usuarioController.completarPrimerIngreso(1L, request);

        assertNotNull(resultado);
        verify(usuarioService).completarPrimerIngreso(1L, request);
    }

    @Test
    void listarPreguntasSeguridad_debeRetornarLista() {
        when(usuarioService.listarPreguntasSeguridad()).thenReturn(List.of("¿Mascota?", "¿Ciudad?"));

        List<String> resultado = usuarioController.listarPreguntasSeguridad();

        assertEquals(2, resultado.size());
        verify(usuarioService).listarPreguntasSeguridad();
    }

    @Test
    void obtenerPreguntaSeguridad_debeRetornarMapaConPregunta() {
        RecuperarPreguntaRequest request = new RecuperarPreguntaRequest("18.456.789-2");
        when(usuarioService.obtenerPreguntaSeguridad("18.456.789-2")).thenReturn("¿Nombre de tu mascota?");

        Map<String, String> resultado = usuarioController.obtenerPreguntaSeguridad(request);

        assertEquals("¿Nombre de tu mascota?", resultado.get("pregunta"));
        verify(usuarioService).obtenerPreguntaSeguridad("18.456.789-2");
    }

    @Test
    void verificarRespuesta_debeRetornarMapaConResultado() {
        VerificarRespuestaRequest request = new VerificarRespuestaRequest(
                "18.456.789-2",
                "¿Cuál es el nombre de tu primera mascota?",
                "Toby"
        );
        when(usuarioService.verificarRespuesta(
                "18.456.789-2",
                "¿Cuál es el nombre de tu primera mascota?",
                "Toby"
        )).thenReturn(true);

        Map<String, Boolean> resultado = usuarioController.verificarRespuesta(request);

        assertEquals(true, resultado.get("valida"));
        verify(usuarioService).verificarRespuesta(
                "18.456.789-2",
                "¿Cuál es el nombre de tu primera mascota?",
                "Toby"
        );
    }

    @Test
    void restablecerContrasena_debeInvocarServicio() {
        RestablecerContrasenaRequest request = mock(RestablecerContrasenaRequest.class);

        usuarioController.restablecerContrasena(request);

        verify(usuarioService).restablecerContrasena(request);
    }

    @Test
    void recuperarPorCorreo_debeRetornarMensaje() {
        RecuperarPreguntaRequest request = new RecuperarPreguntaRequest("18.456.789-2");

        Map<String, String> resultado = usuarioController.recuperarPorCorreo(request);

        assertEquals("Se ha enviado una clave temporal a tu correo registrado", resultado.get("mensaje"));
        verify(usuarioService).enviarClaveTemporalRecuperacion("18.456.789-2");
    }

    @Test
    void actualizar_debeRetornarUsuarioActualizado() {
        ActualizarUsuarioRequest request = mock(ActualizarUsuarioRequest.class);
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.actualizar(eq(1L), any(ActualizarUsuarioRequest.class))).thenReturn(response);

        UsuarioResponse resultado = usuarioController.actualizar(1L, request);

        assertNotNull(resultado);
        verify(usuarioService).actualizar(1L, request);
    }

    @Test
    void cambiarContrasena_debeInvocarServicio() {
        CambiarContrasenaRequest request = mock(CambiarContrasenaRequest.class);

        usuarioController.cambiarContrasena(1L, request);

        verify(usuarioService).cambiarContrasena(1L, request);
    }

    @Test
    void subirAvatar_debeRetornarUsuarioActualizado() {
        MultipartFile archivo = mock(MultipartFile.class);
        UsuarioResponse response = mock(UsuarioResponse.class);
        when(usuarioService.subirAvatar(1L, archivo)).thenReturn(response);

        UsuarioResponse resultado = usuarioController.subirAvatar(1L, archivo);

        assertNotNull(resultado);
        verify(usuarioService).subirAvatar(1L, archivo);
    }

    @Test
    void obtenerAvatar_debeRetornarImagenJpeg() {
        byte[] imagenBytes = new byte[]{10, 20, 30};
        when(usuarioService.obtenerAvatar(1L)).thenReturn(imagenBytes);

        ResponseEntity<byte[]> resultado = usuarioController.obtenerAvatar(1L);

        assertEquals(HttpStatus.OK, resultado.getStatusCode());
        assertEquals(MediaType.IMAGE_JPEG, resultado.getHeaders().getContentType());
        assertArrayEquals(imagenBytes, resultado.getBody());
        verify(usuarioService).obtenerAvatar(1L);
    }

    @Test
    void eliminar_debeInvocarServicio() {
        usuarioController.eliminar(1L);

        verify(usuarioService).eliminar(1L);
    }
}