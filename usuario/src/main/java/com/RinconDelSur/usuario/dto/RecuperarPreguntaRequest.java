package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Identificador del usuario (RUT o correo electrónico) para consultar su pregunta de seguridad")
public record RecuperarPreguntaRequest(

        @Schema(description = "RUT o correo electrónico registrado del usuario", example = "18.456.789-2")
        String rutOCorreo
) {
}