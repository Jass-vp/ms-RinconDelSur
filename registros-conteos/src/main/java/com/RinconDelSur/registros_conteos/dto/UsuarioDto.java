package com.RinconDelSur.registros_conteos.dto;

public record UsuarioDto(
        Long id,
        String rut,
        String nombre,
        String correo,
        String telefono,
        String cargo,
        String turno
) {
}
