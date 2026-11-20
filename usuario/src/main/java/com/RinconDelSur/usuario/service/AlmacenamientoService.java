package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.exception.PeticionInvalidaException;
import com.RinconDelSur.usuario.exception.RecursoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AlmacenamientoService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenamientoService.class);

    private final Path carpeta;

    public AlmacenamientoService(@Value("${app.avatares.carpeta}") String carpeta) {
        this.carpeta = Path.of(carpeta).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.carpeta);
            log.info("Carpeta de avatares inicializada en: {}", this.carpeta);
        } catch (IOException e) {
            log.error("Error crítico al crear el directorio de avatares en: {}", this.carpeta, e);
            throw new IllegalStateException("No se pudo crear la carpeta de avatares: " + this.carpeta, e);
        }
    }

    public String guardar(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            log.warn("Intento de carga de avatar fallido: archivo vacío o nulo");
            throw new PeticionInvalidaException("No se recibió ninguna imagen");
        }

        String tipo = archivo.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            log.warn("Intento de carga de avatar con tipo MIME inválido: {}", tipo);
            throw new PeticionInvalidaException("El archivo debe ser una imagen");
        }

        String nombre = UUID.randomUUID() + ".jpg";
        try {
            Files.copy(archivo.getInputStream(), carpeta.resolve(nombre), StandardCopyOption.REPLACE_EXISTING);
            log.info("Avatar almacenado exitosamente con nombre: {} (tamaño: {} bytes)", nombre, archivo.getSize());
            return nombre;
        } catch (IOException e) {
            log.error("Error de E/S al guardar el avatar {} en disco", nombre, e);
            throw new RuntimeException("No se pudo guardar la imagen en disco", e);
        }
    }

    public byte[] leer(String nombre) {
        try {
            log.debug("Leyendo archivo de avatar desde disco: {}", nombre);
            return Files.readAllBytes(carpeta.resolve(nombre));
        } catch (IOException e) {
            log.warn("Avatar no encontrado en disco: {}", nombre);
            throw new RecursoNoEncontradoException("La imagen no existe en el servidor");
        }
    }

    public void eliminar(String nombre) {
        if (nombre == null) {
            return;
        }
        try {
            boolean eliminado = Files.deleteIfExists(carpeta.resolve(nombre));
            if (eliminado) {
                log.info("Avatar eliminado del sistema: {}", nombre);
            } else {
                log.debug("No se encontró el avatar para eliminar (ya no existía): {}", nombre);
            }
        } catch (IOException ignorada) {
            log.warn("No se pudo eliminar el avatar {} del sistema: {}", nombre, ignorada.getMessage());
        }
    }
}