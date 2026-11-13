package com.RinconDelSur.roles.service;

import com.RinconDelSur.roles.exception.RecursoNoEncontradoException;
import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.repository.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    public Rol obtener(Long id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con ID: " + id));
    }

    public Rol actualizar(Long id, Rol datos) {
        Rol rol = obtener(id);
        if (datos.getNombre() != null) rol.setNombre(datos.getNombre());
        if (datos.getDescripcion() != null) rol.setDescripcion(datos.getDescripcion());
        if (datos.getHabilitado() != null) rol.setHabilitado(datos.getHabilitado());
        return rolRepository.save(rol);
    }
}
