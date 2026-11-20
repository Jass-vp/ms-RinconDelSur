package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Credenciales requeridas para iniciar sesión en el sistema")
public record LoginRequest(

        @Schema(description = "Correo electrónico registrado del usuario", example = "camila.soto@rincondelsur.cl")
        String correo,

        @Schema(description = "Contraseña de acceso del usuario", example = "ClaveSegura123!")
        String contrasena
) {
}