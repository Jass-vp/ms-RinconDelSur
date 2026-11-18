package com.RinconDelSur.roles.service;

import com.RinconDelSur.roles.exception.RecursoNoEncontradoException;
import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.repository.RolRepository;
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
class RolServiceTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private RolService rolService;

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
    void listar_DebeRetornarListaDeRoles() {
        when(rolRepository.findAll()).thenReturn(List.of(rolEjemplo));

        List<Rol> resultado = rolService.listar();

        assertEquals(1, resultado.size());
        assertEquals("P-01", resultado.get(0).getNombre());
        verify(rolRepository, times(1)).findAll();
    }

    @Test
    void obtener_CuandoExisteId_DebeRetornarRol() {
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolEjemplo));

        Rol resultado = rolService.obtener(1L);

        assertNotNull(resultado);
        assertEquals("P-01", resultado.getNombre());
        verify(rolRepository).findById(1L);
    }

    @Test
    void obtener_CuandoNoExisteId_DebeLanzarExcepcion() {
        when(rolRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> rolService.obtener(99L));
        verify(rolRepository).findById(99L);
    }

    @Test
    void actualizar_ConTodosLosCampos_DebeActualizarYGuardar() {
        Rol datosNuevos = Rol.builder()
                .nombre("P-02")
                .descripcion("Subgerente")
                .habilitado(false)
                .build();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        when(rolRepository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        Rol actualizado = rolService.actualizar(1L, datosNuevos);

        assertEquals("P-02", actualizado.getNombre());
        assertEquals("Subgerente", actualizado.getDescripcion());
        assertFalse(actualizado.getHabilitado());
        verify(rolRepository).save(rolEjemplo);
    }

    @Test
    void actualizar_ConCamposNulos_NoDebeSobrescribirValoresExistentes() {
        Rol datosVacios = new Rol();

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rolEjemplo));
        when(rolRepository.save(any(Rol.class))).thenAnswer(inv -> inv.getArgument(0));

        Rol actualizado = rolService.actualizar(1L, datosVacios);

        assertEquals("P-01", actualizado.getNombre());
        assertEquals("Jefa de Tienda", actualizado.getDescripcion());
        assertTrue(actualizado.getHabilitado());
        verify(rolRepository).save(rolEjemplo);
    }
}