package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActuacionSeguimientoAusentismo;
import com.jackren.sistemaacademico.entity.TipoActuacionSeguimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActuacionSeguimientoAusentismoRepository extends JpaRepository<ActuacionSeguimientoAusentismo, Long> {

    List<ActuacionSeguimientoAusentismo> findByMatriculaIdOrderByFechaActuacionDesc(Long matriculaId);

    List<ActuacionSeguimientoAusentismo> findByMatriculaIdAndPeriodoAcademicoIdOrderByFechaActuacionDesc(
            Long matriculaId, Long periodoAcademicoId);

    boolean existsByMatriculaIdAndTipoActuacion(Long matriculaId, TipoActuacionSeguimiento tipoActuacion);

    long countByMatriculaIdAndTipoActuacion(Long matriculaId, TipoActuacionSeguimiento tipoActuacion);
}
