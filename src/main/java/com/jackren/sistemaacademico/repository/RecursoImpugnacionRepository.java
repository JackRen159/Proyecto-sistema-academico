package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoRecursoImpugnacion;
import com.jackren.sistemaacademico.entity.RecursoImpugnacion;
import com.jackren.sistemaacademico.entity.TipoRecursoImpugnacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecursoImpugnacionRepository extends JpaRepository<RecursoImpugnacion, Long> {
    List<RecursoImpugnacion> findByMedidaSancionId(Long medidaSancionId);
    List<RecursoImpugnacion> findByRecurrenteUsuarioId(Long usuarioId);
    List<RecursoImpugnacion> findByEstadoRecurso(EstadoRecursoImpugnacion estadoRecurso);
    List<RecursoImpugnacion> findByTipoRecurso(TipoRecursoImpugnacion tipoRecurso);
}
