package com.RinconDelSur.usuario.configuration;

import com.RinconDelSur.usuario.model.Usuario;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            log.info("Base de datos de usuarios vacía. Iniciando carga de usuarios por defecto de Tienda Rincón del Sur...");

            String clave = "1234";

            List<Usuario> usuariosIniciales = List.of(
                    Usuario.builder().rut("11111111-1").nombre("Carolina Méndez").correo("jefa@rincondelsur.cl").telefono("911111111").contrasena(clave).cargo("Jefa de Operaciones").turno("N/A").rolId(1L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál es el nombre de tu primera mascota?").respuestaSeguridad("perro").build(),
                    Usuario.builder().rut("12345678-9").nombre("Gabriel Gómez").correo("admin@rincondelsur.cl").telefono("912345678").contrasena(clave).cargo("Administrador del sistema").turno("N/A").rolId(2L).activo(true).primerIngreso(false).preguntaSeguridad("¿En qué ciudad naciste?").respuestaSeguridad("santiago").build(),
                    Usuario.builder().rut("15555666-7").nombre("Daniela Navarro").correo("supervisora@rincondelsur.cl").telefono("955556666").contrasena(clave).cargo("Supervisora").turno("Turno Diurno").rolId(3L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál es el nombre de tu colegio primario?").respuestaSeguridad("santa maria").build(),
                    Usuario.builder().rut("98765432-1").nombre("Juan Pérez").correo("juan@rincondelsur.cl").telefono("987654321").contrasena(clave).cargo("Observador de terreno").turno("Turno Diurno").rolId(4L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál es tu comida favorita?").respuestaSeguridad("cazuela").build(),
                    Usuario.builder().rut("18111222-3").nombre("Camila Soto").correo("c.soto@rincondelsur.cl").telefono("911223344").contrasena(clave).cargo("Observador de terreno").turno("Turno Vespertino").rolId(4L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál es el segundo nombre de tu madre?").respuestaSeguridad("elena").build(),
                    Usuario.builder().rut("19222333-4").nombre("Mateo Silva").correo("m.silva@rincondelsur.cl").telefono("955667788").contrasena(clave).cargo("Observador de terreno").turno("Turno Diurno").rolId(4L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál fue tu primer trabajo?").respuestaSeguridad("vendedor").build(),
                    Usuario.builder().rut("17333444-5").nombre("Valentina Rojas").correo("v.rojas@rincondelsur.cl").telefono("999887766").contrasena(clave).cargo("Observador de terreno").turno("Turno Vespertino").rolId(4L).activo(true).primerIngreso(false).preguntaSeguridad("¿Cuál es el nombre de tu mejor amigo de la infancia?").respuestaSeguridad("lucas").build()
            );

            usuarioRepository.saveAll(usuariosIniciales);
            log.info("Carga inicial completada: {} usuarios registrados exitosamente", usuariosIniciales.size());
        }
    }
}