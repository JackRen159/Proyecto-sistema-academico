package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AlertaSeguridadAuditoria;
import com.jackren.sistemaacademico.entity.TipoAlertaSeguridad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaSeguridadAuditoriaRepository extends JpaRepository<AlertaSeguridadAuditoria, Long> {
    List<AlertaSeguridadAuditoria> findByLeidaPorRectorFalseOrderByFechaAlertaDesc();
    List<AlertaSeguridadAuditoria> findByTipoAlerta(TipoAlertaSeguridad tipoAlerta);
}
