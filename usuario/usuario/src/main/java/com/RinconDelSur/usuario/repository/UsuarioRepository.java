package com.RinconDelSur.usuario.repository;

import com.RinconDelSur.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByNombreAsc();

    List<Usuario> findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(String nombre, String correo);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoOrRut(String correo, String rut);

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
