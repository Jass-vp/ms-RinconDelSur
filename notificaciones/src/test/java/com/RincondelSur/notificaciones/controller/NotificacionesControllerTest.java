package com.RincondelSur.notificaciones.controller;

import com.RincondelSur.notificaciones.dto.NotificacionClaveRequest;
import com.RincondelSur.notificaciones.service.CorreoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionControllerTest {

    @Mock
    private CorreoService correoService;

    @InjectMocks
    private NotificacionController notificacionController;

    @Test
    void enviarClaveTemporal_cuandoEsExitoso_debeRetornarOk() {
        NotificacionClaveRequest request = mock(NotificacionClaveRequest.class);
        when(request.getDestinatario()).thenReturn("camila@test.cl");
        doNothing().when(correoService).enviarClaveTemporal(request);

        ResponseEntity<?> respuesta = notificacionController.enviarClaveTemporal(request);

        assertEquals(HttpStatus.OK, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("Correo enviado correctamente a camila@test.cl", ((Map<?, ?>) respuesta.getBody()).get("mensaje"));
        verify(correoService).enviarClaveTemporal(request);
    }

    @Test
    void enviarClaveTemporal_cuandoFallaElServicio_debeRetornarInternalServerError() {
        NotificacionClaveRequest request = mock(NotificacionClaveRequest.class);
        doThrow(new RuntimeException("Error SMTP")).when(correoService).enviarClaveTemporal(request);

        ResponseEntity<?> respuesta = notificacionController.enviarClaveTemporal(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());
        assertNotNull(respuesta.getBody());
        assertEquals("No se pudo enviar el correo: Error SMTP", ((Map<?, ?>) respuesta.getBody()).get("error"));
        verify(correoService).enviarClaveTemporal(request);
    }
}