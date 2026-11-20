package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.CalificacionAreaPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionAreaPeriodoRepository extends JpaRepository<CalificacionAreaPeriodo, Long> {

    List<CalificacionAreaPeriodo> findByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoAcademicoId);

    Optional<CalificacionAreaPeriodo> findByMatriculaIdAndAreaIdAndPeriodoAcademicoId(Long matriculaId, Long areaId, Long periodoAcademicoId);

    List<CalificacionAreaPeriodo> findByPeriodoAcademicoIdAndAreaId(Long periodoAcademicoId, Long areaId);

    List<CalificacionAreaPeriodo> findByMatriculaId(Long matriculaId);
}
