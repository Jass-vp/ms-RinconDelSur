package com.RinconDelSur.conteo.controller;

import com.RinconDelSur.conteo.dto.ConteoRequest;
import com.RinconDelSur.conteo.dto.ConteoResponse;
import com.RinconDelSur.conteo.dto.DatoGraficoResponse;
import com.RinconDelSur.conteo.service.ConteoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/conteos")
@RequiredArgsConstructor
@Tag(name = "Conteos", description = "Gestión de registros de conteo de flujo de personas y métricas estadísticas")
public class ConteoController {

    private final ConteoService conteoService;

    @GetMapping
    @Operation(
            summary = "Listar todos los conteos",
            description = "Obtiene el historial completo de registros de conteo ordenados del más reciente al más antiguo, incluyendo el nombre de la puerta asociada."
    )
    @ApiResponse(responseCode = "200", description = "Lista de conteos obtenida correctamente")
    public List<ConteoResponse> listar() {
        log.info("GET /api/conteos - Solicitando listado de conteos");
        return conteoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Registrar un nuevo conteo",
            description = "Valida la puerta de acceso y el usuario en sus respectivos microservicios y guarda un nuevo registro de conteo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conteo registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos, puerta inexistente/deshabilitada o usuario no encontrado")
    })
    public ConteoResponse registrar(@RequestBody ConteoRequest datos) {
        log.info("POST /api/conteos - Registrando conteo en puerta: {}", datos.puertaAcceso());
        return conteoService.registrar(datos);
    }

    @GetMapping("/grafico-hora")
    @Operation(
            summary = "Obtener flujo de clientes por hora",
            description = "Agrupa y suma la cantidad de clientes efectivos por cada franja horaria registrada."
    )
    @ApiResponse(responseCode = "200", description = "Datos de flujo por hora generados correctamente")
    public List<DatoGraficoResponse> obtenerFlujoPorHora() {
        log.info("GET /api/conteos/grafico-hora - Consultando métricas por hora");
        return conteoService.obtenerFlujoPorHora();
    }

    @GetMapping("/grafico-segmentacion")
    @Operation(
            summary = "Obtener segmentación tránsito vs clientes",
            description = "Compara el total acumulado de personas que transitan por el pasillo frente a quienes ingresan como clientes."
    )
    @ApiResponse(responseCode = "200", description = "Datos de segmentación generados correctamente")
    public List<DatoGraficoResponse> obtenerSegmentacion() {
        log.info("GET /api/conteos/grafico-segmentacion - Consultando segmentación");
        return conteoService.obtenerSegmentacion();
    }

    @GetMapping("/grafico-turno")
    @Operation(
            summary = "Obtener flujo de clientes por turno",
            description = "Clasifica y suma la cantidad de clientes entre Turno Mañana (antes de las 14h) y Turno Tarde."
    )
    @ApiResponse(responseCode = "200", description = "Datos de flujo por turno generados correctamente")
    public List<DatoGraficoResponse> obtenerFlujoPorTurno() {
        log.info("GET /api/conteos/grafico-turno - Consultando métricas por turno");
        return conteoService.obtenerFlujoPorTurno();
    }
}