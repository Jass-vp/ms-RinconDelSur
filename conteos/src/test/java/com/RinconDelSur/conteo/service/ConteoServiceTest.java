package com.RinconDelSur.conteo.service;

import com.RinconDelSur.conteo.client.PuertaClient;
import com.RinconDelSur.conteo.client.UsuarioClient;
import com.RinconDelSur.conteo.dto.*;
import com.RinconDelSur.conteo.exception.PeticionInvalidaException;
import com.RinconDelSur.conteo.model.Conteo;
import com.RinconDelSur.conteo.repository.ConteoRepository;
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

    // ==================== TESTS DE LISTAR ====================

    @Test
    void listar_debeMapearNombreDePuertaOUsarCodigoPorDefecto() {
        Conteo c1 = Conteo.builder()
                .id(1L).puertaAcceso("P1").cantidadTransito(50).cantidadClientes(20)
                .rangoEtario("26–40").compania("Solo").observador("Ana").hora("10h").usuarioId(1L)
                .build();
        Conteo c2 = Conteo.builder()
                .id(2L).puertaAcceso("P_DESCONOCIDA").cantidadTransito(30).cantidadClientes(10)
                .rangoEtario("18–25").compania("Pareja").observador("Luis").hora("15h").usuarioId(2L)
                .build();

        when(conteoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(c1, c2));
        when(puertaClient.listar()).thenReturn(List.of(
                new PuertaDto(1L, "P1", "Acceso Norte", "Entrada norte", true)
        ));

        List<ConteoResponse> resultado = conteoService.listar();

        assertEquals(2, resultado.size());
        assertEquals("Acceso Norte", resultado.get(0).puertaNombre());
        assertEquals("P_DESCONOCIDA", resultado.get(1).puertaNombre());
    }

    // ==================== TESTS DE REGISTRAR (CAMINOS FELICES) ====================

    @Test
    void registrar_cuandoDatosCompletosSonValidos_debeGuardarYRetornarRespuesta() {
        ConteoRequest request = new ConteoRequest(
                "P1", 100, 40, "41–60", "Familia", "Carlos", "11h", 5L
        );

        when(puertaClient.obtener("P1")).thenReturn(
                new PuertaDto(1L, "P1", "Acceso Principal", "Entrada principal", true)
        );
        when(usuarioClient.obtener(5L)).thenReturn(
                new UsuarioDto(5L, "18.456.789-2", "Carlos", "carlos@test.cl", "+56912345678", "Analista", "Mañana")
        );
        when(conteoRepository.save(any(Conteo.class))).thenAnswer(inv -> {
            Conteo c = inv.getArgument(0);
            c.setId(10L);
            return c;
        });

        ConteoResponse response = conteoService.registrar(request);

        assertNotNull(response);
        assertEquals(10L, response.id());
        assertEquals("Acceso Principal", response.puertaNombre());
        assertEquals("41–60", response.rangoEtario());
        assertEquals("Familia", response.compania());
        assertEquals("Carlos", response.observador());
        assertEquals("11h", response.hora());
        verify(conteoRepository, times(1)).save(any(Conteo.class));
    }

    @Test
    void registrar_cuandoCamposOpcionalesSonNulos_debeAsignarValoresPorDefecto() {
        ConteoRequest request = new ConteoRequest(
                "P1", 80, 25, null, null, null, null, null
        );

        when(puertaClient.obtener("P1")).thenReturn(
                new PuertaDto(1L, "P1", "Acceso Sur", "Entrada sur", true)
        );
        when(conteoRepository.save(any(Conteo.class))).thenAnswer(inv -> {
            Conteo c = inv.getArgument(0);
            c.setId(11L);
            return c;
        });

        ConteoResponse response = conteoService.registrar(request);

        assertEquals("26–40", response.rangoEtario());
        assertEquals("Solo", response.compania());
        assertEquals("Observador General", response.observador());
        assertEquals("12h", response.hora());
        verify(usuarioClient, never()).obtener(any());
    }

    // ==================== TESTS DE REGISTRAR (VALIDACIONES / ERRORES) ====================

    @Test
    void registrar_cuandoPuertaEsNulaOBlanca_debeLanzarExcepcion() {
        ConteoRequest reqNula = new ConteoRequest(null, 10, 5, null, null, null, null, null);
        ConteoRequest reqBlanca = new ConteoRequest("   ", 10, 5, null, null, null, null, null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(reqNula));
        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(reqBlanca));
        verify(conteoRepository, never()).save(any());
    }

    @Test
    void registrar_cuandoCantidadesSonNulas_debeLanzarExcepcion() {
        ConteoRequest reqSinTransito = new ConteoRequest("P1", null, 5, null, null, null, null, null);
        ConteoRequest reqSinClientes = new ConteoRequest("P1", 10, null, null, null, null, null, null);

        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(reqSinTransito));
        assertThrows(PeticionInvalidaException.class, () -> conteoService.registrar(reqSinClientes));
        verify(conteoRepository, never()).save(any());
    }

    @Test
    void registrar_cuandoPuertaNoExiste_debeLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P99", 10, 5, null, null, null, null, null);
        when(puertaClient.obtener("P99")).thenReturn(null);

        PeticionInvalidaException ex = assertThrows(
                PeticionInvalidaException.class,
                () -> conteoService.registrar(request)
        );
        assertEquals("La puerta de acceso indicada no existe", ex.getMessage());
    }

    @Test
    void registrar_cuandoPuertaEstaDeshabilitada_debeLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P2", 10, 5, null, null, null, null, null);
        when(puertaClient.obtener("P2")).thenReturn(
                new PuertaDto(2L, "P2", "Puerta Cerrada", "Sector bodega", false)
        );

        PeticionInvalidaException ex = assertThrows(
                PeticionInvalidaException.class,
                () -> conteoService.registrar(request)
        );
        assertEquals("La puerta de acceso se encuentra deshabilitada", ex.getMessage());
    }

    @Test
    void registrar_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        ConteoRequest request = new ConteoRequest("P1", 10, 5, null, null, null, null, 999L);
        when(puertaClient.obtener("P1")).thenReturn(
                new PuertaDto(1L, "P1", "Acceso Norte", "Entrada norte", true)
        );
        when(usuarioClient.obtener(999L)).thenReturn(null);

        PeticionInvalidaException ex = assertThrows(
                PeticionInvalidaException.class,
                () -> conteoService.registrar(request)
        );
        assertEquals("El usuario indicado no existe", ex.getMessage());
    }

    // ==================== TESTS DE GRÁFICOS Y MÉTRICAS ====================

    @Test
    void obtenerFlujoPorHora_debeAgruparYSumarClientesPorHora() {
        Conteo c1 = Conteo.builder().hora("10h").cantidadClientes(12).build();
        Conteo c2 = Conteo.builder().hora("10h").cantidadClientes(8).build();
        Conteo c3 = Conteo.builder().hora("16h").cantidadClientes(25).build();

        when(conteoRepository.findAll()).thenReturn(List.of(c1, c2, c3));

        List<DatoGraficoResponse> resultado = conteoService.obtenerFlujoPorHora();

        assertEquals(2, resultado.size());
        assertEquals("10h", resultado.get(0).etiqueta());
        assertEquals(20, resultado.get(0).valor());
        assertEquals("16h", resultado.get(1).etiqueta());
        assertEquals(25, resultado.get(1).valor());
    }

    @Test
    void obtenerSegmentacion_debeSumarTransitoYClientes() {
        Conteo c1 = Conteo.builder().cantidadTransito(100).cantidadClientes(30).build();
        Conteo c2 = Conteo.builder().cantidadTransito(50).cantidadClientes(20).build();

        when(conteoRepository.findAll()).thenReturn(List.of(c1, c2));

        List<DatoGraficoResponse> resultado = conteoService.obtenerSegmentacion();

        assertEquals(2, resultado.size());
        assertEquals(150, resultado.get(0).valor());
        assertEquals(50, resultado.get(1).valor());
    }

    @Test
    void obtenerFlujoPorTurno_debeSepararMananaTardeYCapturarFormatoInvalido() {
        Conteo manana = Conteo.builder().id(1L).hora("09h").cantidadClientes(15).build();
        Conteo tarde = Conteo.builder().id(2L).hora("15h").cantidadClientes(25).build();
        Conteo formatoRaro = Conteo.builder().id(3L).hora("N/A").cantidadClientes(10).build();

        when(conteoRepository.findAll()).thenReturn(List.of(manana, tarde, formatoRaro));

        List<DatoGraficoResponse> resultado = conteoService.obtenerFlujoPorTurno();

        assertEquals(2, resultado.size());
        assertEquals("Turno Mañana", resultado.get(0).etiqueta());
        assertEquals(15, resultado.get(0).valor());
        assertEquals("Turno Tarde", resultado.get(1).etiqueta());
        assertEquals(35, resultado.get(1).valor());
    }
}