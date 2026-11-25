package com.RinconDelSur.conteo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de una puerta o punto de acceso del local")
public record PuertaDto(

        @Schema(description = "Identificador único de la puerta", example = "1")
        Long id,

        @Schema(description = "Código corto identificador de la puerta", example = "P1")
        String codigo,

        @Schema(description = "Nombre descriptivo de la puerta de acceso", example = "Acceso Principal")
        String nombre,

        @Schema(description = "Detalle o ubicación física de la puerta", example = "Entrada norte frente al estacionamiento")
        String descripcion,

        @Schema(description = "Indica si la puerta se encuentra activa para registrar conteos", example = "true")
        Boolean estaHabilitado
) {
}