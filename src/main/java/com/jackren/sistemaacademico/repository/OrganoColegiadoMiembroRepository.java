package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.OrganoColegiadoMiembro;
import com.jackren.sistemaacademico.entity.TipoOrganoColegiado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganoColegiadoMiembroRepository extends JpaRepository<OrganoColegiadoMiembro, Long> {

    List<OrganoColegiadoMiembro> findByUsuarioId(Long usuarioId);

    List<OrganoColegiadoMiembro> findByTipoOrganoAndAnioLectivoIdAndActivoTrue(TipoOrganoColegiado tipoOrgano, Long anioLectivoId);

    Optional<OrganoColegiadoMiembro> findByTipoOrganoAndUsuarioIdAndAnioLectivoId(
            TipoOrganoColegiado tipoOrgano, Long usuarioId, Long anioLectivoId);
}
