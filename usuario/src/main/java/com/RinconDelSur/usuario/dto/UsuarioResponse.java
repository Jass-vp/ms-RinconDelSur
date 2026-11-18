package com.RinconDelSur.usuario.dto;

public record UsuarioResponse(
        Long id,
        String rut,
        String nombre,
        String correo,
        String telefono,
        String cargo,
        String turno,
        Long rolId,
        String rolNombre,
        Boolean activo,
        Boolean primerIngreso,
        String avatarUrl
) {
}
