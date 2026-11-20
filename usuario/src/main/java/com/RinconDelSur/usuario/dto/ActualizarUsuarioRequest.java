package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para actualizar la información de un usuario existente")
public record ActualizarUsuarioRequest(

        @Schema(description = "Nombre completo del usuario", example = "Camila Soto")
        String nombre,

        @Schema(description = "Correo electrónico institucional o personal", example = "camila.soto@rincondelsur.cl")
        String correo,

        @Schema(description = "Teléfono de contacto del usuario", example = "+56987654321")
        String telefono,

        @Schema(description = "Cargo o rol operativo dentro del local", example = "Analista de Flujo")
        String cargo,

        @Schema(description = "Turno laboral asignado", example = "Mañana (08:00 - 16:00)")
        String turno,

        @Schema(description = "Identificador único del rol asignado al usuario", example = "2")
        Long rolId,

        @Schema(description = "Indica si la cuenta del usuario se encuentra activa o suspendida", example = "true")
        Boolean activo,

        @Schema(description = "Pregunta de seguridad configurada para recuperación de acceso", example = "¿Cuál es el nombre de tu primera mascota?")
        String preguntaSeguridad,

        @Schema(description = "Respuesta a la pregunta de seguridad", example = "Toby")
        String respuestaSeguridad
) {
}