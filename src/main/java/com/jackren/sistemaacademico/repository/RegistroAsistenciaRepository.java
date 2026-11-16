package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoAsistencia;
import com.jackren.sistemaacademico.entity.RegistroAsistencia;
import com.jackren.sistemaacademico.entity.TipoRegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroAsistenciaRepository extends JpaRepository<RegistroAsistencia, Long> {

    List<RegistroAsistencia> findBySesionAsistenciaId(Long sesionId);

    Optional<RegistroAsistencia> findBySesionAsistenciaIdAndMatriculaId(Long sesionId, Long matriculaId);

    List<RegistroAsistencia> findByMatriculaId(Long matriculaId);

    List<RegistroAsistencia> findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(Long matriculaId, Long periodoId);

    @Query("SELECT COUNT(r) FROM RegistroAsistencia r WHERE r.matricula.id = :matriculaId AND r.sesionAsistencia.periodoAcademico.id = :periodoId AND r.sesionAsistencia.asignacionAcademica.id = :asignacionId")
    long countTotalSesionesEstudianteAsignatura(@Param("matriculaId") Long matriculaId,
                                               @Param("periodoId") Long periodoId,
                                               @Param("asignacionId") Long asignacionId);

    @Query("SELECT COUNT(r) FROM RegistroAsistencia r WHERE r.matricula.id = :matriculaId AND r.sesionAsistencia.periodoAcademico.id = :periodoId AND r.sesionAsistencia.asignacionAcademica.id = :asignacionId AND r.estado = :estado")
    long countByMatriculaAndPeriodoAndAsignacionAndEstado(@Param("matriculaId") Long matriculaId,
                                                         @Param("periodoId") Long periodoId,
                                                         @Param("asignacionId") Long asignacionId,
                                                         @Param("estado") EstadoAsistencia estado);

    @Query("SELECT COUNT(r) FROM RegistroAsistencia r WHERE r.matricula.id = :matriculaId AND r.sesionAsistencia.periodoAcademico.id = :periodoId AND r.sesionAsistencia.tipoRegistro = :tipoRegistro")
    long countTotalSesionesEstudianteDiaria(@Param("matriculaId") Long matriculaId,
                                           @Param("periodoId") Long periodoId,
                                           @Param("tipoRegistro") TipoRegistroAsistencia tipoRegistro);

    @Query("SELECT COUNT(r) FROM RegistroAsistencia r WHERE r.matricula.id = :matriculaId AND r.sesionAsistencia.periodoAcademico.id = :periodoId AND r.sesionAsistencia.tipoRegistro = :tipoRegistro AND r.estado = :estado")
    long countByMatriculaAndPeriodoAndTipoRegistroAndEstado(@Param("matriculaId") Long matriculaId,
                                                           @Param("periodoId") Long periodoId,
                                                           @Param("tipoRegistro") TipoRegistroAsistencia tipoRegistro,
                                                           @Param("estado") EstadoAsistencia estado);
}
