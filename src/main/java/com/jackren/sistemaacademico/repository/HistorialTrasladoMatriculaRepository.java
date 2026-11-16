package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.HistorialTrasladoMatricula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialTrasladoMatriculaRepository extends JpaRepository<HistorialTrasladoMatricula, Long> {

    List<HistorialTrasladoMatricula> findByMatriculaIdOrderByFechaTrasladoDesc(Long matriculaId);

    List<HistorialTrasladoMatricula> findByGrupoOrigenIdOrGrupoDestinoIdOrderByFechaTrasladoDesc(Long grupoOrigenId, Long grupoDestinoId);
}
