package com.RincondelSur.notificaciones.controller;

import com.RincondelSur.notificaciones.dto.NotificacionClaveRequest;
import com.RincondelSur.notificaciones.service.CorreoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final CorreoService correoService;

    public NotificacionController(CorreoService correoService) {
        this.correoService = correoService;
    }

    @PostMapping("/clave-temporal")
    public ResponseEntity<?> enviarClaveTemporal(@Valid @RequestBody NotificacionClaveRequest request) {
        try {
            correoService.enviarClaveTemporal(request);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Correo enviado correctamente a " + request.getDestinatario()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", "No se pudo enviar el correo: " + e.getMessage()
            ));
        }
    }
}