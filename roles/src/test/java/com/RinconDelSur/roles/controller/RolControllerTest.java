package com.RinconDelSur.roles.controller;

import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.service.RolService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RolController.class)
class RolControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RolService rolService;

    @Autowired
    private ObjectMapper objectMapper;

    private Rol rolEjemplo;

    @BeforeEach
    void setUp() {
        rolEjemplo = Rol.builder()
                .id(1L)
                .nombre("P-01")
                .descripcion("Jefa de Tienda")
                .habilitado(true)
                .build();
    }

    @Test
    void listar_DebeRetornarStatus200YLista() throws Exception {
        when(rolService.listar()).thenReturn(List.of(rolEjemplo));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("P-01"));
    }

    @Test
    void obtener_DebeRetornarStatus200YRol() throws Exception {
        when(rolService.obtener(1L)).thenReturn(rolEjemplo);

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.descripcion").value("Jefa de Tienda"));
    }

    @Test
    void actualizar_DebeRetornarStatus200YRolActualizado() throws Exception {
        Rol actualizado = Rol.builder()
                .id(1L)
                .nombre("P-01")
                .descripcion("Descripción Actualizada")
                .habilitado(false)
                .build();

        when(rolService.actualizar(eq(1L), any(Rol.class))).thenReturn(actualizado);

        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(actualizado)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.habilitado").value(false));
    }
}