package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para completar la configuración inicial de seguridad en el primer inicio de sesión")
public record PrimerIngresoRequest(

        @Schema(description = "Nueva contraseña definitiva definida por el usuario", example = "MiClaveNueva2026!")
        String nuevaContrasena,

        @Schema(description = "Pregunta de seguridad elegida para futura recuperación de cuenta", example = "¿En qué ciudad naciste?")
        String preguntaSeguridad,

        @Schema(description = "Respuesta secreta asociada a la pregunta de seguridad", example = "Valdivia")
        String respuestaSeguridad
) {
}