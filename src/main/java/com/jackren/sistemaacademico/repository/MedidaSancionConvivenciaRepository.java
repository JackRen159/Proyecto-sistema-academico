package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoDecisionSancion;
import com.jackren.sistemaacademico.entity.MedidaSancionConvivencia;
import com.jackren.sistemaacademico.entity.TipoMedidaSancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedidaSancionConvivenciaRepository extends JpaRepository<MedidaSancionConvivencia, Long> {
    Optional<MedidaSancionConvivencia> findByNumeroResolucion(String numeroResolucion);
    List<MedidaSancionConvivencia> findByExpedienteId(Long expedienteId);
    List<MedidaSancionConvivencia> findByEstadoDecision(EstadoDecisionSancion estadoDecision);
    List<MedidaSancionConvivencia> findByTipoMedida(TipoMedidaSancion tipoMedida);
    boolean existsByNumeroResolucion(String numeroResolucion);
}
