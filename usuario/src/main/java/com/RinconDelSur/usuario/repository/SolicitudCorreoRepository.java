package com.RinconDelSur.usuario.repository;

import com.RinconDelSur.usuario.model.SolicitudCorreo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SolicitudCorreoRepository extends JpaRepository<SolicitudCorreo, Long> {
    List<SolicitudCorreo> findByEstadoOrderByIdDesc(String estado);
    Optional<SolicitudCorreo> findFirstByRutUsuarioAndEstadoOrderByIdDesc(String rutUsuario, String estado);
}