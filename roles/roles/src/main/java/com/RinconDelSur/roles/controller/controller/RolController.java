package com.RinconDelSur.roles.controller.controller;

import com.RinconDelSur.roles.model.Rol;
import com.RinconDelSur.roles.service.RolService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public List<Rol> listar() {
        return rolService.listar();
    }

    @GetMapping("/{id}")
    public Rol obtener(@PathVariable Long id) {
        return rolService.obtener(id);
    }

    @PutMapping("/{id}")
    public Rol actualizar(@PathVariable Long id, @RequestBody Rol datos) {
        return rolService.actualizar(id, datos);
    }
}
