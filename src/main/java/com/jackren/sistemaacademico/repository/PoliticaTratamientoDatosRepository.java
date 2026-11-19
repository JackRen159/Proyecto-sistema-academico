package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.PoliticaTratamientoDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PoliticaTratamientoDatosRepository extends JpaRepository<PoliticaTratamientoDatos, Long> {

    Optional<PoliticaTratamientoDatos> findFirstByEsVigenteTrueOrderByNumeroVersionDesc();

    Optional<PoliticaTratamientoDatos> findByNumeroVersion(Integer numeroVersion);

    List<PoliticaTratamientoDatos> findAllByOrderByNumeroVersionDesc();
}
