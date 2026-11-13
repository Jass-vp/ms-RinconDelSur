package com.RinconDelSur.roles.config;

import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            rolRepository.saveAll(List.of(
                    Rol.builder().id(1L).nombre("Subgerente").descripcion("Encargado de los reportes y gestión comercial").habilitado(true).build(),
                    Rol.builder().id(2L).nombre("Observador").descripcion("Toma datos de conteo y aforo en terreno").habilitado(true).build()
            ));
        }
    }
}
