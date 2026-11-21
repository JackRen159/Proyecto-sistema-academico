package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.DescargoDebidoProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescargoDebidoProcesoRepository extends JpaRepository<DescargoDebidoProceso, Long> {
    List<DescargoDebidoProceso> findByExpedienteIdOrderByFechaRadicacionAsc(Long expedienteId);
    List<DescargoDebidoProceso> findByPresentadoPorUsuarioId(Long usuarioId);
}
