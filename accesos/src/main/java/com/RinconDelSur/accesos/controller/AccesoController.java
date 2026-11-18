package com.RinconDelSur.accesos.controller;

import com.RinconDelSur.accesos.dto.AccesoRequestDTO;
import com.RinconDelSur.accesos.dto.AccesoResponseDTO;
import com.RinconDelSur.accesos.service.AccesoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accesos")
@RequiredArgsConstructor
@Tag(
        name = "Accesos",
        description = "Endpoints para la gestión y control de estado de las puertas de acceso físico (A1 a A4) en Rincón del Sur"
)
public class AccesoController {

    private static final Logger logger = LoggerFactory.getLogger(AccesoController.class);
    private final AccesoService accesoService;

    @Operation(
            summary = "Listar todos los puntos de acceso",
            description = "Obtiene el listado completo de las puertas de acceso configuradas en la tienda, incluyendo habilitadas y bloqueadas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de puntos de acceso obtenido correctamente",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AccesoResponseDTO.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<AccesoResponseDTO>> listarTodos() {
        logger.info("GET /api/accesos - Solicitando listado completo de puntos de acceso");
        return ResponseEntity.ok(accesoService.listarTodos());
    }

    @Operation(
            summary = "Listar puntos de acceso habilitados",
            description = "Obtiene únicamente las puertas de acceso activas/abiertas disponibles para registrar conteo de personas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Listado de puntos de acceso habilitados obtenido correctamente",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = AccesoResponseDTO.class))
            )
    )
    @GetMapping("/habilitados")
    public ResponseEntity<List<AccesoResponseDTO>> listarHabilitados() {
        logger.info("GET /api/accesos/habilitados - Solicitando puertas de acceso habilitadas");
        return ResponseEntity.ok(accesoService.listarHabilitados());
    }

    @Operation(
            summary = "Obtener punto de acceso por ID",
            description = "Busca y retorna la información de una puerta de acceso específica según su identificador único."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Punto de acceso encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccesoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró un punto de acceso con el ID especificado",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<AccesoResponseDTO> obtenerPorId(
            @Parameter(description = "ID único del punto de acceso a consultar", example = "1")
            @PathVariable Long id) {
        logger.info("GET /api/accesos/{} - Buscando punto de acceso por ID", id);
        return ResponseEntity.ok(accesoService.obtenerPorId(id));
    }

    @Operation(
            summary = "Crear un nuevo punto de acceso",
            description = "Registra una nueva puerta de acceso en el catálogo de la tienda validando que el código no esté duplicado."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Punto de acceso creado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccesoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos o código de acceso duplicado",
                    content = @Content
            )
    })
    @PostMapping
    public ResponseEntity<AccesoResponseDTO> crear(
            @Valid @RequestBody AccesoRequestDTO request) {
        logger.info("POST /api/accesos - Creando nuevo punto de acceso con código: {}", request.getCodigo());
        return ResponseEntity.status(HttpStatus.CREATED).body(accesoService.crear(request));
    }

    @Operation(
            summary = "Actualizar un punto de acceso existente",
            description = "Modifica el código, nombre, descripción o estado de habilitación de una puerta de acceso según su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Punto de acceso actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccesoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el punto de acceso que se intenta actualizar",
                    content = @Content
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<AccesoResponseDTO> actualizar(
            @Parameter(description = "ID único del punto de acceso a actualizar", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody AccesoRequestDTO request) {
        logger.info("PUT /api/accesos/{} - Actualizando punto de acceso", id);
        return ResponseEntity.ok(accesoService.actualizar(id, request));
    }

    @Operation(
            summary = "Habilitar o bloquear un punto de acceso",
            description = "Cambia rápidamente el estado de disponibilidad (estaHabilitado) de una puerta sin necesidad de enviar toda la entidad."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estado del punto de acceso actualizado correctamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccesoResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se encontró el punto de acceso con el ID especificado",
                    content = @Content
            )
    })
    @PatchMapping("/{id}/estado")
    public ResponseEntity<AccesoResponseDTO> cambiarEstado(
            @Parameter(description = "ID único del punto de acceso", example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado de habilitación (true = abierto, false = bloqueado)", example = "false")
            @RequestParam boolean habilitado) {
        logger.info("PATCH /api/accesos/{}/estado - Cambiando estado a habilitado={}", id, habilitado);
        return ResponseEntity.ok(accesoService.cambiarEstado(id, habilitado));
    }
}