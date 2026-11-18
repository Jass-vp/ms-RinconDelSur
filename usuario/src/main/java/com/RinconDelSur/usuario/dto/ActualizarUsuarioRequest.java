package com.RinconDelSur.usuario.dto;

public record ActualizarUsuarioRequest(
        String nombre,
        String correo,
        String telefono,
        String cargo,
        String turno,
        Long rolId,
        Boolean activo,
        String preguntaSeguridad,
        String respuestaSeguridad
) {
}
