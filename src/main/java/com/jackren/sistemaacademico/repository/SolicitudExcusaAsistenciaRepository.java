package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoSolicitudExcusa;
import com.jackren.sistemaacademico.entity.SolicitudExcusaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudExcusaAsistenciaRepository extends JpaRepository<SolicitudExcusaAsistencia, Long> {

    Optional<SolicitudExcusaAsistencia> findByNumeroRadicado(String numeroRadicado);

    List<SolicitudExcusaAsistencia> findByMatriculaIdOrderByFechaRadicacionDesc(Long matriculaId);

    List<SolicitudExcusaAsistencia> findByEstado(EstadoSolicitudExcusa estado);

    long countByEstado(EstadoSolicitudExcusa estado);

    @Query("SELECT s FROM SolicitudExcusaAsistencia s WHERE s.matricula.id = :matriculaId AND s.estado = 'APROBADA' AND s.fechaInicio <= :fecha AND s.fechaFin >= :fecha")
    List<SolicitudExcusaAsistencia> findAprobadasCubriendoFecha(
            @Param("matriculaId") Long matriculaId,
            @Param("fecha") LocalDate fecha
    );

    @Query("SELECT s FROM SolicitudExcusaAsistencia s WHERE s.matricula.id = :matriculaId AND s.estado = 'APROBADA' AND s.fechaInicio <= :fechaFin AND s.fechaFin >= :fechaInicio")
    List<SolicitudExcusaAsistencia> findAprobadasEnRango(
            @Param("matriculaId") Long matriculaId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
