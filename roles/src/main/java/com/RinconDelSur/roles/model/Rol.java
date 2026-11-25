package com.RinconDelSur.roles.model;

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
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa un rol o perfil de usuario en Tienda Rincón del Sur")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del rol", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Código único del perfil (P-01 a P-04)", example = "P-01")
    private String nombre;

    @Column(length = 255)
    @Schema(description = "Descripción del cargo o nivel de acceso", example = "Jefa de Tienda")
    private String descripcion;

    @Builder.Default
    @Column(nullable = false)
    @Schema(description = "Indica si el rol está activo en el sistema", example = "true")
    private Boolean habilitado = true;
}