package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoReclamacionCalificacion;
import com.jackren.sistemaacademico.entity.ReclamacionCalificacion;
import com.jackren.sistemaacademico.entity.TipoCalificacionReclamacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReclamacionCalificacionRepository extends JpaRepository<ReclamacionCalificacion, Long> {

    Optional<ReclamacionCalificacion> findByNumeroRadicado(String numeroRadicado);

    List<ReclamacionCalificacion> findByMatriculaId(Long matriculaId);

    List<ReclamacionCalificacion> findByTipoCalificacionAndReferenciaId(TipoCalificacionReclamacion tipo, Long referenciaId);

    List<ReclamacionCalificacion> findByEstado(EstadoReclamacionCalificacion estado);

    long countByEstadoIn(Collection<EstadoReclamacionCalificacion> estados);

    boolean existsByTipoCalificacionAndReferenciaIdAndEstadoIn(
            TipoCalificacionReclamacion tipo,
            Long referenciaId,
            Collection<EstadoReclamacionCalificacion> estados
    );
}
