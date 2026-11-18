package com.RinconDelSur.usuario.dto;

public record RolDto(
        Long id,
        String nombre,
        String descripcion,
        Boolean habilitado
) {
}
