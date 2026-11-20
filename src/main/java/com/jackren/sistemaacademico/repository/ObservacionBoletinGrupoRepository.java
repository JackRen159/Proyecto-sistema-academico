package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ObservacionBoletinGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ObservacionBoletinGrupoRepository extends JpaRepository<ObservacionBoletinGrupo, Long> {

    Optional<ObservacionBoletinGrupo> findByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoId);

    boolean existsByMatriculaIdAndPeriodoAcademicoId(Long matriculaId, Long periodoId);
}
