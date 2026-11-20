package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación de un rol de usuario dentro del sistema")
public record RolDto(

        @Schema(description = "Identificador único del rol", example = "1")
        Long id,

        @Schema(description = "Nombre técnico o descriptivo del rol", example = "ADMINISTRADOR")
        String nombre,

        @Schema(description = "Descripción de los permisos y alcance del rol", example = "Acceso total a la gestión de usuarios, puertas y reportes")
        String descripcion,

        @Schema(description = "Indica si el rol se encuentra activo y disponible para asignación", example = "true")
        Boolean habilitado
) {
}