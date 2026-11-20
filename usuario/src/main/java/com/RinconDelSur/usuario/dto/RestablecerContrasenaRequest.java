package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para restablecer la contraseña de un usuario tras validar su identidad")
public record RestablecerContrasenaRequest(

        @Schema(description = "RUT o correo electrónico registrado del usuario", example = "18.456.789-2")
        String rutOCorreo,

        @Schema(description = "Nueva contraseña que reemplazará a la anterior", example = "NuevaClave2026!")
        String nuevaContrasena
) {
}