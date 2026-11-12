package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.GradoAsignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradoAsignaturaRepository extends JpaRepository<GradoAsignatura, Long> {

    List<GradoAsignatura> findByGradoIdAndActivoTrue(Long gradoId);

    List<GradoAsignatura> findByAsignaturaIdAndActivoTrue(Long asignaturaId);

    Optional<GradoAsignatura> findByGradoIdAndAsignaturaId(Long gradoId, Long asignaturaId);

    boolean existsByGradoIdAndAsignaturaId(Long gradoId, Long asignaturaId);

    boolean existsByGradoId(Long gradoId);

    boolean existsByAsignaturaId(Long asignaturaId);
}
