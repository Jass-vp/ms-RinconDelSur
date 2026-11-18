package com.RinconDelSur.registros_conteos.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String ruta
) {
}
