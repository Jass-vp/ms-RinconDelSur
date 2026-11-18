package com.RinconDelSur.usuario.dto;

public record RegistroRequest(
        String rut,
        String nombre,
        String correo,
        String telefono,
        String contrasena,
        String cargo,
        String turno,
        Long rolId,
        String preguntaSeguridad,
        String respuestaSeguridad
) {
}
