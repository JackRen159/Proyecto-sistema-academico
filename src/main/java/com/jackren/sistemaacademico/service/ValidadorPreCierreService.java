package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.DiagnosticoPreCierreResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ValidadorPreCierreService {

    private final PeriodoAcademicoRepository periodoRepository;
    private final GrupoRepository grupoRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final ActividadEvaluativaRepository actividadRepository;
    private final MatriculaRepository matriculaRepository;
    private final ReclamacionCalificacionRepository reclamacionRepository;
    private final SolicitudExcusaAsistenciaRepository excusaRepository;
    private final InstitucionDatosLegalesRepository institucionRepository;

    public ValidadorPreCierreService(PeriodoAcademicoRepository periodoRepository,
                                     GrupoRepository grupoRepository,
                                     AsignacionAcademicaRepository asignacionRepository,
                                     ActividadEvaluativaRepository actividadRepository,
                                     MatriculaRepository matriculaRepository,
                                     ReclamacionCalificacionRepository reclamacionRepository,
                                     SolicitudExcusaAsistenciaRepository excusaRepository,
                                     InstitucionDatosLegalesRepository institucionRepository) {
        this.periodoRepository = periodoRepository;
        this.grupoRepository = grupoRepository;
        this.asignacionRepository = asignacionRepository;
        this.actividadRepository = actividadRepository;
        this.matriculaRepository = matriculaRepository;
        this.reclamacionRepository = reclamacionRepository;
        this.excusaRepository = excusaRepository;
        this.institucionRepository = institucionRepository;
    }

    public DiagnosticoPreCierreResponse ejecutarDiagnostico(Long periodoId) {
        PeriodoAcademico periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + periodoId));

        DiagnosticoPreCierreResponse resp = new DiagnosticoPreCierreResponse();
        resp.setPeriodoId(periodo.getId());
        resp.setPeriodoNombre(periodo.getNombre());
        resp.setAnioLectivo(periodo.getAnioLectivo() != null ? periodo.getAnioLectivo().getAnio() : 2026);

        List<String> bloqueos = new ArrayList<>();

        // 1. Validar datos legales e institucionales (UC-PROV-13, RN-CFG-08)
        Optional<InstitucionDatosLegales> instOpt = institucionRepository.findFirstByActivoTrue();
        if (instOpt.isEmpty() || instOpt.get().getCodigoDane() == null) {
            bloqueos.add("Falta configurar la identificación legal DANE y firmas oficiales del establecimiento (UC-PROV-13, RN-CFG-08)");
        }

        // 2. Validar reclamaciones abiertas sobre calificaciones (UC-PROV-15, RN-CIE-01)
        List<EstadoReclamacionCalificacion> estadosAbiertos = List.of(
                EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA,
                EstadoReclamacionCalificacion.ESCALADA_SEGUNDA_INSTANCIA
        );
        long reclamosAbiertos = reclamacionRepository.countByEstadoIn(estadosAbiertos);
        if (reclamosAbiertos > 0) {
            bloqueos.add("Existen " + reclamosAbiertos + " reclamaciones académicas sobre calificaciones sin resolver (UC-PROV-15, RN-CIE-01)");
        }

        // 3. Validar excusas de asistencia pendientes de revisión (UC-PROV-18)
        long excusasPendientes = excusaRepository.countByEstado(EstadoSolicitudExcusa.PENDIENTE_REVISION);
        if (excusasPendientes > 0) {
            bloqueos.add("Existen " + excusasPendientes + " solicitudes de excusa médica o de fuerza mayor pendientes de validación (UC-PROV-18)");
        }

        // 4. Validar ponderaciones completas en asignaciones activas (UC-16 E1, RN-CAL-01)
        List<Grupo> grupos = grupoRepository.findByAnioLectivoIdAndActivoTrue(periodo.getAnioLectivo().getId());
        resp.setTotalGruposValidados(grupos.size());
        int totalEstudiantes = 0;

        for (Grupo g : grupos) {
            List<Matricula> matriculas = matriculaRepository.findByGrupoIdAndEstado(g.getId(), EstadoMatricula.MATRICULADA);
            totalEstudiantes += matriculas.size();

            List<AsignacionAcademica> asignaciones = asignacionRepository.findByGrupoIdAndActivoTrue(g.getId());
            for (AsignacionAcademica asig : asignaciones) {
                BigDecimal suma = actividadRepository.sumPorcentajeByAsignacionAndPeriodo(asig.getId(), periodo.getId());
                if (suma == null || suma.compareTo(new BigDecimal("100.00")) != 0) {
                    bloqueos.add("El curso " + g.getNombre() + " en la asignatura " + asig.getAsignatura().getNombre() +
                            " suma " + (suma != null ? suma : 0) + "% de ponderación en actividades evaluativas (debe ser exactamente 100.00% para cerrar, RN-CAL-01)");
                }
            }
        }

        resp.setTotalEstudiantesEvaluados(totalEstudiantes);

        if (!bloqueos.isEmpty()) {
            resp.setAptoParaCierre(false);
            resp.setBloqueosCriticos(bloqueos);
        } else {
            resp.setAptoParaCierre(true);
        }

        return resp;
    }
}
