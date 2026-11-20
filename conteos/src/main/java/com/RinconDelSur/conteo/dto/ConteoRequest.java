package com.RinconDelSur.conteo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para registrar un nuevo conteo de flujo de personas y clientes")
public record ConteoRequest(

        @Schema(description = "Puerta o acceso del local donde se realiza el registro", example = "Acceso Principal")
        String puertaAcceso,

        @Schema(description = "Cantidad total de personas que transitan por el sector", example = "45")
        Integer cantidadTransito,

        @Schema(description = "Cantidad de personas que ingresan o actúan como clientes", example = "18")
        Integer cantidadClientes,

        @Schema(description = "Rango de edad predominante observado", example = "25-35 años")
        String rangoEtario,

        @Schema(description = "Tipo de compañía con la que se desplaza el público", example = "En pareja")
        String compania,

        @Schema(description = "Nombre o identificador del observador que registra el conteo", example = "Camila Soto")
        String observador,

        @Schema(description = "Hora del registro en formato HH:mm", example = "16:30")
        String hora,

        @Schema(description = "ID del usuario autenticado que crea el registro", example = "1")
        Long usuarioId
) {
}