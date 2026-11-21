package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoExpedienteDebidoProceso;
import com.jackren.sistemaacademico.entity.ExpedienteDebidoProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpedienteDebidoProcesoRepository extends JpaRepository<ExpedienteDebidoProceso, Long> {
    Optional<ExpedienteDebidoProceso> findByNumeroExpediente(String numeroExpediente);
    List<ExpedienteDebidoProceso> findByCasoConvivenciaId(Long casoConvivenciaId);
    List<ExpedienteDebidoProceso> findByEstudianteId(Long estudianteId);
    List<ExpedienteDebidoProceso> findByEstadoExpediente(EstadoExpedienteDebidoProceso estadoExpediente);
    boolean existsByNumeroExpediente(String numeroExpediente);
}
