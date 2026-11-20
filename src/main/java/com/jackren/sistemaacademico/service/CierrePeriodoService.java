package com.jackren.sistemaacademico.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.CierrePromocionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CierrePeriodoService {

    private final PeriodoAcademicoRepository periodoRepository;
    private final GrupoRepository grupoRepository;
    private final MatriculaRepository matriculaRepository;
    private final BoletinPublicadoVersionRepository boletinRepository;
    private final CalificacionPeriodoRepository calificacionPeriodoRepository;
    private final CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValidadorPreCierreService validadorPreCierreService;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final CierrePromocionMapper cierrePromocionMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public CierrePeriodoService(PeriodoAcademicoRepository periodoRepository,
                                GrupoRepository grupoRepository,
                                MatriculaRepository matriculaRepository,
                                BoletinPublicadoVersionRepository boletinRepository,
                                CalificacionPeriodoRepository calificacionPeriodoRepository,
                                CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository,
                                UsuarioRepository usuarioRepository,
                                ValidadorPreCierreService validadorPreCierreService,
                                AuditoriaGlobalService auditoriaGlobalService,
                                CierrePromocionMapper cierrePromocionMapper) {
        this.periodoRepository = periodoRepository;
        this.grupoRepository = grupoRepository;
        this.matriculaRepository = matriculaRepository;
        this.boletinRepository = boletinRepository;
        this.calificacionPeriodoRepository = calificacionPeriodoRepository;
        this.calificacionAreaPeriodoRepository = calificacionAreaPeriodoRepository;
        this.usuarioRepository = usuarioRepository;
        this.validadorPreCierreService = validadorPreCierreService;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.cierrePromocionMapper = cierrePromocionMapper;
    }

    @Transactional(readOnly = true)
    public DiagnosticoPreCierreResponse diagnosticarPreCierre(Long periodoId) {
        return validadorPreCierreService.ejecutarDiagnostico(periodoId);
    }

    public List<BoletinPublicadoResponse> cerrarPeriodo(CierrePeriodoRequest request, Long usuarioId) {
        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoId()));

        if (periodo.getEstado() == EstadoPeriodo.CERRADO) {
            throw new ConflictoNegocioException("El periodo académico ya se encuentra en estado CERRADO");
        }

        // UC-20 E1: Validación obligatoria de condiciones de pre-cierre (RN-CIE-01)
        DiagnosticoPreCierreResponse diagnostico = validadorPreCierreService.ejecutarDiagnostico(periodo.getId());
        if (!Boolean.TRUE.equals(diagnostico.getAptoParaCierre())) {
            throw new ConflictoNegocioException("No se puede cerrar el periodo: existen bloqueos críticos pendientes de subsanar (RN-CIE-01, UC-20 E1): " +
                    String.join("; ", diagnostico.getBloqueosCriticos()));
        }

        Usuario publicador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        // 1. Cambiar estado del periodo a CERRADO bloqueando escrituras ordinarias (RN-CIE-04)
        periodo.setEstado(EstadoPeriodo.CERRADO);
        periodoRepository.save(periodo);

        // 2. Generar snapshots inmutables de boletines versionados para cada estudiante activo
        List<Grupo> grupos = grupoRepository.findByAnioLectivoIdAndActivoTrue(periodo.getAnioLectivo().getId());
        List<BoletinPublicadoResponse> boletinesGenerados = new ArrayList<>();

        for (Grupo g : grupos) {
            List<Matricula> matriculados = matriculaRepository.findByGrupoIdAndEstado(g.getId(), EstadoMatricula.MATRICULADA);

            for (Matricula mat : matriculados) {
                int siguienteVersion = (int) boletinRepository.countByMatriculaIdAndPeriodoAcademicoId(mat.getId(), periodo.getId()) + 1;

                List<CalificacionPeriodo> notasAsig = calificacionPeriodoRepository
                        .findByMatriculaIdAndPeriodoAcademicoId(mat.getId(), periodo.getId());
                List<CalificacionAreaPeriodo> notasArea = calificacionAreaPeriodoRepository
                        .findByMatriculaIdAndPeriodoAcademicoId(mat.getId(), periodo.getId());

                String payloadJson;
                try {
                    payloadJson = objectMapper.writeValueAsString(new SnapshotBoletinData(
                            mat.getId(), mat.getNumeroMatricula(), mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos(),
                            g.getNombre(), periodo.getNombre(), notasArea.size(), notasAsig.size()
                    ));
                } catch (Exception e) {
                    payloadJson = "{\"error\":\"Error serializando snapshot\"}";
                }

                BoletinPublicadoVersion b = new BoletinPublicadoVersion(
                        mat, periodo, siguienteVersion, EstadoBoletinVersion.PUBLICADO_OFICIAL,
                        payloadJson, publicador
                );
                BoletinPublicadoVersion guardado = boletinRepository.save(b);
                boletinesGenerados.add(cierrePromocionMapper.toResponse(guardado));
            }
        }

        // 3. Auditoría inmutable de cierre de periodo
        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CIERRE_PERIODO_V2",
                    "PeriodoAcademico",
                    periodo.getId().toString(),
                    "Estado: ABIERTO",
                    "Estado: CERRADO",
                    request.getMotivoCierre() != null ? request.getMotivoCierre() : "Cierre formal de periodo con emisión de boletines V2",
                    "EXITOSO"
            );
        }

        return boletinesGenerados;
    }

    public void reabrirPeriodo(ReaperturaPeriodoRequest request, Long usuarioId) {
        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoId()));

        if (periodo.getEstado() != EstadoPeriodo.CERRADO) {
            throw new ConflictoNegocioException("Solo se pueden reabrir periodos que se encuentren en estado CERRADO");
        }

        periodo.setEstado(EstadoPeriodo.ABIERTO);
        periodoRepository.save(periodo);

        // Marcar boletines previos como RECTIFICADO_HISTORICO
        List<Grupo> grupos = grupoRepository.findByAnioLectivoIdAndActivoTrue(periodo.getAnioLectivo().getId());
        for (Grupo g : grupos) {
            List<Matricula> mats = matriculaRepository.findByGrupoIdAndEstado(g.getId(), EstadoMatricula.MATRICULADA);
            for (Matricula m : mats) {
                boletinRepository.findFirstByMatriculaIdAndPeriodoAcademicoIdAndEstadoBoletinOrderByNumeroVersionDesc(
                        m.getId(), periodo.getId(), EstadoBoletinVersion.PUBLICADO_OFICIAL
                ).ifPresent(b -> {
                    b.setEstadoBoletin(EstadoBoletinVersion.RECTIFICADO_HISTORICO);
                    b.setFechaReapertura(LocalDateTime.now());
                    b.setMotivoReapertura(request.getMotivoReapertura());
                    boletinRepository.save(b);
                });
            }
        }

        // Auditoría reforzada
        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REAPERTURA_PERIODO_V2",
                    "PeriodoAcademico",
                    periodo.getId().toString(),
                    "Estado: CERRADO",
                    "Estado: ABIERTO",
                    "Reapertura excepcional: " + request.getMotivoReapertura(),
                    "EXITOSO"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<BoletinPublicadoResponse> listarBoletinesMatricula(Long matriculaId, Long periodoId) {
        return boletinRepository.findByMatriculaIdAndPeriodoAcademicoIdOrderByNumeroVersionDesc(matriculaId, periodoId).stream()
                .map(cierrePromocionMapper::toResponse)
                .toList();
    }

    private record SnapshotBoletinData(
            Long matriculaId,
            String numeroMatricula,
            String estudianteNombre,
            String grupoNombre,
            String periodoNombre,
            int totalAreas,
            int totalAsignaturas
    ) {}
}
