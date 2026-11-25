package com.RinconDelSur.accesos.service;

import com.RinconDelSur.accesos.dto.AccesoRequestDTO;
import com.RinconDelSur.accesos.dto.AccesoResponseDTO;
import com.RinconDelSur.accesos.exception.RecursoNoEncontradoException;
import com.RinconDelSur.accesos.model.Acceso;
import com.RinconDelSur.accesos.repository.AccesoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock
    private AccesoRepository accesoRepository;

    @InjectMocks
    private AccesoService accesoService;

    private Acceso accesoEjemplo;

    @BeforeEach
    void setUp() {
        accesoEjemplo = Acceso.builder()
                .id(1L)
                .codigo("ACC-01")
                .nombre("Puerta Principal")
                .descripcion("Entrada frontal tienda")
                .estaHabilitado(true)
                .build();
    }

    @Test
    void listarTodos_retornaListaMapeada() {
        when(accesoRepository.findAll()).thenReturn(List.of(accesoEjemplo));

        List<AccesoResponseDTO> resultado = accesoService.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals("ACC-01", resultado.get(0).getCodigo());
        assertEquals("Puerta Principal", resultado.get(0).getNombre());
    }

    @Test
    void listarHabilitados_retornaSoloHabilitados() {
        when(accesoRepository.findByEstaHabilitadoTrue()).thenReturn(List.of(accesoEjemplo));

        List<AccesoResponseDTO> resultado = accesoService.listarHabilitados();

        assertEquals(1, resultado.size());
        assertTrue(resultado.get(0).getEstaHabilitado());
    }

    @Test
    void obtenerPorId_cuandoExiste_retornaAcceso() {
        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoEjemplo));

        AccesoResponseDTO resultado = accesoService.obtenerPorId(1L);

        assertEquals(1L, resultado.getId());
        assertEquals("ACC-01", resultado.getCodigo());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_lanzaRecursoNoEncontrado() {
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> accesoService.obtenerPorId(99L));
    }

    @Test
    void crear_datosValidos_guardaEnMayusculasYRetornaDTO() {
        AccesoRequestDTO request = new AccesoRequestDTO();
        request.setCodigo("acc-02");
        request.setNombre("Bodega Norte");
        request.setDescripcion("Acceso carga");
        request.setEstaHabilitado(null);

        when(accesoRepository.existsByCodigoIgnoreCase("acc-02")).thenReturn(false);
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> {
            Acceso a = inv.getArgument(0);
            a.setId(2L);
            return a;
        });

        AccesoResponseDTO resultado = accesoService.crear(request);

        assertEquals(2L, resultado.getId());
        assertEquals("ACC-02", resultado.getCodigo());
        assertTrue(resultado.getEstaHabilitado());
    }

    @Test
    void crear_codigoDuplicado_lanzaIllegalArgument() {
        AccesoRequestDTO request = new AccesoRequestDTO();
        request.setCodigo("ACC-01");

        when(accesoRepository.existsByCodigoIgnoreCase("ACC-01")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> accesoService.crear(request));
        verify(accesoRepository, never()).save(any());
    }

    @Test
    void actualizar_cuandoExiste_actualizaCamposNoNulos() {
        AccesoRequestDTO request = new AccesoRequestDTO();
        request.setCodigo("acc-01-mod");
        request.setNombre("Puerta Actualizada");
        request.setDescripcion("Nueva desc");
        request.setEstaHabilitado(false);

        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoEjemplo));
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> inv.getArgument(0));

        AccesoResponseDTO resultado = accesoService.actualizar(1L, request);

        assertEquals("ACC-01-MOD", resultado.getCodigo());
        assertEquals("Puerta Actualizada", resultado.getNombre());
        assertEquals("Nueva desc", resultado.getDescripcion());
        assertFalse(resultado.getEstaHabilitado());
    }

    @Test
    void actualizar_conCamposNulos_mantieneValoresActuales() {
        AccesoRequestDTO request = new AccesoRequestDTO();

        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoEjemplo));
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> inv.getArgument(0));

        AccesoResponseDTO resultado = accesoService.actualizar(1L, request);

        assertEquals("ACC-01", resultado.getCodigo());
        assertEquals("Puerta Principal", resultado.getNombre());
        assertTrue(resultado.getEstaHabilitado());
    }

    @Test
    void actualizar_cuandoNoExiste_lanzaRecursoNoEncontrado() {
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> accesoService.actualizar(99L, new AccesoRequestDTO()));
    }

    @Test
    void cambiarEstado_cuandoExiste_actualizaHabilitado() {
        when(accesoRepository.findById(1L)).thenReturn(Optional.of(accesoEjemplo));
        when(accesoRepository.save(any(Acceso.class))).thenAnswer(inv -> inv.getArgument(0));

        AccesoResponseDTO resultado = accesoService.cambiarEstado(1L, false);

        assertFalse(resultado.getEstaHabilitado());
    }

    @Test
    void cambiarEstado_cuandoNoExiste_lanzaRecursoNoEncontrado() {
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> accesoService.cambiarEstado(99L, true));
    }
}