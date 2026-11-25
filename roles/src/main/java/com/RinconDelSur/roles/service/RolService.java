package com.RinconDelSur.roles.service;

import com.RinconDelSur.roles.exception.RecursoNoEncontradoException;
import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RolService {

    private static final Logger logger = LoggerFactory.getLogger(RolService.class);
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Rol> listar() {
        logger.info("Consultando lista completa de roles...");
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Rol obtener(Long id) {
        logger.info("Consultando rol por ID: {}", id);
        return rolRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));
    }

    @Transactional
    public Rol actualizar(Long id, Rol datos) {
        logger.info("Actualizando rol ID: {}", id);
        Rol rol = obtener(id);
        if (datos.getNombre() != null) rol.setNombre(datos.getNombre());
        if (datos.getDescripcion() != null) rol.setDescripcion(datos.getDescripcion());
        if (datos.getHabilitado() != null) rol.setHabilitado(datos.getHabilitado());

        Rol actualizado = rolRepository.save(rol);
        logger.info("Rol actualizado ID: {} (nombre: {}, habilitado: {})",
                actualizado.getId(), actualizado.getNombre(), actualizado.getHabilitado());
        return actualizado;
    }
}