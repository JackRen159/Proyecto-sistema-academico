package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.CasoConvivencia;
import com.jackren.sistemaacademico.entity.EstadoCasoConvivencia;
import com.jackren.sistemaacademico.entity.TipoSituacionConvivencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CasoConvivenciaRepository extends JpaRepository<CasoConvivencia, Long> {
    Optional<CasoConvivencia> findByCodigoExpediente(String codigoExpediente);
    List<CasoConvivencia> findByAnioLectivoId(Long anioLectivoId);
    List<CasoConvivencia> findByEstudianteImplicadoId(Long estudianteId);
    List<CasoConvivencia> findByEstudianteImplicadoIdAndAnioLectivoId(Long estudianteId, Long anioLectivoId);
    List<CasoConvivencia> findByEstadoCaso(EstadoCasoConvivencia estadoCaso);
    List<CasoConvivencia> findByTipoSituacion(TipoSituacionConvivencia tipoSituacion);
    boolean existsByCodigoExpediente(String codigoExpediente);
}
