package com.RinconDelSur.registros_conteos.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conteos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Conteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "puerta_acceso", nullable = false, length = 20)
    private String puertaAcceso;

    @Column(name = "cantidad_transito", nullable = false)
    private Integer cantidadTransito;

    @Column(name = "cantidad_clientes", nullable = false)
    private Integer cantidadClientes;

    @Column(name = "rango_etario", nullable = false, length = 50)
    private String rangoEtario;

    @Column(nullable = false, length = 50)
    private String compania;

    @Column(nullable = false, length = 100)
    private String observador;

    @Column(nullable = false, length = 20)
    private String hora;

    @Column(name = "usuario_id")
    private Long usuarioId;
}
