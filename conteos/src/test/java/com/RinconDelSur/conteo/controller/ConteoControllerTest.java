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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConteoControllerTest {

    @Mock
    private ConteoService conteoService;

    @InjectMocks
    private ConteoController conteoController;

    @Test
    void listar_DeberiaRetornarListaConteos() {
        ConteoResponse resp = new ConteoResponse(1L, "P1", "Acceso Principal", 40, 15, "26–40", "Solo", "Camila", "10h", 1L);
        when(conteoService.listar()).thenReturn(List.of(resp));

        List<ConteoResponse> resultado = conteoController.listar();

        assertEquals(1, resultado.size());
        assertEquals("P1", resultado.get(0).puertaAcceso());
    }

    @Test
    void registrar_DeberiaRetornarConteoCreado() {
        ConteoRequest req = new ConteoRequest("P1", 40, 15, "26–40", "Solo", "Camila", "10h", 1L);
        ConteoResponse resp = new ConteoResponse(1L, "P1", "Acceso Principal", 40, 15, "26–40", "Solo", "Camila", "10h", 1L);
        when(conteoService.registrar(any(ConteoRequest.class))).thenReturn(resp);

        ConteoResponse resultado = conteoController.registrar(req);

        assertEquals(1L, resultado.id());
        assertEquals("Acceso Principal", resultado.puertaNombre());
    }

    @Test
    void obtenerFlujoPorHora_DeberiaRetornarDatos() {
        when(conteoService.obtenerFlujoPorHora()).thenReturn(List.of(new DatoGraficoResponse("10h", 25)));

        List<DatoGraficoResponse> resultado = conteoController.obtenerFlujoPorHora();

        assertEquals(1, resultado.size());
        assertEquals(25, resultado.get(0).valor());
    }

    @Test
    void obtenerSegmentacion_DeberiaRetornarDatos() {
        when(conteoService.obtenerSegmentacion()).thenReturn(List.of(new DatoGraficoResponse("Clientes / Visita", 30)));

        List<DatoGraficoResponse> resultado = conteoController.obtenerSegmentacion();

        assertEquals(30, resultado.get(0).valor());
    }

    @Test
    void obtenerFlujoPorTurno_DeberiaRetornarDatos() {
        when(conteoService.obtenerFlujoPorTurno()).thenReturn(List.of(new DatoGraficoResponse("Turno Mañana", 45)));

        List<DatoGraficoResponse> resultado = conteoController.obtenerFlujoPorTurno();

        assertEquals("Turno Mañana", resultado.get(0).etiqueta());
    }
}