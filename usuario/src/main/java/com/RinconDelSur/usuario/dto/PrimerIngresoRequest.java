package com.RinconDelSur.usuario.dto;

public record PrimerIngresoRequest(
        String nuevaContrasena,
        String preguntaSeguridad,
        String respuestaSeguridad
) {
}
