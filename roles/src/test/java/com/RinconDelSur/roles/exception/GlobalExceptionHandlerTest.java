package com.RinconDelSur.roles.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void manejarNoEncontrado_DebeRetornar404YErrorResponse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/roles/99");

        RecursoNoEncontradoException ex = new RecursoNoEncontradoException("Rol no encontrado con ID: 99");
        ResponseEntity<ErrorResponse> response = handler.manejarNoEncontrado(ex, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(404, response.getBody().status());
        assertEquals("Not Found", response.getBody().error());
        assertEquals("Rol no encontrado con ID: 99", response.getBody().mensaje());
        assertEquals("/api/roles/99", response.getBody().ruta());
        assertNotNull(response.getBody().timestamp());
    }

    @Test
    void manejarGenerico_DebeRetornar500YErrorResponse() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/roles");

        Exception ex = new RuntimeException("Error inesperado en base de datos");
        ResponseEntity<ErrorResponse> response = handler.manejarGenerico(ex, request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500, response.getBody().status());
        assertEquals("Internal Server Error", response.getBody().error());
        assertEquals("Error inesperado en base de datos", response.getBody().mensaje());
        assertEquals("/api/roles", response.getBody().ruta());
    }
}