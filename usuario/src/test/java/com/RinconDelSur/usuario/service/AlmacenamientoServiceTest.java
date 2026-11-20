package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.exception.PeticionInvalidaException;
import com.RinconDelSur.usuario.exception.RecursoNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class AlmacenamientoServiceTest {

    @TempDir
    Path tempDir;

    private AlmacenamientoService service;

    @BeforeEach
    void setUp() {
        service = new AlmacenamientoService(tempDir.toString());
    }

    @Test
    void guardar_imagenValida_retornaNombreYGuardaEnDisco() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "foto.jpg", "image/jpeg", "contenido-imagen".getBytes());

        String nombreGenerado = service.guardar(archivo);

        assertNotNull(nombreGenerado);
        assertTrue(nombreGenerado.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(nombreGenerado)));
    }

    @Test
    void guardar_archivoVacio_lanzaPeticionInvalida() {
        MockMultipartFile archivoVacio = new MockMultipartFile(
                "archivo", "vacio.jpg", "image/jpeg", new byte[0]);

        assertThrows(PeticionInvalidaException.class, () -> service.guardar(archivoVacio));
    }

    @Test
    void guardar_archivoNulo_lanzaPeticionInvalida() {
        assertThrows(PeticionInvalidaException.class, () -> service.guardar(null));
    }

    @Test
    void guardar_tipoMimeNoImagen_lanzaPeticionInvalida() {
        MockMultipartFile archivoTexto = new MockMultipartFile(
                "archivo", "doc.pdf", "application/pdf", "contenido".getBytes());

        assertThrows(PeticionInvalidaException.class, () -> service.guardar(archivoTexto));
    }

    @Test
    void leer_archivoExistente_retornaBytes() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "foto.png", "image/png", "bytes-png".getBytes());
        String nombre = service.guardar(archivo);

        byte[] resultado = service.leer(nombre);

        assertArrayEquals("bytes-png".getBytes(), resultado);
    }

    @Test
    void leer_archivoInexistente_lanzaRecursoNoEncontrado() {
        assertThrows(RecursoNoEncontradoException.class, () -> service.leer("no-existe.jpg"));
    }

    @Test
    void eliminar_archivoExistente_loBorraDeDisco() {
        MockMultipartFile archivo = new MockMultipartFile(
                "archivo", "foto.jpg", "image/jpeg", "datos".getBytes());
        String nombre = service.guardar(archivo);

        service.eliminar(nombre);

        assertFalse(Files.exists(tempDir.resolve(nombre)));
    }

    @Test
    void eliminar_nombreNuloOInexistente_noFalla() {
        assertDoesNotThrow(() -> service.eliminar(null));
        assertDoesNotThrow(() -> service.eliminar("fantasma.jpg"));
    }
}