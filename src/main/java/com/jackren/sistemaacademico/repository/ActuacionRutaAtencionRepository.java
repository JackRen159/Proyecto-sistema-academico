package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActuacionRutaAtencion;
import com.jackren.sistemaacademico.entity.TipoActuacionRutaAtencion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActuacionRutaAtencionRepository extends JpaRepository<ActuacionRutaAtencion, Long> {
    List<ActuacionRutaAtencion> findByCasoConvivenciaIdOrderByFechaActuacionAsc(Long casoConvivenciaId);
    List<ActuacionRutaAtencion> findByCasoConvivenciaIdAndTipoActuacion(Long casoConvivenciaId, TipoActuacionRutaAtencion tipoActuacion);
}
