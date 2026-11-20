package com.RinconDelSur.conteo.service;

import com.RinconDelSur.conteo.client.PuertaClient;
import com.RinconDelSur.conteo.client.UsuarioClient;
import com.RinconDelSur.conteo.dto.*;
import com.RinconDelSur.conteo.exception.PeticionInvalidaException;
import com.RinconDelSur.conteo.model.Conteo;
import com.RinconDelSur.conteo.repository.ConteoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConteoServiceTest {

    @Mock
    private ConteoRepository conteoRepository;

    @Mock
    private PuertaClient puertaClient;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private ConteoService conteoService;

    private Conteo conteoEjemplo;
    private PuertaDto puertaHabilitada;
    private UsuarioDto usuarioValido;

    @BeforeEach
    void setUp() {
        conteoEjemplo = Conteo.builder()
                .id(1L)
                .puertaAcceso("P1")
                .cantidadTransito(50)
                .cantidadClientes(20)
                .rangoEtario("26–40")
                .compania("Solo")
                .observador("Camila")
                .hora("10h")
                .usuarioId(1L)
                .build();

        puertaHabilitada = new PuertaDto(1L, "P1", "Acceso Principal", "Entrada norte", true);
        usuarioValido = new UsuarioDto(1L, "12.345.678-9", "Camila", "camila@test.cl", "99999999", "Analista", "Mañana");
    }

    @Test
    void listar_DeberiaRetornarConteosConNombreDePuerta() {
        when(conteoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(conteoEjemplo));
        when(puertaClient.listar()).thenReturn(List.of(puertaHabilitada));

        List<ConteoResponse> resultado = conteoService.listar();

        assertEquals(1, resultado.size());
        assertEquals("Acceso Principal", resultado.get(0).puertaNombre());
        assertEquals(20, resultado.get(0).cantidadClientes());
    }

    @Test
    void registrar_ConDatosCompletos_DeberiaGuardarYRetornarResponse() {
        ConteoRequest request = new ConteoRequest("P1", 50, 20, "26–40", "En pareja", "Camila", "10h", 1L);

        when(puertaClient.obtener("P1")).thenReturn(puertaHabilitada);
        when(usuarioClient.obtener(1L)).thenReturn(usuarioValido);
        when(conteoRepository.save(any(Conteo.class))).thenReturn(conteoEjemplo);

        ConteoResponse response = conteoService.registrar(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Acceso Principal", response.puertaNombre());
        verify(conteoRepository).save(any(Conteo.class));
    }

    @Test
    void registrar_ConCamposOpcionalesNulos_DeberiaAsignarValoresPorDefecto() {
        ConteoRequest request = new ConteoRequest("P1", 30, 10, null, null, null, null, null);

        when(puertaClient.obtener("P1")).thenReturn(puertaHabilitada);
        when(conteoRepository.save(any(Conteo.class))).thenAnswer(inv -> {
            Conteo c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        ConteoResponse response = conteoService.registrar(request);

        assertEquals("26–40", response.rangoEtario());
        assertEquals("Solo", response.compania());
        assertEquals("Observador General", response.observador());
        assertEquals("12h", response.hora());
        verify(usuarioClient, never()).obtener(any());
    }

    @Test
    void registrar_SinPuertaAcceso_DeberiaLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest(" ", 30, 10, null, null, null, null, null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(request));
        verify(conteoRepository, never()).save(any());
    }

    @Test
    void registrar_SinCantidades_DeberiaLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P1", null, 10, null, null, null, null, null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(request));
    }

    @Test
    void registrar_PuertaNoExiste_DeberiaLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P99", 30, 10, null, null, null, null, null);
        when(puertaClient.obtener("P99")).thenReturn(null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(request));
    }

    @Test
    void registrar_PuertaDeshabilitada_DeberiaLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P2", 30, 10, null, null, null, null, null);
        PuertaDto puertaInactiva = new PuertaDto(2L, "P2", "Acceso Sur", "Cerrada", false);
        when(puertaClient.obtener("P2")).thenReturn(puertaInactiva);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(request));
    }

    @Test
    void registrar_UsuarioNoExiste_DeberiaLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P1", 30, 10, null, null, null, null, 99L);
        when(puertaClient.obtener("P1")).thenReturn(puertaHabilitada);
        when(usuarioClient.obtener(99L)).thenReturn(null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(request));
    }

    @Test
    void obtenerFlujoPorHora_DeberiaAgruparClientesPorHora() {
        Conteo c2 = Conteo.builder().id(2L).hora("10h").cantidadClientes(15).cantidadTransito(30).build();
        Conteo c3 = Conteo.builder().id(3L).hora("16h").cantidadClientes(25).cantidadTransito(40).build();

        when(conteoRepository.findAll()).thenReturn(List.of(conteoEjemplo, c2, c3));

        List<DatoGraficoResponse> resultado = conteoService.obtenerFlujoPorHora();

        assertEquals(2, resultado.size());
        assertEquals("10h", resultado.get(0).etiqueta());
        assertEquals(35, resultado.get(0).valor());
        assertEquals("16h", resultado.get(1).etiqueta());
        assertEquals(25, resultado.get(1).valor());
    }

    @Test
    void obtenerSegmentacion_DeberiaSumarTransitoYClientes() {
        Conteo c2 = Conteo.builder().id(2L).cantidadTransito(30).cantidadClientes(10).build();
        when(conteoRepository.findAll()).thenReturn(List.of(conteoEjemplo, c2));

        List<DatoGraficoResponse> resultado = conteoService.obtenerSegmentacion();

        assertEquals(2, resultado.size());
        assertEquals(80, resultado.get(0).valor()); // 50 + 30
        assertEquals(30, resultado.get(1).valor()); // 20 + 10
    }

    @Test
    void obtenerFlujoPorTurno_DeberiaSepararMananaTardeYFormatoInvalido() {
        Conteo cTarde = Conteo.builder().id(2L).hora("16h").cantidadClientes(15).build();
        Conteo cInvalido = Conteo.builder().id(3L).hora("sin-hora").cantidadClientes(5).build();

        when(conteoRepository.findAll()).thenReturn(List.of(conteoEjemplo, cTarde, cInvalido));

        List<DatoGraficoResponse> resultado = conteoService.obtenerFlujoPorTurno();

        assertEquals(2, resultado.size());
        assertEquals("Turno Mañana", resultado.get(0).etiqueta());
        assertEquals(20, resultado.get(0).valor());
        assertEquals("Turno Tarde", resultado.get(1).etiqueta());
        assertEquals(20, resultado.get(1).valor()); // 15 + 5
    }
}