package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActividadEvaluativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ActividadEvaluativaRepository extends JpaRepository<ActividadEvaluativa, Long> {

    List<ActividadEvaluativa> findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(Long asignacionId, Long periodoId);

    List<ActividadEvaluativa> findByAsignacionAcademicaIdAndPeriodoAcademicoId(Long asignacionId, Long periodoId);

    @Query("SELECT COALESCE(SUM(a.porcentaje), 0) FROM ActividadEvaluativa a WHERE a.asignacionAcademica.id = :asignacionId AND a.periodoAcademico.id = :periodoId AND a.activo = true")
    BigDecimal sumPorcentajeByAsignacionAndPeriodo(@Param("asignacionId") Long asignacionId, @Param("periodoId") Long periodoId);
}
