package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.CalificacionPeriodo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionPeriodoRepository extends JpaRepository<CalificacionPeriodo, Long> {

    Optional<CalificacionPeriodo> findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(
            Long matriculaId, Long asignaturaId, Long periodoAcademicoId);

    List<CalificacionPeriodo> findByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoAcademicoId);

    List<CalificacionPeriodo> findByMatriculaId(Long matriculaId);

    List<CalificacionPeriodo> findByAsignaturaIdAndPeriodoAcademicoId(Long asignaturaId, Long periodoAcademicoId);

    List<CalificacionPeriodo> findByPeriodoAcademicoIdAndMatriculaGrupoId(Long periodoId, Long grupoId);

    boolean existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(
            Long matriculaId, Long asignaturaId, Long periodoAcademicoId);
}
