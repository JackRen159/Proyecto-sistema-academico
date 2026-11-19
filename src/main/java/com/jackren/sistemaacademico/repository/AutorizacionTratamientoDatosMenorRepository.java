package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AutorizacionTratamientoDatosMenor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AutorizacionTratamientoDatosMenorRepository extends JpaRepository<AutorizacionTratamientoDatosMenor, Long> {

    Optional<AutorizacionTratamientoDatosMenor> findByEstudianteIdAndPoliticaId(Long estudianteId, Long politicaId);

    List<AutorizacionTratamientoDatosMenor> findByEstudianteIdOrderByFechaAutorizacionDesc(Long estudianteId);
}
