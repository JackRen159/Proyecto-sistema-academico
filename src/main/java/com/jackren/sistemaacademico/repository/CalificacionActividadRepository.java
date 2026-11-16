package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.CalificacionActividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalificacionActividadRepository extends JpaRepository<CalificacionActividad, Long> {

    Optional<CalificacionActividad> findByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);

    List<CalificacionActividad> findByActividadId(Long actividadId);

    List<CalificacionActividad> findByMatriculaId(Long matriculaId);

    List<CalificacionActividad> findByActividadPeriodoAcademicoIdAndMatriculaId(Long periodoId, Long matriculaId);

    boolean existsByActividadIdAndMatriculaId(Long actividadId, Long matriculaId);
}
