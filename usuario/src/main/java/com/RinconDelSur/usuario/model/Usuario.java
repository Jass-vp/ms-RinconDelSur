package com.RinconDelSur.usuario.model;

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
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Entidad que representa a un usuario registrado en el sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador único del usuario", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    @Schema(description = "RUT único del usuario con guión y dígito verificador", example = "18.456.789-2")
    private String rut;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre completo del usuario", example = "Camila Soto")
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Correo electrónico único del usuario", example = "camila.soto@rincondelsur.cl")
    private String correo;

    @Column(length = 20)
    @Schema(description = "Teléfono de contacto del usuario", example = "+56987654321")
    private String telefono;

    @Column(nullable = false, length = 100)
    @Schema(description = "Contraseña encriptada del usuario", example = "$2a$10$...", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String contrasena;

    @Column(length = 100)
    @Schema(description = "Cargo o puesto operativo dentro del local", example = "Analista de Flujo")
    private String cargo;

    @Column(length = 50)
    @Schema(description = "Turno laboral asignado", example = "Mañana (08:00 - 16:00)")
    private String turno;

    @Column(name = "rol_id", nullable = false)
    @Schema(description = "Identificador del rol asignado al usuario", example = "2")
    private Long rolId;

    @Builder.Default
    @Column(nullable = false)
    @Schema(description = "Indica si la cuenta del usuario se encuentra activa", example = "true")
    private Boolean activo = true;

    @Builder.Default
    @Column(name = "primer_ingreso", nullable = false)
    @Schema(description = "Indica si el usuario debe completar la configuración de seguridad en su primer inicio de sesión", example = "true")
    private Boolean primerIngreso = true;

    @Column(name = "pregunta_seguridad", length = 150)
    @Schema(description = "Pregunta de seguridad configurada para recuperación de contraseña", example = "¿En qué ciudad naciste?")
    private String preguntaSeguridad;

    @Column(name = "respuesta_seguridad", length = 100)
    @Schema(description = "Respuesta secreta a la pregunta de seguridad", example = "Valdivia", accessMode = Schema.AccessMode.WRITE_ONLY)
    private String respuestaSeguridad;

    @Column(length = 100)
    @Schema(description = "Identificador o nombre de archivo del avatar del usuario", example = "avatar_01.png")
    private String avatar;

    public Boolean getActivo() {
        return activo != null ? activo : true;
    }

    public Boolean getPrimerIngreso() {
        return primerIngreso != null ? primerIngreso : false;
    }
}