package com.RinconDelSur.usuario.dto;

public record SolicitudCorreoResponse(
        Long id,
        String rutUsuario,
        String nombreUsuario,
        String correoActual,
        String correoNuevo,
        String motivo,
        String estado
) {}