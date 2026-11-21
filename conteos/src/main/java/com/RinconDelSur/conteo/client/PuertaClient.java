package com.RinconDelSur.conteo.client;

import com.RinconDelSur.conteo.dto.PuertaDto;
import com.RinconDelSur.conteo.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.Collections;
import java.util.List;

@Component
public class PuertaClient {

    private final WebClient webClient;

    public PuertaClient(@Value("${accesos.service.url}") String accesosUrl) {
        this.webClient = WebClient.builder().baseUrl(accesosUrl).build();
    }

    public List<PuertaDto> listar() {
        try {
            List<PuertaDto> puertas = webClient.get()
                    .uri("/api/accesos")
                    .retrieve()
                    .bodyToFlux(PuertaDto.class)
                    .collectList()
                    .block();
            return puertas != null ? puertas : Collections.emptyList();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de puertas no está disponible");
        }
    }

    public PuertaDto obtener(String identificador) {
        if (identificador == null || identificador.isBlank()) {
            return null;
        }

        String valorBuscado = identificador.trim();

        return listar().stream()
                .filter(p -> (p.codigo() != null && p.codigo().equalsIgnoreCase(valorBuscado))
                        || (p.id() != null && String.valueOf(p.id()).equalsIgnoreCase(valorBuscado))
                        || (p.nombre() != null && p.nombre().equalsIgnoreCase(valorBuscado)))
                .findFirst()
                .orElse(null);
    }
}