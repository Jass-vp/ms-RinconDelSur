package com.RinconDelSur.conteo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el detalle completo de un registro de conteo almacenado")
public record ConteoResponse(

        @Schema(description = "Identificador único del conteo generado en base de datos", example = "10")
        Long id,

        @Schema(description = "Código o identificador de la puerta de acceso", example = "P1")
        String puertaAcceso,

        @Schema(description = "Nombre descriptivo de la puerta de acceso", example = "Acceso Principal")
        String puertaNombre,

        @Schema(description = "Cantidad total de personas que transitan por el sector", example = "45")
        Integer cantidadTransito,

        @Schema(description = "Cantidad de personas que ingresan o actúan como clientes", example = "18")
        Integer cantidadClientes,

        @Schema(description = "Rango de edad predominante observado", example = "25-35 años")
        String rangoEtario,

        @Schema(description = "Tipo de compañía con la que se desplaza el público", example = "En pareja")
        String compania,

        @Schema(description = "Nombre o identificador del observador que registró el conteo", example = "Camila Soto")
        String observador,

        @Schema(description = "Hora del registro en formato HH:mm", example = "16:30")
        String hora,

        @Schema(description = "ID del usuario que realizó el registro", example = "1")
        Long usuarioId
) {
}