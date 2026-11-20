package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.BoletinPublicadoVersion;
import com.jackren.sistemaacademico.entity.EstadoBoletinVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoletinPublicadoVersionRepository extends JpaRepository<BoletinPublicadoVersion, Long> {

    List<BoletinPublicadoVersion> findByMatriculaIdAndPeriodoAcademicoIdOrderByNumeroVersionDesc(Long matriculaId, Long periodoId);

    Optional<BoletinPublicadoVersion> findFirstByMatriculaIdAndPeriodoAcademicoIdAndEstadoBoletinOrderByNumeroVersionDesc(
            Long matriculaId, Long periodoId, EstadoBoletinVersion estadoBoletin);

    Optional<BoletinPublicadoVersion> findByMatriculaIdAndPeriodoAcademicoIdAndNumeroVersion(
            Long matriculaId, Long periodoId, Integer numeroVersion);

    long countByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoId);
}
