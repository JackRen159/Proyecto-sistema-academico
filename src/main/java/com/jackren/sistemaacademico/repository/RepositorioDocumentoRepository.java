package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoCicloVidaDocumental;
import com.jackren.sistemaacademico.entity.NivelConfidencialidadDocumental;
import com.jackren.sistemaacademico.entity.RepositorioDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioDocumentoRepository extends JpaRepository<RepositorioDocumento, Long> {
    Optional<RepositorioDocumento> findByCodigoDocumento(String codigoDocumento);
    List<RepositorioDocumento> findByEstudianteId(Long estudianteId);
    List<RepositorioDocumento> findByEstudianteIdAndEstadoCicloVida(Long estudianteId, EstadoCicloVidaDocumental estado);
    List<RepositorioDocumento> findByEstudianteIdAndNivelConfidencialidadIn(Long estudianteId, List<NivelConfidencialidadDocumental> niveles);
    List<RepositorioDocumento> findByEstadoCicloVida(EstadoCicloVidaDocumental estado);
    boolean existsByCodigoDocumento(String codigoDocumento);
}
