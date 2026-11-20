package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.DiaSemana;
import com.jackren.sistemaacademico.entity.EstadoHorarioVersion;
import com.jackren.sistemaacademico.entity.HorarioBloque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HorarioBloqueRepository extends JpaRepository<HorarioBloque, Long> {

    List<HorarioBloque> findByHorarioVersionId(Long horarioVersionId);

    List<HorarioBloque> findByAsignacionAcademicaId(Long asignacionAcademicaId);

    List<HorarioBloque> findByHorarioVersionIdAndDiaSemana(Long horarioVersionId, DiaSemana diaSemana);

    Optional<HorarioBloque> findByHorarioVersionIdAndDiaSemanaAndFranjaHorariaId(
            Long horarioVersionId, DiaSemana diaSemana, Long franjaHorariaId);

    // Consulta de colisión de docente en versión publicada o borrador
    @Query("SELECT b FROM HorarioBloque b WHERE b.asignacionAcademica.profesor.id = :profesorId " +
           "AND b.diaSemana = :diaSemana AND b.franjaHoraria.id = :franjaId " +
           "AND b.horarioVersion.estado = :estadoVersion AND b.id != :bloqueIdIgnorar")
    List<HorarioBloque> findColisionesDocente(
            @Param("profesorId") Long profesorId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("franjaId") Long franjaId,
            @Param("estadoVersion") EstadoHorarioVersion estadoVersion,
            @Param("bloqueIdIgnorar") Long bloqueIdIgnorar
    );

    // Consulta de colisión de aula
    @Query("SELECT b FROM HorarioBloque b WHERE b.aula.id = :aulaId " +
           "AND b.diaSemana = :diaSemana AND b.franjaHoraria.id = :franjaId " +
           "AND b.horarioVersion.estado = :estadoVersion AND b.id != :bloqueIdIgnorar")
    List<HorarioBloque> findColisionesAula(
            @Param("aulaId") Long aulaId,
            @Param("diaSemana") DiaSemana diaSemana,
            @Param("franjaId") Long franjaId,
            @Param("estadoVersion") EstadoHorarioVersion estadoVersion,
            @Param("bloqueIdIgnorar") Long bloqueIdIgnorar
    );

    // Consulta de agenda del docente
    @Query("SELECT b FROM HorarioBloque b WHERE b.asignacionAcademica.profesor.id = :profesorId " +
           "AND b.horarioVersion.estado = 'PUBLICADO_VIGENTE' ORDER BY b.diaSemana, b.franjaHoraria.numeroBloque")
    List<HorarioBloque> findAgendaDocenteVigente(@Param("profesorId") Long profesorId);

    // Conteo de horas semanales asignadas a una asignatura en una versión
    @Query("SELECT COUNT(b) FROM HorarioBloque b WHERE b.horarioVersion.id = :versionId " +
           "AND b.asignacionAcademica.asignatura.id = :asignaturaId")
    long countBloquesAsignadosAsignatura(
            @Param("versionId") Long versionId,
            @Param("asignaturaId") Long asignaturaId
    );
}
