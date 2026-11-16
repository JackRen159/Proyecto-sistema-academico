package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.JustificacionAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JustificacionAsistenciaRepository extends JpaRepository<JustificacionAsistencia, Long> {

    Optional<JustificacionAsistencia> findByRegistroAsistenciaId(Long registroId);

    List<JustificacionAsistencia> findByRegistroAsistenciaMatriculaIdOrderByFechaRadicacionDesc(Long matriculaId);
}
