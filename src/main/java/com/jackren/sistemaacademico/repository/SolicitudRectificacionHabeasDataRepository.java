package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoSolicitudRectificacion;
import com.jackren.sistemaacademico.entity.SolicitudRectificacionHabeasData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudRectificacionHabeasDataRepository extends JpaRepository<SolicitudRectificacionHabeasData, Long> {

    Optional<SolicitudRectificacionHabeasData> findByNumeroRadicado(String numeroRadicado);

    List<SolicitudRectificacionHabeasData> findByEstudianteIdOrderByFechaRadicacionDesc(Long estudianteId);

    Page<SolicitudRectificacionHabeasData> findByEstadoOrderByFechaRadicacionAsc(
            EstadoSolicitudRectificacion estado, Pageable pageable);
}
