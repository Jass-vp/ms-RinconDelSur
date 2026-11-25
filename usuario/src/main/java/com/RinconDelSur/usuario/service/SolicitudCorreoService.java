package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.model.SolicitudCorreo;
import com.RinconDelSur.usuario.repository.SolicitudCorreoRepository;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolicitudCorreoService {

    private final SolicitudCorreoRepository solicitudRepo;
    private final UsuarioRepository usuarioRepo;

    public SolicitudCorreoResponse crear(SolicitudCorreoRequest req) {
        log.info("Creando solicitud de cambio de correo para RUT: {} ({} -> {})",
                req.rutUsuario(), req.correoActual(), req.correoNuevo());

        SolicitudCorreo nueva = new SolicitudCorreo();
        nueva.setRutUsuario(req.rutUsuario());
        nueva.setNombreUsuario(req.nombreUsuario());
        nueva.setCorreoActual(req.correoActual());
        nueva.setCorreoNuevo(req.correoNuevo());
        nueva.setMotivo(req.motivo());
        nueva.setEstado("PENDIENTE");

        SolicitudCorreo guardada = solicitudRepo.save(nueva);
        log.info("Solicitud de correo creada exitosamente con ID: {}", guardada.getId());
        return mapear(guardada);
    }

    public List<SolicitudCorreoResponse> listarPendientes() {
        log.debug("Consultando listado de solicitudes de cambio de correo pendientes");
        List<SolicitudCorreoResponse> pendientes = solicitudRepo.findByEstadoOrderByIdDesc("PENDIENTE")
                .stream()
                .map(this::mapear)
                .toList();
        log.debug("Se encontraron {} solicitudes pendientes", pendientes.size());
        return pendientes;
    }

    public SolicitudCorreoResponse obtenerPendientePorRut(String rut) {
        log.debug("Buscando solicitud pendiente de cambio de correo para RUT: {}", rut);
        return solicitudRepo.findFirstByRutUsuarioAndEstadoOrderByIdDesc(rut, "PENDIENTE")
                .map(this::mapear)
                .orElseThrow(() -> {
                    log.warn("No se encontró solicitud pendiente de cambio de correo para RUT: {}", rut);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin solicitud pendiente");
                });
    }

    @Transactional
    public void resolver(Long id, boolean aprobar) {
        log.info("Resolviendo solicitud de correo ID: {} | Decisión: {}", id, aprobar ? "APROBADA" : "RECHAZADA");

        SolicitudCorreo sol = solicitudRepo.findById(id)
                .orElseThrow(() -> {
                    log.warn("Solicitud de cambio de correo no encontrada con ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada");
                });

        sol.setEstado(aprobar ? "APROBADA" : "RECHAZADA");
        solicitudRepo.save(sol);

        if (aprobar) {
            usuarioRepo.findByRut(sol.getRutUsuario()).ifPresentOrElse(
                    usuario -> {
                        usuario.setCorreo(sol.getCorreoNuevo());
                        usuarioRepo.save(usuario);
                        log.info("Correo actualizado a '{}' para usuario con RUT: {}",
                                sol.getCorreoNuevo(), sol.getRutUsuario());
                    },
                    () -> log.warn("Solicitud ID {} aprobada, pero no se encontró usuario registrado con RUT: {}",
                            id, sol.getRutUsuario())
            );
        }
    }

    private SolicitudCorreoResponse mapear(SolicitudCorreo s) {
        return new SolicitudCorreoResponse(
                s.getId(), s.getRutUsuario(), s.getNombreUsuario(),
                s.getCorreoActual(), s.getCorreoNuevo(), s.getMotivo(), s.getEstado()
        );
    }
}