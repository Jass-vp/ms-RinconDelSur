package com.RinconDelSur.registros_conteos.service;

import com.RinconDelSur.registros_conteos.client.PuertaClient;
import com.RinconDelSur.registros_conteos.client.UsuarioClient;
import com.RinconDelSur.registros_conteos.dto.*;
import com.RinconDelSur.registros_conteos.exception.PeticionInvalidaException;
import com.RinconDelSur.registros_conteos.model.Conteo;
import com.RinconDelSur.registros_conteos.repository.ConteoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConteoService {

    private final ConteoRepository conteoRepository;
    private final PuertaClient puertaClient;
    private final UsuarioClient usuarioClient;

    public List<ConteoResponse> listar() {
        List<Conteo> conteos = conteoRepository.findAllByOrderByIdDesc();
        Map<String, String> puertas = puertaClient.listar().stream()
                .collect(Collectors.toMap(PuertaDto::id, PuertaDto::nombre));

        return conteos.stream()
                .map(c -> aRespuesta(c, puertas.getOrDefault(c.getPuertaAcceso(), c.getPuertaAcceso())))
                .toList();
    }

    public ConteoResponse registrar(ConteoRequest datos) {
        if (datos.puertaAcceso() == null || datos.puertaAcceso().isBlank()) {
            throw new PeticionInvalidaException("La puerta de acceso es obligatoria");
        }
        if (datos.cantidadTransito() == null || datos.cantidadClientes() == null) {
            throw new PeticionInvalidaException("Las cantidades son obligatorias");
        }

        // Validar que la puerta exista llamando a puertas-service con WebClient
        PuertaDto puerta = puertaClient.obtener(datos.puertaAcceso());
        if (puerta == null) {
            throw new PeticionInvalidaException("La puerta de acceso indicada no existe");
        }

        // Validar usuario mediante WebClient si se indica usuarioId
        if (datos.usuarioId() != null && datos.usuarioId() > 0) {
            UsuarioDto usuario = usuarioClient.obtener(datos.usuarioId());
            if (usuario == null) {
                throw new PeticionInvalidaException("El usuario indicado no existe");
            }
        }

        Conteo nuevo = Conteo.builder()
                .puertaAcceso(datos.puertaAcceso())
                .cantidadTransito(datos.cantidadTransito())
                .cantidadClientes(datos.cantidadClientes())
                .rangoEtario(datos.rangoEtario() != null ? datos.rangoEtario() : "26–40")
                .compania(datos.compania() != null ? datos.compania() : "Solo")
                .observador(datos.observador() != null ? datos.observador() : "Observador General")
                .hora(datos.hora() != null ? datos.hora() : "12h")
                .usuarioId(datos.usuarioId())
                .build();

        return aRespuesta(conteoRepository.save(nuevo), puerta.nombre());
    }

    public List<DatoGraficoResponse> obtenerFlujoPorHora() {
        List<Conteo> conteos = conteoRepository.findAll();
        Map<String, Integer> porHora = new LinkedHashMap<>();

        for (Conteo c : conteos) {
            porHora.put(c.getHora(), porHora.getOrDefault(c.getHora(), 0) + c.getCantidadClientes());
        }

        List<DatoGraficoResponse> res = new ArrayList<>();
        porHora.forEach((hora, total) -> res.add(new DatoGraficoResponse(hora, total)));
        return res;
    }

    public List<DatoGraficoResponse> obtenerSegmentacion() {
        List<Conteo> conteos = conteoRepository.findAll();
        int totalTransito = conteos.stream().mapToInt(Conteo::getCantidadTransito).sum();
        int totalClientes = conteos.stream().mapToInt(Conteo::getCantidadClientes).sum();

        return List.of(
                new DatoGraficoResponse("Transito (Pasillo)", totalTransito),
                new DatoGraficoResponse("Clientes / Visita", totalClientes)
        );
    }

    public List<DatoGraficoResponse> obtenerFlujoPorTurno() {
        List<Conteo> conteos = conteoRepository.findAll();
        int manana = 0;
        int tarde = 0;

        for (Conteo c : conteos) {
            try {
                int h = Integer.parseInt(c.getHora().replace("h", "").trim());
                if (h < 14) {
                    manana += c.getCantidadClientes();
                } else {
                    tarde += c.getCantidadClientes();
                }
            } catch (Exception ignored) {
                tarde += c.getCantidadClientes();
            }
        }

        return List.of(
                new DatoGraficoResponse("Turno Mañana", manana),
                new DatoGraficoResponse("Turno Tarde", tarde)
        );
    }

    private ConteoResponse aRespuesta(Conteo c, String puertaNombre) {
        return new ConteoResponse(
                c.getId(),
                c.getPuertaAcceso(),
                puertaNombre,
                c.getCantidadTransito(),
                c.getCantidadClientes(),
                c.getRangoEtario(),
                c.getCompania(),
                c.getObservador(),
                c.getHora(),
                c.getUsuarioId()
        );
    }
}
