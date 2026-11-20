package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoTitulacionGraduando;
import com.jackren.sistemaacademico.entity.TitulacionGraduandoMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TitulacionGraduandoMediaRepository extends JpaRepository<TitulacionGraduandoMedia, Long> {

    Optional<TitulacionGraduandoMedia> findByMatriculaId(Long matriculaId);

    List<TitulacionGraduandoMedia> findByEstadoTitulacion(EstadoTitulacionGraduando estado);

    Optional<TitulacionGraduandoMedia> findByNumeroDiploma(String numeroDiploma);

    Optional<TitulacionGraduandoMedia> findByNumeroActaIndividual(String numeroActaIndividual);

    boolean existsByMatriculaId(Long matriculaId);
}
