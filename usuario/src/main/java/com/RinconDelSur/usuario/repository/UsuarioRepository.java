package com.RinconDelSur.usuario.repository;

import com.RinconDelSur.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    List<Usuario> findAllByOrderByNombreAsc();

    List<Usuario> findByNombreContainingIgnoreCaseOrCorreoContainingIgnoreCaseOrderByNombreAsc(String nombre, String correo);

    Optional<Usuario> findByCorreo(String correo);

    Optional<Usuario> findByCorreoOrRut(String correo, String rut);

    boolean existsByCorreo(String correo);

    boolean existsByRut(String rut);
    Optional<Usuario> findByRut(String rut);

    boolean existsByCorreoAndIdNot(String correo, Long id);

    @Query("SELECT DISTINCT u.preguntaSeguridad FROM Usuario u WHERE u.preguntaSeguridad IS NOT NULL AND u.preguntaSeguridad <> ''")
    List<String> obtenerPreguntasDisponibles();
}
