package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActaNivelacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActaNivelacionRepository extends JpaRepository<ActaNivelacion, Long> {

    Optional<ActaNivelacion> findByNumeroActa(String numeroActa);

    List<ActaNivelacion> findByMatriculaId(Long matriculaId);

    List<ActaNivelacion> findByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoAcademicoId);

    Optional<ActaNivelacion> findByCalificacionPeriodoId(Long calificacionPeriodoId);

    boolean existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(Long matriculaId, Long asignaturaId, Long periodoAcademicoId);

    long countByPeriodoAcademicoId(Long periodoAcademicoId);
}
