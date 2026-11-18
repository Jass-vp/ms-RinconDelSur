package com.RinconDelSur.roles.config;

import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DatosIniciales implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatosIniciales.class);
    private final RolRepository rolRepository;

    @Override
    public void run(String... args) {
        if (rolRepository.count() == 0) {
            logger.info("Cargando roles iniciales de Tienda Rincón del Sur...");
            rolRepository.saveAll(List.of(
                    Rol.builder()
                            .nombre("P-01")
                            .descripcion("Jefa de Tienda - Administración general y métricas globales")
                            .habilitado(true)
                            .build(),
                    Rol.builder()
                            .nombre("P-02")
                            .descripcion("Subgerente - Gestión de cuentas, reportes y administración comercial")
                            .habilitado(true)
                            .build(),
                    Rol.builder()
                            .nombre("P-03")
                            .descripcion("Supervisora - Supervisión de conteo y aforo en terreno")
                            .habilitado(true)
                            .build(),
                    Rol.builder()
                            .nombre("P-04")
                            .descripcion("Observador - Toma de datos de conteo y aforo en puerta")
                            .habilitado(true)
                            .build()
            ));
            logger.info("Roles iniciales registrados correctamente.");
        }
    }
}