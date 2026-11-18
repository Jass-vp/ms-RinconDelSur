package com.RinconDelSur.registros_conteos.repository;

import com.RinconDelSur.registros_conteos.model.Conteo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConteoRepository extends JpaRepository<Conteo, Long> {

    List<Conteo> findAllByOrderByIdDesc();
}
