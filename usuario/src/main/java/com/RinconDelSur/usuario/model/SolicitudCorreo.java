package com.RinconDelSur.usuario.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitudes_correo")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "Entidad que representa una solicitud de cambio o recuperación de correo electrónico de un usuario")
public class SolicitudCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único de la solicitud", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false)
    @Schema(description = "RUT del usuario que realiza la solicitud", example = "18.456.789-2")
    private String rutUsuario;

    @Column(nullable = false)
    @Schema(description = "Nombre completo del usuario solicitante", example = "Camila Soto")
    private String nombreUsuario;

    @Column(nullable = false)
    @Schema(description = "Correo electrónico registrado actualmente en la cuenta", example = "camila.antiguo@rincondelsur.cl")
    private String correoActual;

    @Column(nullable = false)
    @Schema(description = "Nuevo correo electrónico solicitado para vincular a la cuenta", example = "camila.soto@rincondelsur.cl")
    private String correoNuevo;

    @Column(nullable = false)
    @Schema(description = "Motivo o justificación de la solicitud de cambio", example = "Cambio de correo institucional por actualización de área")
    private String motivo;

    @Column(nullable = false)
    @Schema(description = "Estado actual de la solicitud (PENDIENTE, APROBADA, RECHAZADA)", example = "PENDIENTE")
    private String estado = "PENDIENTE";
}