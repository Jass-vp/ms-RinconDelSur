package com.RincondelSur.notificaciones.service;

import com.RincondelSur.notificaciones.dto.NotificacionClaveRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class CorreoService {

    private static final Logger log = LoggerFactory.getLogger(CorreoService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    public CorreoService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarClaveTemporal(NotificacionClaveRequest request) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setFrom(remitente);
        mensaje.setTo(request.getDestinatario());

        boolean esRecuperacion = "RECUPERACION".equalsIgnoreCase(request.getTipo());

        if (esRecuperacion) {
            mensaje.setSubject("Tienda Rincón del Sur - Recuperación de contraseña");
            mensaje.setText(
                "Hola " + request.getNombreUsuario() + ",\n\n" +
                "Recibimos una solicitud para restablecer tu contraseña.\n" +
                "Tu clave temporal de acceso es: " + request.getClaveTemporal() + "\n\n" +
                "Ingresa con esta clave en la aplicación y el sistema te pedirá crear una nueva contraseña.\n" +
                "Si no solicitaste este cambio, contacta a Administración."
            );
        } else {
            mensaje.setSubject("Bienvenido/a a Tienda Rincón del Sur - Tu clave de acceso");
            mensaje.setText(
                "Hola " + request.getNombreUsuario() + ",\n\n" +
                "Tu cuenta ha sido creada exitosamente en Tienda Rincón del Sur.\n" +
                "Tu clave temporal de ingreso es: " + request.getClaveTemporal() + "\n\n" +
                "Por seguridad, deberás cambiar esta contraseña en tu primer inicio de sesión."
            );
        }

        mailSender.send(mensaje);
        log.info("Correo enviado correctamente a {}", request.getDestinatario());
    }
}