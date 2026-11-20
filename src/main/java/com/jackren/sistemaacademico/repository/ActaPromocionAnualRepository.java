package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActaPromocionAnual;
import com.jackren.sistemaacademico.entity.TipoActaPromocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActaPromocionAnualRepository extends JpaRepository<ActaPromocionAnual, Long> {

    Optional<ActaPromocionAnual> findByNumeroActa(String numeroActa);

    List<ActaPromocionAnual> findByAnioLectivoIdAndGradoId(Long anioLectivoId, Long gradoId);

    List<ActaPromocionAnual> findByAnioLectivoId(Long anioLectivoId);

    List<ActaPromocionAnual> findByAnioLectivoIdAndTipoActa(Long anioLectivoId, TipoActaPromocion tipoActa);
}
