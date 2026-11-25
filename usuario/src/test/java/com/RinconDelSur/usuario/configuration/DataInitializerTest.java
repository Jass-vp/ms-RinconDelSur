package com.RinconDelSur.usuario.configuration;

import com.RinconDelSur.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DataInitializer dataInitializer;

    @Test
    void run_cuandoBaseDatosVacia_insertaSieteUsuarios() {
        when(usuarioRepository.count()).thenReturn(0L);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");

        dataInitializer.run();

        verify(usuarioRepository).saveAll(argThat(iterable -> {
            int count = 0;
            for (Object ignored : iterable) {
                count++;
            }
            return count == 7;
        }));
    }

    @Test
    void run_cuandoYaExistenUsuarios_noInsertaNada() {
        when(usuarioRepository.count()).thenReturn(7L);

        dataInitializer.run();

        verify(usuarioRepository, never()).saveAll(anyList());
    }
}