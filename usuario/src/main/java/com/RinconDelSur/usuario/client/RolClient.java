package com.RinconDelSur.usuario.client;

import com.RinconDelSur.usuario.dto.RolDto;
import com.RinconDelSur.usuario.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class RolClient {

    private final WebClient webClient;

    public RolClient(@Value("${roles.service.url}") String rolesUrl) {
        this.webClient = WebClient.builder().baseUrl(rolesUrl).build();
    }

    public List<RolDto> listar() {
        try {
            return webClient.get()
                    .uri("/api/roles")
                    .retrieve()
                    .bodyToFlux(RolDto.class)
                    .collectList()
                    .block();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de roles no está disponible");
        }
    }

    public RolDto obtener(Long id) {
        try {
            return webClient.get()
                    .uri("/api/roles/{id}", id)
                    .retrieve()
                    .bodyToMono(RolDto.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                    .block();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de roles no está disponible");
        }
    }
}
