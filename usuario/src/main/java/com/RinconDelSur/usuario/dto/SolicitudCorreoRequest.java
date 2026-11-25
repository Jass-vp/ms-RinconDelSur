package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos requeridos para generar una solicitud de cambio o recuperación de correo electrónico")
public record SolicitudCorreoRequest(

        @Schema(description = "RUT del usuario que solicita el cambio", example = "18.456.789-2")
        String rutUsuario,

        @Schema(description = "Nombre completo del usuario solicitante", example = "Camila Soto")
        String nombreUsuario,

        @Schema(description = "Correo electrónico registrado actualmente en la cuenta", example = "camila.antiguo@rincondelsur.cl")
        String correoActual,

        @Schema(description = "Nuevo correo electrónico que se desea vincular a la cuenta", example = "camila.soto@rincondelsur.cl")
        String correoNuevo,

        @Schema(description = "Justificación o motivo por el cual se solicita el cambio de correo", example = "Cambio de correo institucional por actualización de área")
        String motivo
) {
        
}