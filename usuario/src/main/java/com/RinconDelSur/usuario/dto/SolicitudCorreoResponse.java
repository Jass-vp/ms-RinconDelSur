package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta con el detalle y estado de una solicitud de cambio o recuperación de correo electrónico")
public record SolicitudCorreoResponse(

        @Schema(description = "Identificador único de la solicitud", example = "1")
        Long id,

        @Schema(description = "RUT del usuario que generó la solicitud", example = "18.456.789-2")
        String rutUsuario,

        @Schema(description = "Nombre completo del usuario solicitante", example = "Camila Soto")
        String nombreUsuario,

        @Schema(description = "Correo electrónico registrado anteriormente", example = "camila.antiguo@rincondelsur.cl")
        String correoActual,

        @Schema(description = "Nuevo correo electrónico solicitado", example = "camila.soto@rincondelsur.cl")
        String correoNuevo,

        @Schema(description = "Justificación ingresada para el cambio de correo", example = "Cambio de correo institucional por actualización de área")
        String motivo,

        @Schema(description = "Estado actual de la solicitud (PENDIENTE, APROBADA, RECHAZADA)", example = "PENDIENTE")
        String estado
) {
        
}