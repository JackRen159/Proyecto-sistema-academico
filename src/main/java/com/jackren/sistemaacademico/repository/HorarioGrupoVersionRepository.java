package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoHorarioVersion;
import com.jackren.sistemaacademico.entity.HorarioGrupoVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioGrupoVersionRepository extends JpaRepository<HorarioGrupoVersion, Long> {

    List<HorarioGrupoVersion> findByGrupoIdAndEstado(Long grupoId, EstadoHorarioVersion estado);

    List<HorarioGrupoVersion> findByGrupoIdOrderByNumeroVersionDesc(Long grupoId);

    Optional<HorarioGrupoVersion> findFirstByGrupoIdAndEstadoOrderByNumeroVersionDesc(Long grupoId, EstadoHorarioVersion estado);

    Optional<HorarioGrupoVersion> findByGrupoIdAndNumeroVersion(Long grupoId, Integer numeroVersion);

    long countByGrupoId(Long grupoId);
}
