package com.RinconDelSur.conteo.controller;

import com.RinconDelSur.conteo.dto.ConteoRequest;
import com.RinconDelSur.conteo.dto.ConteoResponse;
import com.RinconDelSur.conteo.dto.DatoGraficoResponse;
import com.RinconDelSur.conteo.service.ConteoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/conteos")
@RequiredArgsConstructor
public class ConteoController {

    private final ConteoService conteoService;

    @GetMapping
    public List<ConteoResponse> listar() {
        return conteoService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConteoResponse registrar(@RequestBody ConteoRequest datos) {
        return conteoService.registrar(datos);
    }

    @GetMapping("/grafico-hora")
    public List<DatoGraficoResponse> obtenerFlujoPorHora() {
        return conteoService.obtenerFlujoPorHora();
    }

    @GetMapping("/grafico-segmentacion")
    public List<DatoGraficoResponse> obtenerSegmentacion() {
        return conteoService.obtenerSegmentacion();
    }

    @GetMapping("/grafico-turno")
    public List<DatoGraficoResponse> obtenerFlujoPorTurno() {
        return conteoService.obtenerFlujoPorTurno();
    }
}
