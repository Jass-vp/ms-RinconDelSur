package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para validar la pregunta y respuesta de seguridad de un usuario")
public record VerificarRespuestaRequest(

        @Schema(description = "RUT o correo electrónico registrado del usuario", example = "18.456.789-2")
        String rutOCorreo,

        @Schema(description = "Pregunta de seguridad seleccionada por el usuario", example = "¿Cuál es el nombre de tu primera mascota?")
        String pregunta,

        @Schema(description = "Respuesta proporcionada por el usuario a su pregunta de seguridad", example = "Toby")
        String respuesta
) {
}