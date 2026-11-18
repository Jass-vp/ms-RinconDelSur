package com.RinconDelSur.registros_conteos.dto;
public record PuertaDto(
        String id,
        String nombre,
        String ubicacion,
        String descripcion,
        Boolean habilitada,
        Boolean modoOfflinePrioritario
) {
}
