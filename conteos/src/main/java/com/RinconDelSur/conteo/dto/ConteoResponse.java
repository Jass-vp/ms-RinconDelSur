package com.RinconDelSur.conteo.dto;

public record ConteoResponse(
        Long id,
        String puertaAcceso,
        String puertaNombre,
        Integer cantidadTransito,
        Integer cantidadClientes,
        String rangoEtario,
        String compania,
        String observador,
        String hora,
        Long usuarioId
) {
}
