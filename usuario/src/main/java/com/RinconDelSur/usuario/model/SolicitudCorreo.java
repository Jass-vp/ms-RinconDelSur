package com.RinconDelSur.usuario.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "solicitudes_correo")
@AllArgsConstructor 
@NoArgsConstructor 
@Data 
public class SolicitudCorreo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String rutUsuario;

    @Column(nullable = false)
    private String nombreUsuario;

    @Column(nullable = false)
    private String correoActual;

    @Column(nullable = false)
    private String correoNuevo;

    @Column(nullable = false)
    private String motivo;

    @Column(nullable = false)
    private String estado = "PENDIENTE"; 
}