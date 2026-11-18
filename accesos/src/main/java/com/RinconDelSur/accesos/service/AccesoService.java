package com.RinconDelSur.accesos.service;

import com.RinconDelSur.accesos.dto.AccesoRequestDTO;
import com.RinconDelSur.accesos.dto.AccesoResponseDTO;
import com.RinconDelSur.accesos.exception.RecursoNoEncontradoException;
import com.RinconDelSur.accesos.model.Acceso;
import com.RinconDelSur.accesos.repository.AccesoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AccesoService {

    private static final Logger logger = LoggerFactory.getLogger(AccesoService.class);
    private final AccesoRepository accesoRepository;

    @Transactional(readOnly = true)
    public List<AccesoResponseDTO> listarTodos() {
        logger.info("Consultando todas las puertas de acceso...");
        return accesoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccesoResponseDTO> listarHabilitados() {
        logger.info("Consultando puertas de acceso habilitadas...");
        return accesoRepository.findByEstaHabilitadoTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccesoResponseDTO obtenerPorId(Long id) {
        logger.info("Consultando acceso por ID: {}", id);
        Acceso acceso = accesoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RecursoNoEncontradoException("Acceso no encontrado con ID: " + id));
        return toResponse(acceso);
    }

    @Transactional
    public AccesoResponseDTO crear(AccesoRequestDTO request) {
        logger.info("Creando nuevo punto de acceso: {}", request.getCodigo());
        if (accesoRepository.existsByCodigoIgnoreCase(request.getCodigo())) {
            throw new IllegalArgumentException("Ya existe un acceso con el código: " + request.getCodigo());
        }
        Acceso nuevo = Acceso.builder()
                .codigo(request.getCodigo().toUpperCase())
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .estaHabilitado(request.getEstaHabilitado() != null ? request.getEstaHabilitado() : true)
                .build();
        return toResponse(accesoRepository.save(nuevo));
    }

    @Transactional
    public AccesoResponseDTO actualizar(Long id, AccesoRequestDTO request) {
        logger.info("Actualizando acceso ID: {}", id);
        Acceso acceso = accesoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RecursoNoEncontradoException("Acceso no encontrado con ID: " + id));

        if (request.getCodigo() != null) acceso.setCodigo(request.getCodigo().toUpperCase());
        if (request.getNombre() != null) acceso.setNombre(request.getNombre());
        if (request.getDescripcion() != null) acceso.setDescripcion(request.getDescripcion());
        if (request.getEstaHabilitado() != null) acceso.setEstaHabilitado(request.getEstaHabilitado());

        return toResponse(accesoRepository.save(acceso));
    }

    @Transactional
    public AccesoResponseDTO cambiarEstado(Long id, boolean habilitado) {
        logger.info("Cambiando estado de acceso ID: {} a {}", id, habilitado);
        Acceso acceso = accesoRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RecursoNoEncontradoException("Acceso no encontrado con ID: " + id));
        acceso.setEstaHabilitado(habilitado);
        return toResponse(accesoRepository.save(acceso));
    }

    private AccesoResponseDTO toResponse(Acceso a) {
        return AccesoResponseDTO.builder()
                .id(a.getId())
                .codigo(a.getCodigo())
                .nombre(a.getNombre())
                .descripcion(a.getDescripcion())
                .estaHabilitado(a.getEstaHabilitado())
                .build();
    }
}