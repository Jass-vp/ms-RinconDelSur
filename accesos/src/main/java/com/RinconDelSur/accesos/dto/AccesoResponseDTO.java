package com.RinconDelSur.accesos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de salida de un punto de acceso")
public class AccesoResponseDTO {

    @Schema(description = "ID interno", example = "1")
    private Long id;

    @Schema(description = "Código de la puerta (A1..A4)", example = "A1")
    private String codigo;

    @Schema(description = "Nombre de la puerta", example = "Acceso Principal")
    private String nombre;

    @Schema(description = "Ubicación física", example = "Patio de comidas")
    private String descripcion;

    @Schema(description = "Indica si está habilitada", example = "true")
    private Boolean estaHabilitado;
}