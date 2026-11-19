package com.RinconDelSur.usuario.controller;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.service.SolicitudCorreoService;
import lombok.RequiredArgsConstructor;
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
public class SolicitudCorreoController {

    private final SolicitudCorreoService solicitudCorreoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudCorreoResponse crear(@RequestBody SolicitudCorreoRequest datos) {
        return solicitudCorreoService.crear(datos);
    }

    @GetMapping("/pendientes")
    public List<SolicitudCorreoResponse> listarPendientes() {
        return solicitudCorreoService.listarPendientes();
    }

    @GetMapping("/pendientes/{rut}")
    public SolicitudCorreoResponse obtenerPendientePorRut(@PathVariable String rut) {
        return solicitudCorreoService.obtenerPendientePorRut(rut);
    }

    @PutMapping("/{id}/resolver")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resolver(@PathVariable Long id, @RequestParam boolean aprobar) {
        solicitudCorreoService.resolver(id, aprobar);
    }
}