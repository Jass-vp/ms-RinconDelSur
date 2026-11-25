package com.RinconDelSur.conteo.service;

import com.RinconDelSur.conteo.client.PuertaClient;
import com.RinconDelSur.conteo.client.UsuarioClient;
import com.RinconDelSur.conteo.dto.*;
import com.RinconDelSur.conteo.exception.PeticionInvalidaException;
import com.RinconDelSur.conteo.model.Conteo;
import com.RinconDelSur.conteo.repository.ConteoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConteoService {

    private final ConteoRepository conteoRepository;
    private final PuertaClient puertaClient;
    private final UsuarioClient usuarioClient;

    public List<ConteoResponse> listar() {
        log.info("Listando todos los registros de conteo ordenados por ID descendente");
        List<Conteo> conteos = conteoRepository.findAllByOrderByIdDesc();
        Map<String, String> puertas = puertaClient.listar().stream()
                .collect(Collectors.toMap(PuertaDto::codigo, PuertaDto::nombre));

        log.debug("Se encontraron {} conteos y {} puertas asociadas", conteos.size(), puertas.size());
        return conteos.stream()
                .map(c -> aRespuesta(c, puertas.getOrDefault(c.getPuertaAcceso(), c.getPuertaAcceso())))
                .toList();
    }

    public ConteoResponse registrar(ConteoRequest datos) {
        log.info("Iniciando registro de conteo para puerta: {}, usuarioId: {}", datos.puertaAcceso(), datos.usuarioId());

        if (datos.puertaAcceso() == null || datos.puertaAcceso().isBlank()) {
            log.warn("Intento de registro fallido: puerta de acceso vacía o nula");
            throw new PeticionInvalidaException("La puerta de acceso es obligatoria");
        }
        if (datos.cantidadTransito() == null || datos.cantidadClientes() == null) {
            log.warn("Intento de registro fallido: cantidades de tránsito o clientes nulas");
            throw new PeticionInvalidaException("Las cantidades son obligatorias");
        }

        PuertaDto puerta = puertaClient.obtener(datos.puertaAcceso());
        if (puerta == null) {
            log.warn("Validación fallida: la puerta '{}' no existe en el microservicio de puertas", datos.puertaAcceso());
            throw new PeticionInvalidaException("La puerta de acceso indicada no existe");
        }
        if (Boolean.FALSE.equals(puerta.estaHabilitado())) {
            log.warn("Validación fallida: la puerta '{}' se encuentra deshabilitada", datos.puertaAcceso());
            throw new PeticionInvalidaException("La puerta de acceso se encuentra deshabilitada");
        }

        if (datos.usuarioId() != null && datos.usuarioId() > 0) {
            UsuarioDto usuario = usuarioClient.obtener(datos.usuarioId());
            if (usuario == null) {
                log.warn("Validación fallida: el usuario con ID {} no existe", datos.usuarioId());
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

        Conteo guardado = conteoRepository.save(nuevo);
        log.info("Conteo registrado exitosamente con ID: {} en puerta: {}", guardado.getId(), puerta.nombre());

        return aRespuesta(guardado, puerta.nombre());
    }

    public List<DatoGraficoResponse> obtenerFlujoPorHora() {
        log.info("Calculando métricas de flujo de clientes por hora");
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
        log.info("Calculando segmentación entre tránsito total y clientes efectivos");
        List<Conteo> conteos = conteoRepository.findAll();
        int totalTransito = conteos.stream().mapToInt(Conteo::getCantidadTransito).sum();
        int totalClientes = conteos.stream().mapToInt(Conteo::getCantidadClientes).sum();

        return List.of(
                new DatoGraficoResponse("Transito (Pasillo)", totalTransito),
                new DatoGraficoResponse("Clientes / Visita", totalClientes)
        );
    }

    public List<DatoGraficoResponse> obtenerFlujoPorTurno() {
        log.info("Calculando distribución de clientes por turno (Mañana vs Tarde)");
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
            } catch (Exception e) {
                log.debug("Formato de hora no numérico ('{}') en conteo ID {}, asignando a Turno Tarde", c.getHora(), c.getId());
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