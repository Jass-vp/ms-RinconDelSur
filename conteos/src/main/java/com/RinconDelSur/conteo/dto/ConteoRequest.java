package com.RinconDelSur.conteo.dto;

public record ConteoRequest(
        String puertaAcceso,
        Integer cantidadTransito,
        Integer cantidadClientes,
        String rangoEtario,
        String compania,
        String observador,
        String hora,
        Long usuarioId
) {
}
