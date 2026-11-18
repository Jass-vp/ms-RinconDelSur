package com.RinconDelSur.accesos.repository;

import com.RinconDelSur.accesos.model.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccesoRepository extends JpaRepository<Acceso, Long> {
    List<Acceso> findByEstaHabilitadoTrue();
    Optional<Acceso> findByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCase(String codigo);
}