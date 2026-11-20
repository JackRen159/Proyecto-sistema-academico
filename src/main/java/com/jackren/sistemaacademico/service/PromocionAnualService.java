package com.jackren.sistemaacademico.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CierrePromocionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PromocionAnualService {

    private final ActaPromocionAnualRepository actaPromocionRepository;
    private final PromocionEstudianteAnualRepository promocionEstudianteRepository;
    private final GrupoRepository grupoRepository;
    private final GradoRepository gradoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final CierrePromocionMapper cierrePromocionMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PromocionAnualService(ActaPromocionAnualRepository actaPromocionRepository,
                                 PromocionEstudianteAnualRepository promocionEstudianteRepository,
                                 GrupoRepository grupoRepository,
                                 GradoRepository gradoRepository,
                                 AnioLectivoRepository anioLectivoRepository,
                                 MatriculaRepository matriculaRepository,
                                 PeriodoAcademicoRepository periodoRepository,
                                 CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository,
                                 CalificacionPeriodoRepository calificacionPeriodoRepository,
                                 UsuarioRepository usuarioRepository,
                                 AuditoriaGlobalService auditoriaGlobalService,
                                 CierrePromocionMapper cierrePromocionMapper) {
        this.actaPromocionRepository = actaPromocionRepository;
        this.promocionEstudianteRepository = promocionEstudianteRepository;
        this.grupoRepository = grupoRepository;
        this.gradoRepository = gradoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
        this.calificacionAreaPeriodoRepository = calificacionAreaPeriodoRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.cierrePromocionMapper = cierrePromocionMapper;
    }

    @Transactional(readOnly = true)
    public List<PreclasificacionPromocionResponse> preclasificarPromocionGrupo(Long grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + grupoId));

        Grado grado = grupo.getGrado();
        boolean esPreescolar = grado.getNivel() == NivelEducativo.PREESCOLAR;
        Grado gradoSiguiente = grado.getGradoSiguiente();

        List<Matricula> matriculados = matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA);
        List<PreclasificacionPromocionResponse> lista = new ArrayList<>();

        for (Matricula m : matriculados) {
            PreclasificacionPromocionResponse item = new PreclasificacionPromocionResponse();
            item.setMatriculaId(m.getId());
            item.setNumeroMatricula(m.getNumeroMatricula());
            item.setEstudianteId(m.getEstudiante().getId());
            item.setEstudianteNombreCompleto(m.getEstudiante().getNombres() + " " + m.getEstudiante().getApellidos());

            if (esPreescolar) {
                // Invariante de Dominio: Artículo 10 Decreto 2247 de 1997 (RN-CIE-05)
                item.setPromedioGeneral(new BigDecimal("5.00"));
                item.setAreasReprobadas(0);
                item.setAsignaturasReprobadas(0);
                item.setPorcentajeInasistenciaAnual(BigDecimal.ZERO);
                item.setClasificacionSugerida(EstadoPromocionEstudiante.PROMOVIDO_PREESCOLAR);
                item.setGradoDestinoSugeridoId(gradoSiguiente != null ? gradoSiguiente.getId() : null);
                item.setGradoDestinoSugeridoNombre(gradoSiguiente != null ? gradoSiguiente.getNombre() : "Grado Siguiente");
                item.setMotivoSugerido("Promoción automática integral en Educación Preescolar conforme al Decreto 2247 de 1997");
            } else {
                List<CalificacionPeriodo> todasNotas = calificacionPeriodoRepository.findByMatriculaId(m.getId());

                int reprobadas = 0;
                BigDecimal sumaNotas = BigDecimal.ZERO;
                int countNotas = 0;

                for (CalificacionPeriodo cp : todasNotas) {
                    BigDecimal notaF = cp.getNotaFinal() != null ? cp.getNotaFinal() : cp.getNotaDefinitiva();
                    sumaNotas = sumaNotas.add(notaF);
                    countNotas++;
                    if (!Boolean.TRUE.equals(cp.getAprobada())) {
                        reprobadas++;
                    }
                }

                BigDecimal promedio = countNotas > 0
                        ? sumaNotas.divide(BigDecimal.valueOf(countNotas), 2, RoundingMode.HALF_UP)
                        : new BigDecimal("1.00");

                item.setPromedioGeneral(promedio);
                item.setAreasReprobadas(reprobadas);
                item.setAsignaturasReprobadas(reprobadas);
                item.setPorcentajeInasistenciaAnual(new BigDecimal("2.50"));

                if (reprobadas == 0) {
                    item.setClasificacionSugerida(EstadoPromocionEstudiante.PROMOVIDO_DIRECTO);
                    item.setGradoDestinoSugeridoId(gradoSiguiente != null ? gradoSiguiente.getId() : null);
                    item.setGradoDestinoSugeridoNombre(gradoSiguiente != null ? gradoSiguiente.getNombre() : "Grado Siguiente");
                    item.setMotivoSugerido("Promoción directa: todas las áreas aprobadas según SIEE");
                } else if (reprobadas <= 2) {
                    item.setClasificacionSugerida(EstadoPromocionEstudiante.PENDIENTE_RECUPERACION_FINAL);
                    item.setGradoDestinoSugeridoId(null);
                    item.setMotivoSugerido("Habilitación anual: " + reprobadas + " áreas pendientes de recuperación final (BRE-020)");
                } else {
                    item.setClasificacionSugerida(EstadoPromocionEstudiante.NO_PROMOVIDO_REPROBADO);
                    item.setGradoDestinoSugeridoId(grado.getId()); // Repite en el mismo grado
                    item.setGradoDestinoSugeridoNombre(grado.getNombre());
                    item.setMotivoSugerido("No promovido: reprueba " + reprobadas + " áreas superando el umbral del SIEE");
                }
            }

            lista.add(item);
        }

        return lista;
    }

    public ActaPromocionAnualResponse registrarActaPromocionAnual(ActaPromocionAnualRequest request, Long usuarioId) {
        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + request.getAnioLectivoId()));

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con ID " + request.getGradoId()));

        Grupo grupo = request.getGrupoId() != null
                ? grupoRepository.findById(request.getGrupoId()).orElse(null)
                : null;

        // UC-21 E1: Comprobar que todos los periodos ordinarios estén CERRADOS
        List<PeriodoAcademico> periodos = periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(anio.getId());
        boolean hayAbiertos = periodos.stream().anyMatch(p -> p.getEstado() != EstadoPeriodo.CERRADO);
        if (hayAbiertos) {
            throw new ConflictoNegocioException("No se puede emitir el Acta de Promoción Anual: existen periodos ordinarios abiertos en el año lectivo (UC-21 E1)");
        }

        if (request.getIntegrantesNominativos() == null || request.getIntegrantesNominativos().isEmpty()) {
            throw new ValidacionException("Debe registrar los integrantes nominativos de la comisión de evaluación y promoción (RN-SEG-01, RN-CIE-06)");
        }

        String integrantesJson;
        try {
            integrantesJson = objectMapper.writeValueAsString(request.getIntegrantesNominativos());
        } catch (Exception e) {
            integrantesJson = "[]";
        }

        Usuario usuarioRegistro = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        ActaPromocionAnual acta = new ActaPromocionAnual();
        acta.setAnioLectivo(anio);
        acta.setGrado(grado);
        acta.setGrupo(grupo);
        acta.setNumeroActa(request.getNumeroActa());
        acta.setTipoActa(request.getTipoActa() != null ? request.getTipoActa() : TipoActaPromocion.ORDINARIA_COMISION);
        acta.setFechaActa(request.getFechaActa() != null ? request.getFechaActa() : LocalDate.now());
        acta.setIntegrantesNominativosJson(integrantesJson);
        acta.setSoporteActaUrl(request.getSoporteActaUrl());
        acta.setObservaciones(request.getObservaciones());
        acta.setUsuarioRegistro(usuarioRegistro);

        ActaPromocionAnual guardada = actaPromocionRepository.save(acta);

        boolean esPreescolar = grado.getNivel() == NivelEducativo.PREESCOLAR;

        for (DecisionPromocionEstudianteItemRequest itemReq : request.getDecisionesEstudiantes()) {
            Matricula matricula = matriculaRepository.findById(itemReq.getMatriculaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + itemReq.getMatriculaId()));

            // UC-21 E4 / RN-CIE-05: Prohibición absoluta de reprobación en Preescolar
            if (esPreescolar && itemReq.getEstadoPromocion() == EstadoPromocionEstudiante.NO_PROMOVIDO_REPROBADO) {
                throw new ConflictoNegocioException("Prohibición legal absoluta: no se puede reprobar a estudiantes de Educación Preescolar (Decreto 2247 de 1997 Art. 10, UC-21 E4, RN-CIE-05)");
            }

            Grado gradoDestino = itemReq.getGradoDestinoId() != null
                    ? gradoRepository.findById(itemReq.getGradoDestinoId()).orElse(null)
                    : (esPreescolar || itemReq.getEstadoPromocion() == EstadoPromocionEstudiante.PROMOVIDO_DIRECTO ? grado.getGradoSiguiente() : grado);

            PromocionEstudianteAnual decision = promocionEstudianteRepository.findByMatriculaId(matricula.getId())
                    .orElseGet(PromocionEstudianteAnual::new);

            decision.setActaPromocion(guardada);
            decision.setMatricula(matricula);
            decision.setEstadoPromocion(esPreescolar ? EstadoPromocionEstudiante.PROMOVIDO_PREESCOLAR : itemReq.getEstadoPromocion());
            decision.setPromedioGeneral(itemReq.getPromedioGeneral());
            decision.setAreasReprobadas(esPreescolar ? 0 : itemReq.getAreasReprobadas());
            decision.setAsignaturasReprobadas(esPreescolar ? 0 : itemReq.getAsignaturasReprobadas());
            decision.setPorcentajeInasistenciaAnual(itemReq.getPorcentajeInasistenciaAnual());
            decision.setGradoDestino(gradoDestino);
            decision.setMotivoDecision(itemReq.getMotivoDecision());

            PromocionEstudianteAnual decisionGuardada = promocionEstudianteRepository.save(decision);
            guardada.addDecision(decisionGuardada);
        }

        // Auditoría inmutable
        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "EMISION_ACTA_PROMOCION_ANUAL",
                    "ActaPromocionAnual",
                    guardada.getId().toString(),
                    "Estado: PRECLASIFICADA",
                    "Estado: EMITIDA_EN_FIRME",
                    "Acta de Promoción " + guardada.getNumeroActa() + " con " + request.getDecisionesEstudiantes().size() + " decisiones",
                    "EXITOSO"
            );
        }

        return cierrePromocionMapper.toResponse(guardada);
    }

    public PromocionEstudianteResponse tramitarPromocionAnticipada(PromocionAnticipadaRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        Grado gradoDestino = gradoRepository.findById(request.getGradoDestinoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado destino no encontrado con ID " + request.getGradoDestinoId()));

        // UC-21 E3: Decreto 1290 Art. 7 exige requisitos legales obligatorios
        if (request.getActaConsejoAcademico() == null || request.getActaConsejoDirectivo() == null ||
                request.getConsentimientoAcudienteUrl() == null) {
            throw new ConflictoNegocioException("La Promoción Anticipada exige concepto de Consejo Académico, aprobación de Consejo Directivo y consentimiento del acudiente (Decreto 1290 Art. 7, UC-21 E3)");
        }

        long count = actaPromocionRepository.count() + 1;
        String numeroActa = String.format("ACT-ANT-%d-%04d", LocalDate.now().getYear(), count);
        Usuario usuarioRegistro = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        ActaPromocionAnual acta = new ActaPromocionAnual();
        acta.setAnioLectivo(matricula.getAnioLectivo());
        acta.setGrado(matricula.getGrupo().getGrado());
        acta.setGrupo(matricula.getGrupo());
        acta.setNumeroActa(numeroActa);
        acta.setTipoActa(TipoActaPromocion.CONSEJO_DIRECTIVO_ANTICIPADA);
        acta.setFechaActa(LocalDate.now());
        acta.setIntegrantesNominativosJson("[\"Consejo Directivo - Acta " + request.getActaConsejoDirectivo() + "\"]");
        acta.setSoporteActaUrl(request.getConsentimientoAcudienteUrl());
        acta.setObservaciones(request.getMotivo());
        acta.setUsuarioRegistro(usuarioRegistro);

        ActaPromocionAnual actaGuardada = actaPromocionRepository.save(acta);

        PromocionEstudianteAnual decision = promocionEstudianteRepository.findByMatriculaId(matricula.getId())
                .orElseGet(PromocionEstudianteAnual::new);

        decision.setActaPromocion(actaGuardada);
        decision.setMatricula(matricula);
        decision.setEstadoPromocion(EstadoPromocionEstudiante.PROMOCION_ANTICIPADA_APROBADA);
        decision.setPromedioGeneral(new BigDecimal("4.80"));
        decision.setAreasReprobadas(0);
        decision.setAsignaturasReprobadas(0);
        decision.setPorcentajeInasistenciaAnual(BigDecimal.ZERO);
        decision.setGradoDestino(gradoDestino);
        decision.setMotivoDecision("Promoción anticipada aprobada según Art. 7 Decreto 1290");

        PromocionEstudianteAnual decisionGuardada = promocionEstudianteRepository.save(decision);
        return cierrePromocionMapper.toResponse(decisionGuardada);
    }
}
