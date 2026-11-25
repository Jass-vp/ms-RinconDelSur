package com.RinconDelSur.accesos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accesos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa una puerta o punto de acceso físico de Tienda Rincón del Sur")
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador interno de base de datos", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "Código identificador de la puerta (A1 a A4)", example = "A1")
    private String codigo;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del acceso", example = "Acceso Principal")
    private String nombre;

    @Column(nullable = false, length = 255)
    @Schema(description = "Ubicación o referencia física de la puerta", example = "Patio de comidas")
    private String descripcion;

    @Builder.Default
    @Column(name = "esta_habilitado", nullable = false)
    @Schema(description = "Indica si la puerta está abierta/habilitada para conteo", example = "true")
    private Boolean estaHabilitado = true;
}