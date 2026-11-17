package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConsecutivoDocumento;
import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsecutivoDocumentoRepository extends JpaRepository<ConsecutivoDocumento, Long> {

    Optional<ConsecutivoDocumento> findByAnioLectivoIdAndTipoDocumento(Long anioLectivoId, TipoDocumentoAcademico tipoDocumento);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM ConsecutivoDocumento c WHERE c.anioLectivo.id = :anioLectivoId AND c.tipoDocumento = :tipoDocumento")
    Optional<ConsecutivoDocumento> findByAnioLectivoIdAndTipoDocumentoConBloqueo(@Param("anioLectivoId") Long anioLectivoId,
                                                                                  @Param("tipoDocumento") TipoDocumentoAcademico tipoDocumento);

    List<ConsecutivoDocumento> findByAnioLectivoId(Long anioLectivoId);

    boolean existsByAnioLectivoIdAndTipoDocumento(Long anioLectivoId, TipoDocumentoAcademico tipoDocumento);
}
