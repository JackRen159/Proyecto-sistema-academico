package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConvalidacionCalificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConvalidacionCalificacionRepository extends JpaRepository<ConvalidacionCalificacion, Long> {

    Optional<ConvalidacionCalificacion> findByNumeroActa(String numeroActa);

    List<ConvalidacionCalificacion> findByMatriculaId(Long matriculaId);

    List<ConvalidacionCalificacion> findByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoAcademicoId);
}
