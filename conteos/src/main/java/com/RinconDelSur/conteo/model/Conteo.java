package com.RinconDelSur.conteo.model;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Entidad que representa un registro de conteo de flujo en base de datos")
public class Conteo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único autogenerado del conteo", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "puerta_acceso", nullable = false, length = 20)
    @Schema(description = "Código de la puerta de acceso donde se realizó el registro", example = "P1")
    private String puertaAcceso;

    @Column(name = "cantidad_transito", nullable = false)
    @Schema(description = "Cantidad total de personas que transitaron por el sector", example = "45")
    private Integer cantidadTransito;

    @Column(name = "cantidad_clientes", nullable = false)
    @Schema(description = "Cantidad de personas que ingresaron como clientes", example = "18")
    private Integer cantidadClientes;

    @Column(name = "rango_etario", nullable = false, length = 50)
    @Schema(description = "Rango de edad predominante observado", example = "25-35 años")
    private String rangoEtario;

    @Column(nullable = false, length = 50)
    @Schema(description = "Tipo de compañía con la que se desplaza el público", example = "En pareja")
    private String compania;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del observador que efectuó el registro", example = "Camila Soto")
    private String observador;

    @Column(nullable = false, length = 20)
    @Schema(description = "Hora del registro en formato HH:mm", example = "16:30")
    private String hora;

    @Column(name = "usuario_id")
    @Schema(description = "ID del usuario que creó el registro", example = "1")
    private Long usuarioId;
}