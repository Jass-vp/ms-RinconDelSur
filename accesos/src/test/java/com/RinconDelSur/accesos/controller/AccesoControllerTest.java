package com.RinconDelSur.accesos.controller;

import com.RinconDelSur.accesos.dto.AccesoRequestDTO;
import com.RinconDelSur.accesos.dto.AccesoResponseDTO;
import com.RinconDelSur.accesos.service.AccesoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AccesoControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AccesoService accesoService;

    @InjectMocks
    private AccesoController accesoController;

    private AccesoResponseDTO accesoResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(accesoController).build();
        objectMapper = new ObjectMapper();

        accesoResponse = AccesoResponseDTO.builder()
                .id(1L)
                .codigo("A1")
                .nombre("Puerta Principal")
                .descripcion("Entrada frontal")
                .estaHabilitado(true)
                .build();
    }

    @Test
    void listarTodos_retorna200YLista() throws Exception {
        when(accesoService.listarTodos()).thenReturn(List.of(accesoResponse));

        mockMvc.perform(get("/api/accesos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].codigo").value("A1"));
    }

    @Test
    void listarHabilitados_retorna200YLista() throws Exception {
        when(accesoService.listarHabilitados()).thenReturn(List.of(accesoResponse));

        mockMvc.perform(get("/api/accesos/habilitados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estaHabilitado").value(true));
    }

    @Test
    void obtenerPorId_retorna200YAcceso() throws Exception {
        when(accesoService.obtenerPorId(1L)).thenReturn(accesoResponse);

        mockMvc.perform(get("/api/accesos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Puerta Principal"));
    }

    @Test
    void crear_retorna201YAccesoCreado() throws Exception {
        AccesoRequestDTO request = new AccesoRequestDTO();
        request.setCodigo("A1");
        request.setNombre("Puerta Principal");
        request.setDescripcion("Entrada frontal");
        request.setEstaHabilitado(true);

        when(accesoService.crear(any(AccesoRequestDTO.class))).thenReturn(accesoResponse);

        mockMvc.perform(post("/api/accesos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.codigo").value("A1"));
    }

    @Test
    void actualizar_retorna200YAccesoActualizado() throws Exception {
        AccesoRequestDTO request = new AccesoRequestDTO();
        request.setCodigo("A1");
        request.setNombre("Puerta Actualizada");
        request.setDescripcion("Entrada modificada");
        request.setEstaHabilitado(true);

        AccesoResponseDTO actualizado = AccesoResponseDTO.builder()
                .id(1L)
                .codigo("A1")
                .nombre("Puerta Actualizada")
                .descripcion("Entrada modificada")
                .estaHabilitado(true)
                .build();

        when(accesoService.actualizar(eq(1L), any(AccesoRequestDTO.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/accesos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Puerta Actualizada"));
    }

    @Test
    void cambiarEstado_retorna200YEstadoActualizado() throws Exception {
        AccesoResponseDTO bloqueado = AccesoResponseDTO.builder()
                .id(1L)
                .codigo("A1")
                .nombre("Puerta Principal")
                .descripcion("Entrada frontal")
                .estaHabilitado(false)
                .build();

        when(accesoService.cambiarEstado(1L, false)).thenReturn(bloqueado);

        mockMvc.perform(patch("/api/accesos/1/estado")
                        .param("habilitado", "false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estaHabilitado").value(false));
    }
}