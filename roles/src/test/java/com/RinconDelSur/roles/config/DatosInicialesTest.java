package com.RinconDelSur.roles.config;

import com.RinconDelSur.roles.repository.RolRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatosInicialesTest {

    @Mock
    private RolRepository rolRepository;

    @InjectMocks
    private DatosIniciales datosIniciales;

    @Test
    void run_CuandoNoExistenRoles_DebeCrearLosCuatroPerfiles() throws Exception {
        when(rolRepository.count()).thenReturn(0L);

        datosIniciales.run();

        verify(rolRepository, times(1)).saveAll(anyList());
    }

    @Test
    void run_CuandoYaExistenRoles_NoDebeDuplicar() throws Exception {
        when(rolRepository.count()).thenReturn(4L);

        datosIniciales.run();

        verify(rolRepository, never()).saveAll(anyList());
    }
}