package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para actualizar la contraseña de un usuario autenticado")
public record CambiarContrasenaRequest(

        @Schema(description = "Contraseña actual del usuario para verificación", example = "ClaveActual123")
        String actual,

        @Schema(description = "Nueva contraseña que se desea establecer", example = "NuevaClave456!")
        String nueva
) {
}