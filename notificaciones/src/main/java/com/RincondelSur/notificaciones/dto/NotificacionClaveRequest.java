package com.RincondelSur.notificaciones.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NotificacionClaveRequest {

    @NotBlank(message = "El destinatario es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String destinatario;

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombreUsuario;

    @NotBlank(message = "La clave temporal es obligatoria")
    private String claveTemporal;

    // "BIENVENIDA" (al crear cuenta) o "RECUPERACION" (al olvidar clave)
    private String tipo = "BIENVENIDA";
}