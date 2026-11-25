package com.RinconDelSur.roles.controller;

import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.service.RolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", 
description = "Endpoints para la consulta y actualización de perfiles de acceso (P-01 a P-04) en Rincón del Sur")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);
    private final RolService rolService;

    @Operation(
            summary = "Listar todos los roles",
            description = "Obtiene el listado completo de roles configurados en el sistema."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de roles obtenido correctamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))
    )
    @GetMapping
    public List<Rol> listar() {
        logger.info("GET /api/roles - Solicitando listado de roles");
        return rolService.listar();
    }

    @Operation(
            summary = "Obtener rol por ID",
            description = "Busca y retorna la información de un rol específico según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró un rol con el ID especificado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public Rol obtener(
            @Parameter(description = "ID único del rol a consultar", example = "1")
            @PathVariable Long id) {
        logger.info("GET /api/roles/{} - Buscando rol por ID", id);
        return rolService.obtener(id);
    }

    @Operation(
            summary = "Actualizar un rol existente",
            description = "Modifica el nombre, descripción o estado de habilitación de un rol según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Rol actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Rol.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el rol que se intenta actualizar",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public Rol actualizar(
            @Parameter(description = "ID único del rol a actualizar", example = "1")
            @PathVariable Long id,
            @RequestBody Rol datos) {
        logger.info("PUT /api/roles/{} - Actualizando rol", id);
        return rolService.actualizar(id, datos);
    }
}