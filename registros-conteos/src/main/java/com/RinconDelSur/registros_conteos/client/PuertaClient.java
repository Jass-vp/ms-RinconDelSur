package com.RinconDelSur.registros_conteos.client;

import com.RinconDelSur.registros_conteos.dto.PuertaDto;
import com.RinconDelSur.registros_conteos.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class PuertaClient {

    private final WebClient webClient;

    public PuertaClient(@Value("${puertas.service.url}") String puertasUrl) {
        this.webClient = WebClient.builder().baseUrl(puertasUrl).build();
    }

    public List<PuertaDto> listar() {
        try {
            return webClient.get()
                    .uri("/api/puertas")
                    .retrieve()
                    .bodyToFlux(PuertaDto.class)
                    .collectList()
                    .block();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de puertas no está disponible");
        }
    }

    public PuertaDto obtener(String id) {
        try {
            return webClient.get()
                    .uri("/api/puertas/{id}", id)
                    .retrieve()
                    .bodyToMono(PuertaDto.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                    .block();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de puertas no está disponible");
        }
    }
}
