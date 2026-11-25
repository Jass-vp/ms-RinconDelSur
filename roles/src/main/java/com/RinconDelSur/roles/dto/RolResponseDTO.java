package com.RinconDelSur.roles.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Datos de salida de un rol")
public class RolResponseDTO {

    @Schema(description = "Identificador único del rol", example = "1")
    private Long id;

    @Schema(description = "Código único del perfil", example = "P-01")
    private String nombre;

    @Schema(description = "Descripción del cargo o nivel de acceso", example = "Jefa de Tienda")
    private String descripcion;

    @Schema(description = "Indica si el rol está activo", example = "true")
    private Boolean habilitado;
}