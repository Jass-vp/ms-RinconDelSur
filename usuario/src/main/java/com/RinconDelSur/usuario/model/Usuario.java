package com.RinconDelSur.usuario.model;


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
public class Usuario {

   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String rut;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(length = 20)
    private String telefono;

    @Column(nullable = false, length = 100)
    private String contrasena;

    // ⚠️ ATRIBUTOS FALTANTES QUE CAUSAN EL ERROR:
    @Column(length = 100)
    private String cargo;

    @Column(length = 50)
    private String turno;

    @Column(name = "rol_id", nullable = false)
    private Long rolId;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;

    @Builder.Default
    @Column(name = "primer_ingreso", nullable = false)
    private Boolean primerIngreso = true;

    @Column(name = "pregunta_seguridad", length = 150)
    private String preguntaSeguridad;

    @Column(name = "respuesta_seguridad", length = 100)
    private String respuestaSeguridad;

    @Column(length = 100)
    private String avatar;

    public Boolean getActivo() {
        return activo != null ? activo : true;
    }

    public Boolean getPrimerIngreso() {
        return primerIngreso != null ? primerIngreso : false;
    }
}
