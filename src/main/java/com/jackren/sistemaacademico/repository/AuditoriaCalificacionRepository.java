package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AuditoriaCalificacion;
import com.jackren.sistemaacademico.entity.TipoEntidadAuditoriaCalificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaCalificacionRepository extends JpaRepository<AuditoriaCalificacion, Long> {

    List<AuditoriaCalificacion> findByMatriculaIdOrderByFechaAuditoriaDesc(Long matriculaId);

    List<AuditoriaCalificacion> findByTipoEntidadAndEntidadIdOrderByFechaAuditoriaDesc(
            TipoEntidadAuditoriaCalificacion tipoEntidad, Long entidadId);
}
