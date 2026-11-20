package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AsignacionAcademica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AsignacionAcademicaRepository extends JpaRepository<AsignacionAcademica, Long> {

    List<AsignacionAcademica> findByProfesorIdAndActivoTrue(Long profesorId);

    List<AsignacionAcademica> findByGrupoIdAndActivoTrue(Long grupoId);

    @Query("SELECT a FROM AsignacionAcademica a WHERE a.grupo.anioLectivo.id = :anioLectivoId AND a.activo = true")
    List<AsignacionAcademica> findByAnioLectivoId(@Param("anioLectivoId") Long anioLectivoId);

    Optional<AsignacionAcademica> findByGrupoIdAndAsignaturaId(Long grupoId, Long asignaturaId);

    boolean existsByGrupoIdAndAsignaturaId(Long grupoId, Long asignaturaId);

    boolean existsByGrupoIdAndAsignaturaIdAndIdNot(Long grupoId, Long asignaturaId, Long id);

    Optional<AsignacionAcademica> findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(Long grupoId);

    /**
     * Comprobación contextual de autorización docente (Spec S2):
     * Verifica si el usuario autenticado (con cuenta vinculada al profesor) está formalmente
     * asignado a la asignatura y grupo solicitados.
     */
    boolean existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue(Long usuarioId, Long grupoId, Long asignaturaId);

    boolean existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(Long usuarioId, Long grupoId);

    boolean existsByProfesorIdAndGrupoIdAndAsignaturaIdAndActivoTrue(Long profesorId, Long grupoId, Long asignaturaId);
}
