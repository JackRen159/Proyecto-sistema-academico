package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.RegistroEmisionDocumento;
import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroEmisionDocumentoRepository extends JpaRepository<RegistroEmisionDocumento, Long> {

    Optional<RegistroEmisionDocumento> findByCodigoVerificacion(String codigoVerificacion);

    Optional<RegistroEmisionDocumento> findByAnioLectivoIdAndTipoDocumentoAndNumeroConsecutivoCompleto(
            Long anioLectivoId, TipoDocumentoAcademico tipoDocumento, String numeroConsecutivoCompleto);

    List<RegistroEmisionDocumento> findByEstudianteIdOrderByFechaEmisionDesc(Long estudianteId);

    List<RegistroEmisionDocumento> findByMatriculaIdOrderByFechaEmisionDesc(Long matriculaId);

    List<RegistroEmisionDocumento> findByAnioLectivoIdAndTipoDocumentoOrderByFechaEmisionDesc(
            Long anioLectivoId, TipoDocumentoAcademico tipoDocumento);

    Optional<RegistroEmisionDocumento> findByMatriculaIdAndPeriodoAcademicoIdAndTipoDocumentoAndAnuladoFalse(
            Long matriculaId, Long periodoAcademicoId, TipoDocumentoAcademico tipoDocumento);

    Optional<RegistroEmisionDocumento> findByMatriculaIdAndTipoDocumentoAndAnuladoFalse(
            Long matriculaId, TipoDocumentoAcademico tipoDocumento);

    boolean existsByCodigoVerificacion(String codigoVerificacion);
}
