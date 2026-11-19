package com.RinconDelSur.conteo.dto;

public record PuertaDto(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        Boolean estaHabilitado
) {
}