package com.jackren.sistemaacademico.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CierrePromocionMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
public class ComisionPeriodicaService {

    private final ObservacionBoletinGrupoRepository observacionRepository;
    private final ActaComisionEvaluacionPeriodicaRepository actaComisionRepository;
    private final MatriculaRepository matriculaRepository;
    private final PeriodoAcademicoRepository periodoRepository;
    private final GradoRepository gradoRepository;
    private final ProfesorRepository profesorRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final CierrePromocionMapper cierrePromocionMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ComisionPeriodicaService(ObservacionBoletinGrupoRepository observacionRepository,
                                   ActaComisionEvaluacionPeriodicaRepository actaComisionRepository,
                                   MatriculaRepository matriculaRepository,
                                   PeriodoAcademicoRepository periodoRepository,
                                   GradoRepository gradoRepository,
                                   ProfesorRepository profesorRepository,
                                   AsignacionAcademicaRepository asignacionRepository,
                                   UsuarioRepository usuarioRepository,
                                   CierrePromocionMapper cierrePromocionMapper) {
        this.observacionRepository = observacionRepository;
        this.actaComisionRepository = actaComisionRepository;
        this.matriculaRepository = matriculaRepository;
        this.periodoRepository = periodoRepository;
        this.gradoRepository = gradoRepository;
        this.profesorRepository = profesorRepository;
        this.asignacionRepository = asignacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.cierrePromocionMapper = cierrePromocionMapper;
    }

    public ObservacionBoletinGrupoResponse registrarObservacionGrupo(ObservacionBoletinGrupoRequest request, Long usuarioId, String rol) {
        Matricula matricula = matriculaRepository.findById(request.getMatriculaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + request.getMatriculaId()));

        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        Grupo grupo = matricula.getGrupo();

        // UC-PROV-23 E1 / RN-09: Solo el Director de Grupo asignado o directivos pueden registrar observaciones
        Profesor director = null;
        if ("PROFESOR".equalsIgnoreCase(rol)) {
            director = profesorRepository.findByUsuarioId(usuarioId)
                    .orElseThrow(() -> new AccessDeniedException("Perfil de profesor no encontrado"));

            final Long docId = director.getId();
            boolean esDirector = asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupo.getId())
                    .map(a -> a.getProfesor() != null && a.getProfesor().getId().equals(docId))
                    .orElse(false);

            if (!esDirector) {
                throw new AccessDeniedException("Acceso denegado: solo el Director de Grupo asignado puede registrar observaciones del boletín para este curso (UC-PROV-23 E1)");
            }
        }

        ObservacionBoletinGrupo obs = observacionRepository
                .findByMatriculaIdAndPeriodoAcademicoId(matricula.getId(), periodo.getId())
                .orElseGet(ObservacionBoletinGrupo::new);

        obs.setMatricula(matricula);
        obs.setPeriodoAcademico(periodo);
        obs.setDocenteDirector(director);
        obs.setObservacionFormativa(request.getObservacionFormativa());
        obs.setRecomendacionesPedagogicas(request.getRecomendacionesPedagogicas());

        ObservacionBoletinGrupo guardada = observacionRepository.save(obs);
        return cierrePromocionMapper.toResponse(guardada);
    }

    public ActaComisionPeriodicaResponse registrarActaComisionPeriodica(ActaComisionPeriodicaRequest request, Long usuarioId) {
        PeriodoAcademico periodo = periodoRepository.findById(request.getPeriodoAcademicoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Periodo académico no encontrado con ID " + request.getPeriodoAcademicoId()));

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado no encontrado con ID " + request.getGradoId()));

        if (request.getIntegrantesNominativos() == null || request.getIntegrantesNominativos().isEmpty()) {
            throw new ValidacionException("Debe registrar los integrantes nominativos de la comisión de evaluación (UC-PROV-23 E2)");
        }

        Usuario secretario = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;

        String integrantesJson;
        try {
            integrantesJson = objectMapper.writeValueAsString(request.getIntegrantesNominativos());
        } catch (Exception e) {
            integrantesJson = "[]";
        }

        ActaComisionEvaluacionPeriodica acta = new ActaComisionEvaluacionPeriodica();
        acta.setPeriodoAcademico(periodo);
        acta.setGrado(grado);
        acta.setNumeroActa(request.getNumeroActa());
        acta.setFechaSesion(request.getFechaSesion() != null ? request.getFechaSesion() : LocalDate.now());
        acta.setIntegrantesNominativosJson(integrantesJson);
        acta.setConclusionesGenerales(request.getConclusionesGenerales());
        acta.setCompromisosPedagogicosJson(request.getCompromisosPedagogicosJson());
        acta.setUsuarioSecretario(secretario);

        ActaComisionEvaluacionPeriodica guardada = actaComisionRepository.save(acta);
        return cierrePromocionMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public ObservacionBoletinGrupoResponse obtenerObservacionEstudiante(Long matriculaId, Long periodoId) {
        return observacionRepository.findByMatriculaIdAndPeriodoAcademicoId(matriculaId, periodoId)
                .map(cierrePromocionMapper::toResponse)
                .orElse(null);
    }
}
