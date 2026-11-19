package com.RinconDelSur.usuario.service;

import com.RinconDelSur.usuario.dto.SolicitudCorreoRequest;
import com.RinconDelSur.usuario.dto.SolicitudCorreoResponse;
import com.RinconDelSur.usuario.model.SolicitudCorreo;
import com.RinconDelSur.usuario.repository.SolicitudCorreoRepository;
import com.RinconDelSur.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudCorreoService {

    private final SolicitudCorreoRepository solicitudRepo;
    private final UsuarioRepository usuarioRepo;

    public SolicitudCorreoResponse crear(SolicitudCorreoRequest req) {
        SolicitudCorreo nueva = new SolicitudCorreo();
        nueva.setRutUsuario(req.rutUsuario());
        nueva.setNombreUsuario(req.nombreUsuario());
        nueva.setCorreoActual(req.correoActual());
        nueva.setCorreoNuevo(req.correoNuevo());
        nueva.setMotivo(req.motivo());
        nueva.setEstado("PENDIENTE");
        return mapear(solicitudRepo.save(nueva));
    }

    public List<SolicitudCorreoResponse> listarPendientes() {
        return solicitudRepo.findByEstadoOrderByIdDesc("PENDIENTE")
                .stream().map(this::mapear).toList();
    }

    public SolicitudCorreoResponse obtenerPendientePorRut(String rut) {
        return solicitudRepo.findFirstByRutUsuarioAndEstadoOrderByIdDesc(rut, "PENDIENTE")
                .map(this::mapear)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin solicitud pendiente"));
    }

    @Transactional
    public void resolver(Long id, boolean aprobar) {
        SolicitudCorreo sol = solicitudRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        sol.setEstado(aprobar ? "APROBADA" : "RECHAZADA");
        solicitudRepo.save(sol);

        if (aprobar) {
            usuarioRepo.findByRut(sol.getRutUsuario()).ifPresent(usuario -> {
                usuario.setCorreo(sol.getCorreoNuevo());
                usuarioRepo.save(usuario);
            });
        }
    }

    private SolicitudCorreoResponse mapear(SolicitudCorreo s) {
        return new SolicitudCorreoResponse(
                s.getId(), s.getRutUsuario(), s.getNombreUsuario(),
                s.getCorreoActual(), s.getCorreoNuevo(), s.getMotivo(), s.getEstado()
        );
    }
}