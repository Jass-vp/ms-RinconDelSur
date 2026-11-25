package com.RinconDelSur.conteo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos básicos de un usuario que participa en los registros de conteo")
public record UsuarioDto(

        @Schema(description = "Identificador único del usuario", example = "1")
        Long id,

        @Schema(description = "RUT del usuario con guión y dígito verificador", example = "18.456.789-2")
        String rut,

        @Schema(description = "Nombre completo del usuario", example = "Camila Soto")
        String nombre,

        @Schema(description = "Correo electrónico institucional o personal", example = "camila.soto@rincondelsur.cl")
        String correo,

        @Schema(description = "Teléfono de contacto del usuario", example = "+56987654321")
        String telefono,

        @Schema(description = "Cargo o rol dentro del local", example = "Analista de Flujo")
        String cargo,

        @Schema(description = "Turno laboral asignado", example = "Mañana (08:00 - 16:00)")
        String turno
) {
}