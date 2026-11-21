package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MedidaSancionService {

    private final MedidaSancionConvivenciaRepository medidaRepository;
    private final PlanContinuidadAcademicaRepository planContinuidadRepository;
    private final ExpedienteDebidoProcesoRepository expedienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesorRepository profesorRepository;
    private final MatriculaRepository matriculaRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public MedidaSancionService(
            MedidaSancionConvivenciaRepository medidaRepository,
            PlanContinuidadAcademicaRepository planContinuidadRepository,
            ExpedienteDebidoProcesoRepository expedienteRepository,
            UsuarioRepository usuarioRepository,
            ProfesorRepository profesorRepository,
            MatriculaRepository matriculaRepository,
            AuditoriaGlobalService auditoriaGlobalService) {
        this.medidaRepository = medidaRepository;
        this.planContinuidadRepository = planContinuidadRepository;
        this.expedienteRepository = expedienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.profesorRepository = profesorRepository;
        this.matriculaRepository = matriculaRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public MedidaSancionResponse decidirMedidaOSancion(MedidaSancionRequest request, Long autoridadFirmanteId) {
        ExpedienteDebidoProceso expediente = expedienteRepository.findById(request.expedienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente de debido proceso no encontrado con ID: " + request.expedienteId()));

        Usuario firmante = usuarioRepository.findById(autoridadFirmanteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario firmante no encontrado con ID: " + autoridadFirmanteId));

        // 1. Precondición bloqueante de debido proceso formal (RN-07, RN-CON-07, E1, E2)
        if (expediente.getEstadoExpediente() != EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION &&
            expediente.getEstadoExpediente() != EstadoExpedienteDebidoProceso.PLAZO_DESCARGOS_VENCIDO_CERTIFICADO) {
            throw new ConflictoNegocioException(
                    "El backend bloquea cualquier fallo sancionatorio si no se acreditó en debido proceso la notificación de cargos, pruebas y descargos recibidos o vencimiento certificado (RN-07, E1, E2)"
            );
        }

        // 2. Validación de competencia institucional (E3)
        Rol rolFirmante = firmante.getRol();
        boolean esSancionGrave = request.tipoMedida() == TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA ||
                request.tipoMedida() == TipoMedidaSancion.CANCELACION_MATRICULA_EXPULSION ||
                request.tipoMedida() == TipoMedidaSancion.PERDIDA_CUPO_SIGUIENTE_ANIO;

        if (esSancionGrave && rolFirmante != Rol.RECTOR) {
            throw new ConflictoNegocioException(
                    "Las sanciones disciplinarias de suspensión o exclusión son competencia privativa de Rectoría (E3)"
            );
        }

        // 3. Exigencia de Acta del Consejo Directivo para expulsión o pérdida de cupo (A3, E3, Ley 115 Art. 144)
        if (request.tipoMedida() == TipoMedidaSancion.CANCELACION_MATRICULA_EXPULSION ||
            request.tipoMedida() == TipoMedidaSancion.PERDIDA_CUPO_SIGUIENTE_ANIO) {
            if (request.actaConsejoDirectivo() == null || request.actaConsejoDirectivo().trim().isEmpty() ||
                request.fechaActaConsejoDirectivo() == null) {
                throw new ConflictoNegocioException(
                        "La sanción de cancelación de matrícula o pérdida de cupo exige Acta del Consejo Directivo y Resolución Rectoral motivada (A3, E3, Ley 115 Art. 144)"
                );
            }
        }

        // 4. Activación obligatoria del Plan de Continuidad Académica en suspensiones presenciales (RN-08, RN-CAL-05, Sentencia T-004 de 2026, E4)
        if (request.tipoMedida() == TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA) {
            if (request.planContinuidad() == null ||
                request.planContinuidad().guiasTalleresDescripcion() == null || request.planContinuidad().guiasTalleresDescripcion().trim().isEmpty() ||
                request.planContinuidad().mecanismoEvaluacion() == null || request.planContinuidad().mecanismoEvaluacion().trim().isEmpty()) {
                throw new ConflictoNegocioException(
                        "Toda suspensión temporal exige registrar y activar un Plan de Continuidad Académica para no privar al estudiante de su evaluación pedagógica (RN-08, Sentencia T-004 de 2026, E4)"
                );
            }
        }

        if (request.motivacionHechosNormas() == null || request.motivacionHechosNormas().trim().length() < 10) {
            throw new ValidacionException("La motivación fáctica y normativa es obligatoria (E4)");
        }
        if (request.analisisProporcionalidad() == null || request.analisisProporcionalidad().trim().isEmpty()) {
            throw new ValidacionException("El análisis de proporcionalidad y razonabilidad es obligatorio (E4)");
        }

        int anio = expediente.getCasoConvivencia().getAnioLectivo().getAnio();
        String prefijo = rolFirmante == Rol.RECTOR ? "RES-RECT-" : "RES-COORD-";
        String numeroResolucion = prefijo + anio + "-" + String.format("%04d", medidaRepository.count() + 1);

        MedidaSancionConvivencia medida = new MedidaSancionConvivencia();
        medida.setExpediente(expediente);
        medida.setNumeroResolucion(numeroResolucion);
        medida.setTipoMedida(request.tipoMedida());
        medida.setEsSancionDisciplinaria(request.esSancionDisciplinaria() != null && request.esSancionDisciplinaria());
        medida.setMotivacionHechosNormas(request.motivacionHechosNormas().trim());
        medida.setAnalisisProporcionalidad(request.analisisProporcionalidad().trim());
        medida.setDiasSuspension(request.diasSuspension());
        medida.setFechaInicioSuspension(request.fechaInicioSuspension());
        medida.setFechaFinSuspension(request.fechaFinSuspension());
        medida.setActaConsejoDirectivo(request.actaConsejoDirectivo());
        medida.setFechaActaConsejoDirectivo(request.fechaActaConsejoDirectivo());
        medida.setAutoridadFirmante(firmante);
        medida.setRolFirmante(rolFirmante.name());
        medida.setEstadoDecision(EstadoDecisionSancion.NOTIFICADA_EN_TERMINO_RECURSOS);
        medida.setFechaNotificacion(LocalDateTime.now());

        MedidaSancionConvivencia guardada = medidaRepository.save(medida);

        // Actualizar estado del expediente
        expediente.setEstadoExpediente(EstadoExpedienteDebidoProceso.FALLO_PRIMERA_INSTANCIA_EMITIDO);
        expedienteRepository.save(expediente);

        // Guardar Plan de Continuidad Académica si aplica
        PlanContinuidadResponse planResp = null;
        if (request.tipoMedida() == TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA && request.planContinuidad() != null) {
            PlanContinuidadAcademica plan = new PlanContinuidadAcademica();
            plan.setMedidaSancion(guardada);
            plan.setEstudiante(expediente.getEstudiante());
            plan.setFechaInicio(request.fechaInicioSuspension() != null ? request.fechaInicioSuspension() : java.time.LocalDate.now());
            plan.setFechaFin(request.fechaFinSuspension() != null ? request.fechaFinSuspension() : plan.getFechaInicio().plusDays(request.diasSuspension() != null ? request.diasSuspension() : 3));
            plan.setGuiasTalleresDescripcion(request.planContinuidad().guiasTalleresDescripcion().trim());
            plan.setMecanismoEvaluacion(request.planContinuidad().mecanismoEvaluacion().trim());

            if (request.planContinuidad().docenteTutorSeguimientoId() != null) {
                Profesor tutor = profesorRepository.findById(request.planContinuidad().docenteTutorSeguimientoId()).orElse(null);
                plan.setDocenteTutorSeguimiento(tutor);
            }

            PlanContinuidadAcademica planGuardado = planContinuidadRepository.save(plan);
            planResp = toPlanResponse(planGuardado);
        }

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_MEDIDA_SANCION",
                    "MedidaSancionConvivencia",
                    guardada.getId().toString(),
                    "N/A",
                    "Resolución: " + guardada.getNumeroResolucion() + ", Tipo: " + guardada.getTipoMedida(),
                    "Decisión formal motivada por " + firmante.getUsername(),
                    "EXITOSO"
            );
        }

        return toResponse(guardada, planResp);
    }

    @Transactional(readOnly = true)
    public MedidaSancionResponse obtenerPorId(Long medidaId) {
        MedidaSancionConvivencia m = medidaRepository.findById(medidaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Medida no encontrada con ID: " + medidaId));
        PlanContinuidadResponse plan = planContinuidadRepository.findByMedidaSancionId(medidaId)
                .map(this::toPlanResponse)
                .orElse(null);
        return toResponse(m, plan);
    }

    @Transactional(readOnly = true)
    public List<MedidaSancionResponse> listarPorExpediente(Long expedienteId) {
        return medidaRepository.findByExpedienteId(expedienteId).stream()
                .map(m -> {
                    PlanContinuidadResponse plan = planContinuidadRepository.findByMedidaSancionId(m.getId())
                            .map(this::toPlanResponse)
                            .orElse(null);
                    return toResponse(m, plan);
                })
                .toList();
    }

    public MedidaSancionResponse toResponse(MedidaSancionConvivencia m, PlanContinuidadResponse plan) {
        return new MedidaSancionResponse(
                m.getId(),
                m.getExpediente().getId(),
                m.getNumeroResolucion(),
                m.getTipoMedida(),
                m.getEsSancionDisciplinaria(),
                m.getMotivacionHechosNormas(),
                m.getAnalisisProporcionalidad(),
                m.getDiasSuspension(),
                m.getFechaInicioSuspension(),
                m.getFechaFinSuspension(),
                m.getActaConsejoDirectivo(),
                m.getFechaActaConsejoDirectivo(),
                m.getAutoridadFirmante().getId(),
                m.getAutoridadFirmante().getUsername(),
                m.getRolFirmante(),
                m.getEstadoDecision(),
                m.getFechaEmision(),
                m.getFechaNotificacion(),
                m.getFechaFirmeza(),
                plan
        );
    }

    public PlanContinuidadResponse toPlanResponse(PlanContinuidadAcademica p) {
        return new PlanContinuidadResponse(
                p.getId(),
                p.getMedidaSancion().getId(),
                p.getEstudiante().getId(),
                p.getEstudiante().getNombres() + " " + p.getEstudiante().getApellidos(),
                p.getFechaInicio(),
                p.getFechaFin(),
                p.getGuiasTalleresDescripcion(),
                p.getMecanismoEvaluacion(),
                p.getDocenteTutorSeguimiento() != null ? p.getDocenteTutorSeguimiento().getId() : null,
                p.getDocenteTutorSeguimiento() != null ? p.getDocenteTutorSeguimiento().getNombres() + " " + p.getDocenteTutorSeguimiento().getApellidos() : null,
                p.getCumplidoSatisfactoriamente(),
                p.getObservacionesEvaluacion(),
                p.getFechaRegistro()
        );
    }
}
