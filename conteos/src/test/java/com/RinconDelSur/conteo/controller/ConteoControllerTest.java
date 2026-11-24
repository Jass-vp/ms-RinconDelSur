package com.RinconDelSur.conteo.controller;

import com.RinconDelSur.conteo.dto.ConteoRequest;
import com.RinconDelSur.conteo.dto.ConteoResponse;
import com.RinconDelSur.conteo.dto.DatoGraficoResponse;
import com.RinconDelSur.conteo.service.ConteoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConteoControllerTest {

    @Mock
    private ConteoService conteoService;

    @InjectMocks
    private ConteoController conteoController;

    @Test
    void listar_debeRetornarListaDeConteos() {
        ConteoResponse resp = new ConteoResponse(
                1L, "P1", "Acceso Principal", 50, 20, "26–40", "Solo", "Camila", "10h", 1L
        );
        when(conteoService.listar()).thenReturn(List.of(resp));

        List<ConteoResponse> resultado = conteoController.listar();

        assertEquals(1, resultado.size());
        assertEquals("P1", resultado.get(0).puertaAcceso());
        assertEquals("Acceso Principal", resultado.get(0).puertaNombre());
        verify(conteoService).listar();
    }

    @Test
    void registrar_debeRetornarConteoCreado() {
        ConteoRequest request = new ConteoRequest(
                "P1", 100, 40, "26–40", "Solo", "Camila", "11h", 1L
        );
        ConteoResponse response = new ConteoResponse(
                10L, "P1", "Acceso Principal", 100, 40, "26–40", "Solo", "Camila", "11h", 1L
        );

        when(conteoService.registrar(any(ConteoRequest.class))).thenReturn(response);

        ConteoResponse resultado = conteoController.registrar(request);

        assertNotNull(resultado);
        assertEquals(10L, resultado.id());
        assertEquals(40, resultado.cantidadClientes());
        verify(conteoService).registrar(request);
    }

    @Test
    void obtenerFlujoPorHora_debeRetornarMetricas() {
        when(conteoService.obtenerFlujoPorHora()).thenReturn(List.of(
                new DatoGraficoResponse("10h", 25)
        ));

        List<DatoGraficoResponse> resultado = conteoController.obtenerFlujoPorHora();

        assertEquals(1, resultado.size());
        assertEquals("10h", resultado.get(0).etiqueta());
        assertEquals(25, resultado.get(0).valor());
        verify(conteoService).obtenerFlujoPorHora();
    }

    @Test
    void obtenerSegmentacion_debeRetornarMetricas() {
        when(conteoService.obtenerSegmentacion()).thenReturn(List.of(
                new DatoGraficoResponse("Transito (Pasillo)", 150),
                new DatoGraficoResponse("Clientes / Visita", 50)
        ));

        List<DatoGraficoResponse> resultado = conteoController.obtenerSegmentacion();

        assertEquals(2, resultado.size());
        assertEquals(150, resultado.get(0).valor());
        verify(conteoService).obtenerSegmentacion();
    }

    @Test
    void obtenerFlujoPorTurno_debeRetornarMetricas() {
        when(conteoService.obtenerFlujoPorTurno()).thenReturn(List.of(
                new DatoGraficoResponse("Turno Mañana", 30),
                new DatoGraficoResponse("Turno Tarde", 45)
        ));

        List<DatoGraficoResponse> resultado = conteoController.obtenerFlujoPorTurno();

        assertEquals(2, resultado.size());
        assertEquals("Turno Tarde", resultado.get(1).etiqueta());
        verify(conteoService).obtenerFlujoPorTurno();
    }
}