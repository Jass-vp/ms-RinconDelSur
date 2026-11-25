package com.RinconDelSur.conteo.repository;

import com.RinconDelSur.conteo.model.Conteo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConteoRepository extends JpaRepository<Conteo, Long> {

    List<Conteo> findAllByOrderByIdDesc();
}
