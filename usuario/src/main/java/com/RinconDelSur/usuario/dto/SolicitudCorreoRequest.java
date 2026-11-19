package com.RinconDelSur.usuario.dto;

public record SolicitudCorreoRequest(
        String rutUsuario,
        String nombreUsuario,
        String correoActual,
        String correoNuevo,
        String motivo
) {}