package com.RinconDelSur.usuario.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@Slf4j
public class NotificacionClient {

    private final WebClient webClient;

    public NotificacionClient(
            @Value("${notificaciones.url:http://localhost:8085}") String notificacionesUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(notificacionesUrl)
                .build();
    }

    public void enviarClaveTemporal(String destinatario, String nombreUsuario, String claveTemporal, String tipo) {
        Map<String, String> payload = Map.of(
                "destinatario", destinatario,
                "nombreUsuario", nombreUsuario,
                "claveTemporal", claveTemporal,
                "tipo", tipo
        );

        try {
            webClient.post()
                    .uri("/api/notificaciones/clave-temporal")
                    .bodyValue(payload)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Correo de clave temporal ({}) enviado a {}", tipo, destinatario);
        } catch (Exception e) {
            log.error("No se pudo enviar el correo a {}: {}", destinatario, e.getMessage());
            throw new RuntimeException("El usuario fue creado, pero falló el envío del correo con su clave temporal.");
        }
    }
}