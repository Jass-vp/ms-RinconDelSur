package com.RinconDelSur.usuario.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Representación pública de los datos de un usuario del sistema")
public record UsuarioResponse(

        @Schema(description = "Identificador único del usuario", example = "1")
        Long id,

        @Schema(description = "RUT del usuario con guión y dígito verificador", example = "18.456.789-2")
        String rut,

        @Schema(description = "Nombre completo del usuario", example = "Camila Soto")
        String nombre,

        @Schema(description = "Correo electrónico registrado del usuario", example = "camila.soto@rincondelsur.cl")
        String correo,

        @Schema(description = "Teléfono de contacto del usuario", example = "+56987654321")
        String telefono,

        @Schema(description = "Cargo o función operativa dentro del local", example = "Analista de Flujo")
        String cargo,

        @Schema(description = "Turno laboral asignado", example = "Mañana (08:00 - 16:00)")
        String turno,

        @Schema(description = "Identificador único del rol asignado", example = "2")
        Long rolId,

        @Schema(description = "Nombre descriptivo del rol asignado", example = "OPERADOR")
        String rolNombre,

        @Schema(description = "Indica si la cuenta del usuario está activa", example = "true")
        Boolean activo,

        @Schema(description = "Indica si el usuario aún debe completar su configuración de primer ingreso", example = "false")
        Boolean primerIngreso,

        @Schema(description = "URL de la imagen de perfil o avatar del usuario", example = "https://cdn.rincondelsur.cl/avatars/user-1.png")
        String avatarUrl
) {
}