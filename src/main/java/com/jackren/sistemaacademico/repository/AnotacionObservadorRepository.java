package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AnotacionObservador;
import com.jackren.sistemaacademico.entity.TipoAnotacionObservador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnotacionObservadorRepository extends JpaRepository<AnotacionObservador, Long> {
    List<AnotacionObservador> findByEstudianteIdOrderByFechaRegistroDesc(Long estudianteId);
    List<AnotacionObservador> findByEstudianteIdAndPeriodoAcademicoId(Long estudianteId, Long periodoAcademicoId);
    List<AnotacionObservador> findByEstudianteIdAndTipoAnotacion(Long estudianteId, TipoAnotacionObservador tipoAnotacion);
    List<AnotacionObservador> findByAutorUsuarioId(Long autorUsuarioId);
    List<AnotacionObservador> findByCasoConvivenciaId(Long casoConvivenciaId);
}
