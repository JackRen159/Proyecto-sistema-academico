package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.PlanContinuidadAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanContinuidadAcademicaRepository extends JpaRepository<PlanContinuidadAcademica, Long> {
    Optional<PlanContinuidadAcademica> findByMedidaSancionId(Long medidaSancionId);
    List<PlanContinuidadAcademica> findByEstudianteId(Long estudianteId);
    List<PlanContinuidadAcademica> findByDocenteTutorSeguimientoId(Long profesorId);
    boolean existsByMedidaSancionId(Long medidaSancionId);
}
