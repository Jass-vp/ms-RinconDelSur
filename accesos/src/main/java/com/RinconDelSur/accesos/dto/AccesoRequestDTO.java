package com.RinconDelSur.accesos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para crear o actualizar un punto de acceso")
public class AccesoRequestDTO {

    @NotBlank(message = "El código de la puerta es obligatorio")
    @Size(max = 20, message = "El código no puede superar los 20 caracteres")
    @Schema(description = "Código único de la puerta", example = "A1")
    private String codigo;

    @NotBlank(message = "El nombre del acceso es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    @Schema(description = "Nombre del acceso", example = "Acceso Principal")
    private String nombre;

    @NotBlank(message = "La descripción o ubicación es obligatoria")
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Schema(description = "Ubicación física de la puerta", example = "Patio de comidas")
    private String descripcion;

    @Builder.Default
    @Schema(description = "Estado de habilitación de la puerta", example = "true")
    private Boolean estaHabilitado = true;
}
