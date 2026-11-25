package com.RincondelSur.notificaciones.service;

import com.RincondelSur.notificaciones.dto.NotificacionClaveRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorreoServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private CorreoService correoService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(correoService, "remitente", "noreply@rincondelsur.cl");
    }

    @Test
    void enviarClaveTemporal_cuandoEsRecuperacion_debeEnviarCorreoRecuperacion() {
        NotificacionClaveRequest request = mock(NotificacionClaveRequest.class);
        when(request.getDestinatario()).thenReturn("camila@test.cl");
        when(request.getTipo()).thenReturn("RECUPERACION");
        when(request.getNombreUsuario()).thenReturn("Camila");
        when(request.getClaveTemporal()).thenReturn("TMP12345");

        correoService.enviarClaveTemporal(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage enviado = captor.getValue();
        assertEquals("noreply@rincondelsur.cl", enviado.getFrom());
        assertNotNull(enviado.getTo());
        assertEquals("camila@test.cl", enviado.getTo()[0]);
        assertEquals("Tienda Rincón del Sur - Recuperación de contraseña", enviado.getSubject());
        assertNotNull(enviado.getText());
        assertTrue(enviado.getText().contains("TMP12345"));
    }

    @Test
    void enviarClaveTemporal_cuandoEsBienvenida_debeEnviarCorreoBienvenida() {
        NotificacionClaveRequest request = mock(NotificacionClaveRequest.class);
        when(request.getDestinatario()).thenReturn("camila@test.cl");
        when(request.getTipo()).thenReturn("REGISTRO");
        when(request.getNombreUsuario()).thenReturn("Camila");
        when(request.getClaveTemporal()).thenReturn("TMP98765");

        correoService.enviarClaveTemporal(request);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage enviado = captor.getValue();
        assertEquals("Bienvenido/a a Tienda Rincón del Sur - Tu clave de acceso", enviado.getSubject());
        assertNotNull(enviado.getText());
        assertTrue(enviado.getText().contains("TMP98765"));
    }
}