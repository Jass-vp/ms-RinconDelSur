package com.RinconDelSur.registros_conteos.client;

import com.RinconDelSur.registros_conteos.dto.UsuarioDto;
import com.RinconDelSur.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Component
public class UsuarioClient {

    private final WebClient webClient;

    public UsuarioClient(@Value("${usuarios.service.url}") String usuariosUrl) {
        this.webClient = WebClient.builder().baseUrl(usuariosUrl).build();
    }

    public UsuarioDto obtener(Long id) {
        try {
            return webClient.get()
                    .uri("/api/usuarios/{id}", id)
                    .retrieve()
                    .bodyToMono(UsuarioDto.class)
                    .onErrorResume(WebClientResponseException.NotFound.class, e -> Mono.empty())
                    .block();
        } catch (WebClientRequestException e) {
            throw new ServicioNoDisponibleException("El servicio de usuarios no está disponible");
        }
    }
}
