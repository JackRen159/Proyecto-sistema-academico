package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.SesionAsistencia;
import com.jackren.sistemaacademico.entity.TipoRegistroAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SesionAsistenciaRepository extends JpaRepository<SesionAsistencia, Long> {

    List<SesionAsistencia> findByGrupoIdAndPeriodoAcademicoIdOrderByFechaDescBloqueHorarioDesc(Long grupoId, Long periodoId);

    List<SesionAsistencia> findByAsignacionAcademicaIdAndPeriodoAcademicoIdOrderByFechaDescBloqueHorarioDesc(Long asignacionId, Long periodoId);

    Optional<SesionAsistencia> findByAsignacionAcademicaIdAndFechaAndBloqueHorario(
            Long asignacionId, LocalDate fecha, Integer bloqueHorario);

    Optional<SesionAsistencia> findByGrupoIdAndFechaAndTipoRegistro(
            Long grupoId, LocalDate fecha, TipoRegistroAsistencia tipoRegistro);

    long countByAsignacionAcademicaIdAndPeriodoAcademicoId(Long asignacionId, Long periodoId);

    long countByGrupoIdAndPeriodoAcademicoIdAndTipoRegistro(Long grupoId, Long periodoId, TipoRegistroAsistencia tipoRegistro);
}
