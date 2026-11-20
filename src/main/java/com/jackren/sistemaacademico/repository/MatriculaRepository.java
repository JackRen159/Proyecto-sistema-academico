package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoMatricula;
import com.jackren.sistemaacademico.entity.Matricula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    Optional<Matricula> findByNumeroMatricula(String numeroMatricula);

    boolean existsByNumeroMatricula(String numeroMatricula);

    /**
     * Cuenta la cantidad de estudiantes formalmente matriculados en un grupo
     * para validar el cupo máximo permitido.
     */
    long countByGrupoIdAndEstado(Long grupoId, EstadoMatricula estado);

    /**
     * Invariante de unicidad: valida si el estudiante ya tiene una matrícula activa en el año lectivo.
     */
    boolean existsByEstudianteIdAndAnioLectivoIdAndEstado(Long estudianteId, Long anioLectivoId, EstadoMatricula estado);

    Optional<Matricula> findByEstudianteIdAndAnioLectivoIdAndEstado(Long estudianteId, Long anioLectivoId, EstadoMatricula estado);

    List<Matricula> findByGrupoIdAndEstado(Long grupoId, EstadoMatricula estado);

    @Query("SELECT m FROM Matricula m WHERE m.grupo.id = :grupoId ORDER BY m.estudiante.apellidos ASC, m.estudiante.nombres ASC")
    List<Matricula> findListadoOficialPorGrupo(@Param("grupoId") Long grupoId);

    boolean existsByEstudianteIdAndAnioLectivoIdAndEstadoIn(Long estudianteId, Long anioLectivoId, List<EstadoMatricula> estados);

    long countByGrupoIdAndEstadoIn(Long grupoId, List<EstadoMatricula> estados);

    List<Matricula> findByEstudianteIdOrderByFechaMatriculaDesc(Long estudianteId);

    List<Matricula> findByAnioLectivoIdAndEstado(Long anioLectivoId, EstadoMatricula estado);

    Page<Matricula> findByAnioLectivoId(Long anioLectivoId, Pageable pageable);

    /**
     * Comprobación contextual (Spec S2): Valida si el estudiante cuenta con matrícula
     * activa en un grupo específico para recibir calificaciones o registrar asistencia.
     */
    boolean existsByEstudianteIdAndGrupoIdAndEstado(Long estudianteId, Long grupoId, EstadoMatricula estado);

    @Query("SELECT COUNT(m) FROM Matricula m WHERE m.anioLectivo.id = :anioLectivoId")
    long countByAnioLectivoId(@Param("anioLectivoId") Long anioLectivoId);
}
