package com.RinconDelSur.usuario.controller;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.service.SolicitudCorreoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudCorreoControllerTest {

    @Mock
    private SolicitudCorreoService solicitudCorreoService;

    @InjectMocks
    private SolicitudCorreoController solicitudCorreoController;

    @Test
    void crear_debeRetornarSolicitudCreada() {
        SolicitudCorreoRequest request = new SolicitudCorreoRequest(
                "18.456.789-2",
                "Camila Soto",
                "camila.antiguo@rincondelsur.cl",
                "camila.soto@rincondelsur.cl",
                "Cambio de correo institucional"
        );
        SolicitudCorreoResponse response = mock(SolicitudCorreoResponse.class);

        when(solicitudCorreoService.crear(any(SolicitudCorreoRequest.class))).thenReturn(response);

        SolicitudCorreoResponse resultado = solicitudCorreoController.crear(request);

        assertNotNull(resultado);
        verify(solicitudCorreoService).crear(request);
    }

    @Test
    void listarPendientes_debeRetornarListaDeSolicitudes() {
        SolicitudCorreoResponse response = mock(SolicitudCorreoResponse.class);
        when(solicitudCorreoService.listarPendientes()).thenReturn(List.of(response));

        List<SolicitudCorreoResponse> resultado = solicitudCorreoController.listarPendientes();

        assertEquals(1, resultado.size());
        verify(solicitudCorreoService).listarPendientes();
    }

    @Test
    void obtenerPendientePorRut_debeRetornarSolicitud() {
        SolicitudCorreoResponse response = mock(SolicitudCorreoResponse.class);
        when(solicitudCorreoService.obtenerPendientePorRut("18.456.789-2")).thenReturn(response);

        SolicitudCorreoResponse resultado = solicitudCorreoController.obtenerPendientePorRut("18.456.789-2");

        assertNotNull(resultado);
        verify(solicitudCorreoService).obtenerPendientePorRut("18.456.789-2");
    }

    @Test
    void resolver_debeInvocarServicio() {
        solicitudCorreoController.resolver(1L, true);

        verify(solicitudCorreoService).resolver(1L, true);
    }
}