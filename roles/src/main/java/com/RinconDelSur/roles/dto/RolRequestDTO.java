package com.RinconDelSur.roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de entrada para crear o actualizar un rol")
public class RolRequestDTO {

    @NotBlank(message = "El nombre del rol es obligatorio")
    @Size(max = 50, message = "El nombre no puede superar los 50 caracteres")
    @Schema(description = "Código único del perfil (P-01 a P-04)", example = "P-01")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Schema(description = "Descripción del cargo o nivel de acceso", example = "Jefa de Tienda")
    private String descripcion;

    @Builder.Default
    @Schema(description = "Indica si el rol está activo en el sistema", example = "true")
    private Boolean habilitado = true;
}