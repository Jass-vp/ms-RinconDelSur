package com.RinconDelSur.usuario.controller;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.service.SolicitudCorreoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/solicitudes-correo")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Solicitudes de Correo", description = "Gestión de solicitudes de cambio o recuperación de correo electrónico de usuarios")
public class SolicitudCorreoController {

    private final SolicitudCorreoService solicitudCorreoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear solicitud de cambio de correo", description = "Registra una nueva solicitud de cambio de correo electrónico en estado PENDIENTE")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Solicitud creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de solicitud inválidos")
    })
    public SolicitudCorreoResponse crear(@RequestBody SolicitudCorreoRequest datos) {
        log.info("POST /api/solicitudes-correo - Creando solicitud para RUT: {}", datos.rutUsuario());
        return solicitudCorreoService.crear(datos);
    }

    @GetMapping("/pendientes")
    @Operation(summary = "Listar solicitudes pendientes", description = "Obtiene todas las solicitudes de cambio de correo en estado PENDIENTE ordenadas de forma descendente")
    @ApiResponse(responseCode = "200", description = "Listado obtenido correctamente")
    public List<SolicitudCorreoResponse> listarPendientes() {
        log.debug("GET /api/solicitudes-correo/pendientes");
        return solicitudCorreoService.listarPendientes();
    }

    @GetMapping("/pendientes/{rut}")
    @Operation(summary = "Obtener solicitud pendiente por RUT", description = "Busca la última solicitud pendiente de cambio de correo asociada a un RUT específico")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Solicitud pendiente encontrada"),
            @ApiResponse(responseCode = "404", description = "No existe ninguna solicitud pendiente para el RUT indicado")
    })
    public SolicitudCorreoResponse obtenerPendientePorRut(
            @Parameter(description = "RUT del usuario a consultar", example = "18.456.789-2") @PathVariable String rut) {
        log.debug("GET /api/solicitudes-correo/pendientes/{}", rut);
        return solicitudCorreoService.obtenerPendientePorRut(rut);
    }

    @PutMapping("/{id}/resolver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Resolver solicitud de cambio de correo", description = "Aprueba o rechaza una solicitud pendiente; si se aprueba, actualiza el correo del usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Solicitud resuelta correctamente"),
            @ApiResponse(responseCode = "404", description = "Solicitud no encontrada con el ID especificado")
    })
    public void resolver(
            @Parameter(description = "Identificador único de la solicitud", example = "1") @PathVariable Long id,
            @Parameter(description = "Indica si la solicitud es aprobada (true) o rechazada (false)", example = "true") @RequestParam boolean aprobar) {
        log.info("PUT /api/solicitudes-correo/{}/resolver - aprobar: {}", id, aprobar);
        solicitudCorreoService.resolver(id, aprobar);
    }
}