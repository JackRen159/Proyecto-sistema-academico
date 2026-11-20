package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoPromocionEstudiante;
import com.jackren.sistemaacademico.entity.PromocionEstudianteAnual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromocionEstudianteAnualRepository extends JpaRepository<PromocionEstudianteAnual, Long> {

    Optional<PromocionEstudianteAnual> findByMatriculaId(Long matriculaId);

    List<PromocionEstudianteAnual> findByActaPromocionId(Long actaPromocionId);

    List<PromocionEstudianteAnual> findByEstadoPromocion(EstadoPromocionEstudiante estado);

    boolean existsByMatriculaId(Long matriculaId);

    long countByActaPromocionAnioLectivoIdAndEstadoPromocion(Long anioLectivoId, EstadoPromocionEstudiante estado);
}
