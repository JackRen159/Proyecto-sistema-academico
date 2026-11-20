package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActaComisionEvaluacionPeriodica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActaComisionEvaluacionPeriodicaRepository extends JpaRepository<ActaComisionEvaluacionPeriodica, Long> {

    Optional<ActaComisionEvaluacionPeriodica> findByNumeroActa(String numeroActa);

    List<ActaComisionEvaluacionPeriodica> findByPeriodoAcademicoIdAndGradoId(Long periodoId, Long gradoId);

    List<ActaComisionEvaluacionPeriodica> findByPeriodoAcademicoId(Long periodoId);
}
