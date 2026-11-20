package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AuditoriaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditoriaAsistenciaRepository extends JpaRepository<AuditoriaAsistencia, Long> {

    List<AuditoriaAsistencia> findByMatriculaIdOrderByFechaAuditoriaDesc(Long matriculaId);

    List<AuditoriaAsistencia> findByRegistroAsistenciaIdOrderByFechaAuditoriaDesc(Long registroId);
}
