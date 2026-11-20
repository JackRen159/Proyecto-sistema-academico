package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.HistorialNovedadMatricula;
import com.jackren.sistemaacademico.entity.TipoNovedadMatricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialNovedadMatriculaRepository extends JpaRepository<HistorialNovedadMatricula, Long> {

    List<HistorialNovedadMatricula> findByMatriculaIdOrderByFechaEfectivaDesc(Long matriculaId);

    List<HistorialNovedadMatricula> findByTipoNovedad(TipoNovedadMatricula tipoNovedad);

    List<HistorialNovedadMatricula> findByMatriculaEstudianteIdOrderByFechaEfectivaDesc(Long estudianteId);
}
