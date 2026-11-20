package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para registrar un nuevo usuario en el sistema")
public record RegistroRequest(

        @Schema(description = "RUT del usuario con guión y dígito verificador", example = "18.456.789-2")
        String rut,

        @Schema(description = "Nombre completo del usuario", example = "Camila Soto")
        String nombre,

        @Schema(description = "Correo electrónico institucional o personal", example = "camila.soto@rincondelsur.cl")
        String correo,

        @Schema(description = "Teléfono de contacto del usuario", example = "+56987654321")
        String telefono,

        @Schema(description = "Contraseña inicial de acceso", example = "ClaveTemporal123!")
        String contrasena,

        @Schema(description = "Cargo o rol operativo dentro del local", example = "Analista de Flujo")
        String cargo,

        @Schema(description = "Turno laboral asignado", example = "Mañana (08:00 - 16:00)")
        String turno,

        @Schema(description = "Identificador único del rol asignado al usuario", example = "2")
        Long rolId,

        @Schema(description = "Pregunta de seguridad para recuperación de cuenta", example = "¿Cuál es el nombre de tu primera mascota?")
        String preguntaSeguridad,

        @Schema(description = "Respuesta a la pregunta de seguridad", example = "Toby")
        String respuestaSeguridad
) {
}