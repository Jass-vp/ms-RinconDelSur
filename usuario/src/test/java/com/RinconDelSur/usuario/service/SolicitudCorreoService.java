package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.model.SolicitudCorreo;
import com.RinconDelSur.usuario.model.Usuario;
import com.RinconDelSur.usuario.repository.SolicitudCorreoRepository;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitudCorreoServiceTest {

    @Mock
    private SolicitudCorreoRepository solicitudRepo;

    @Mock
    private UsuarioRepository usuarioRepo;

    @InjectMocks
    private SolicitudCorreoService service;

    @Test
    void crear_datosValidos_guardaYRetornaPendiente() {
        SolicitudCorreoRequest req = new SolicitudCorreoRequest(
                "18.111.222-3", "Camila Soto", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Actualización");

        when(solicitudRepo.save(any(SolicitudCorreo.class))).thenAnswer(inv -> {
            SolicitudCorreo s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        SolicitudCorreoResponse resp = service.crear(req);

        assertEquals(1L, resp.id());
        assertEquals("PENDIENTE", resp.estado());
        assertEquals("nuevo@rincondelsur.cl", resp.correoNuevo());
    }

    @Test
    void listarPendientes_retornaListaMapeada() {
        SolicitudCorreo sol = new SolicitudCorreo(
                1L, "18.111.222-3", "Camila Soto", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Motivo", "PENDIENTE");
        when(solicitudRepo.findByEstadoOrderByIdDesc("PENDIENTE")).thenReturn(List.of(sol));

        List<SolicitudCorreoResponse> lista = service.listarPendientes();

        assertEquals(1, lista.size());
        assertEquals("18.111.222-3", lista.get(0).rutUsuario());
    }

    @Test
    void obtenerPendientePorRut_cuandoExiste_retornaResponse() {
        SolicitudCorreo sol = new SolicitudCorreo(
                2L, "18.111.222-3", "Camila Soto", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Motivo", "PENDIENTE");
        when(solicitudRepo.findFirstByRutUsuarioAndEstadoOrderByIdDesc("18.111.222-3", "PENDIENTE"))
                .thenReturn(Optional.of(sol));

        SolicitudCorreoResponse resp = service.obtenerPendientePorRut("18.111.222-3");

        assertEquals(2L, resp.id());
    }

    @Test
    void obtenerPendientePorRut_cuandoNoExiste_lanzaNotFound() {
        when(solicitudRepo.findFirstByRutUsuarioAndEstadoOrderByIdDesc("99.999.999-9", "PENDIENTE"))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.obtenerPendientePorRut("99.999.999-9"));
    }

    @Test
    void resolver_aprobarTrue_actualizaEstadoYCorreoUsuario() {
        SolicitudCorreo sol = new SolicitudCorreo(
                1L, "18.111.222-3", "Camila Soto", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Motivo", "PENDIENTE");
        Usuario usuario = Usuario.builder().id(5L).rut("18.111.222-3").correo("viejo@rincondelsur.cl").build();

        when(solicitudRepo.findById(1L)).thenReturn(Optional.of(sol));
        when(usuarioRepo.findByRut("18.111.222-3")).thenReturn(Optional.of(usuario));

        service.resolver(1L, true);

        assertEquals("APROBADA", sol.getEstado());
        assertEquals("nuevo@rincondelsur.cl", usuario.getCorreo());
        verify(solicitudRepo).save(sol);
        verify(usuarioRepo).save(usuario);
    }

    @Test
    void resolver_aprobarFalse_rechazaSinModificarUsuario() {
        SolicitudCorreo sol = new SolicitudCorreo(
                1L, "18.111.222-3", "Camila Soto", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Motivo", "PENDIENTE");
        when(solicitudRepo.findById(1L)).thenReturn(Optional.of(sol));

        service.resolver(1L, false);

        assertEquals("RECHAZADA", sol.getEstado());
        verify(solicitudRepo).save(sol);
        verifyNoInteractions(usuarioRepo);
    }

    @Test
    void resolver_solicitudInexistente_lanzaNotFound() {
        when(solicitudRepo.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> service.resolver(99L, true));
    }
    @Test
    void resolver_aprobarTrueCuandoUsuarioNoExiste_soloActualizaSolicitud() {
        SolicitudCorreo sol = new SolicitudCorreo(
                1L, "99.999.999-9", "Sin Cuenta", "viejo@rincondelsur.cl", "nuevo@rincondelsur.cl", "Motivo", "PENDIENTE");
        when(solicitudRepo.findById(1L)).thenReturn(Optional.of(sol));
        when(usuarioRepo.findByRut("99.999.999-9")).thenReturn(Optional.empty());

        service.resolver(1L, true);

        assertEquals("APROBADA", sol.getEstado());
        verify(solicitudRepo).save(sol);
        verify(usuarioRepo, never()).save(any());
    }
}