package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoSolicitudDocumento;
import com.jackren.sistemaacademico.entity.SolicitudDocumentoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudDocumentoAcademicoRepository extends JpaRepository<SolicitudDocumentoAcademico, Long> {
    Optional<SolicitudDocumentoAcademico> findByNumeroRadicado(String numeroRadicado);
    List<SolicitudDocumentoAcademico> findByEstudianteId(Long estudianteId);
    List<SolicitudDocumentoAcademico> findBySolicitanteUsuarioId(Long usuarioId);
    List<SolicitudDocumentoAcademico> findByEstadoSolicitud(EstadoSolicitudDocumento estado);
    boolean existsByNumeroRadicado(String numeroRadicado);
}
