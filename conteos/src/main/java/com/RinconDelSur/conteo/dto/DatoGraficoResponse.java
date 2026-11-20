package com.RinconDelSur.conteo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Par etiqueta-valor utilizado para representar métricas en gráficos estadísticos")
public record DatoGraficoResponse(

        @Schema(description = "Nombre o categoría del eje (ej. puerta, rango etario u hora)", example = "Acceso Principal")
        String etiqueta,

        @Schema(description = "Valor numérico o cantidad acumulada asociada a la etiqueta", example = "120")
        Integer valor
) {
}