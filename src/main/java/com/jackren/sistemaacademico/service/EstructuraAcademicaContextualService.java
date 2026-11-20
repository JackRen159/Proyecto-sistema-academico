package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.DiaNoLectivoResponse;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.DiaNoLectivoMapper;
import com.jackren.sistemaacademico.mapper.GradoAsignaturaMapper;
import com.jackren.sistemaacademico.mapper.PeriodoAcademicoMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EstructuraAcademicaContextualService {

    private final UsuarioRepository usuarioRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final MatriculaRepository matriculaRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final DiaNoLectivoRepository diaNoLectivoRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final GradoAsignaturaMapper gradoAsignaturaMapper;
    private final PeriodoAcademicoMapper periodoAcademicoMapper;
    private final DiaNoLectivoMapper diaNoLectivoMapper;

    public EstructuraAcademicaContextualService(UsuarioRepository usuarioRepository,
                                                EstudianteRepository estudianteRepository,
                                                AcudienteRepository acudienteRepository,
                                                EstudianteAcudienteRepository estudianteAcudienteRepository,
                                                MatriculaRepository matriculaRepository,
                                                AnioLectivoRepository anioLectivoRepository,
                                                PeriodoAcademicoRepository periodoAcademicoRepository,
                                                DiaNoLectivoRepository diaNoLectivoRepository,
                                                GradoAsignaturaRepository gradoAsignaturaRepository,
                                                GradoAsignaturaMapper gradoAsignaturaMapper,
                                                PeriodoAcademicoMapper periodoAcademicoMapper,
                                                DiaNoLectivoMapper diaNoLectivoMapper) {
        this.usuarioRepository = usuarioRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.matriculaRepository = matriculaRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.diaNoLectivoRepository = diaNoLectivoRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.gradoAsignaturaMapper = gradoAsignaturaMapper;
        this.periodoAcademicoMapper = periodoAcademicoMapper;
        this.diaNoLectivoMapper = diaNoLectivoMapper;
    }

    public List<PeriodoAcademicoResponse> consultarPeriodosVigentes() {
        AnioLectivo anioActivo = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un año lectivo actualmente ACTIVO."));

        return periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(anioActivo.getId())
                .stream()
                .map(periodoAcademicoMapper::toResponse)
                .toList();
    }

    public List<DiaNoLectivoResponse> consultarCalendarioVigente() {
        AnioLectivo anioActivo = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un año lectivo actualmente ACTIVO."));

        return diaNoLectivoRepository.findByAnioLectivoIdAndActivoTrueOrderByFechaInicioAsc(anioActivo.getId())
                .stream()
                .map(diaNoLectivoMapper::toResponse)
                .toList();
    }

    public List<GradoAsignaturaResponse> consultarMiPlanEstudios(String username) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario: " + username));

        AnioLectivo anioActivo = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe un año lectivo actualmente ACTIVO."));

        Estudiante estudiante = null;
        if (usuario.getPersonaTipo() == TipoPersonaUsuario.ESTUDIANTE && usuario.getPersonaId() != null) {
            estudiante = estudianteRepository.findById(usuario.getPersonaId()).orElse(null);
        } else if (usuario.getPersonaTipo() == TipoPersonaUsuario.ACUDIENTE && usuario.getPersonaId() != null) {
            Acudiente acudiente = acudienteRepository.findById(usuario.getPersonaId()).orElse(null);
            if (acudiente != null) {
                List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository
                        .findByAcudienteAndEstadoVinculo(acudiente, EstadoVinculo.VERIFICADO);
                if (!vinculos.isEmpty()) {
                    estudiante = vinculos.getFirst().getEstudiante();
                }
            }
        }

        if (estudiante == null) {
            return Collections.emptyList();
        }

        return matriculaRepository.findByEstudianteIdAndAnioLectivoIdAndEstado(estudiante.getId(), anioActivo.getId(), EstadoMatricula.MATRICULADA)
                .map(matricula -> {
                    Long gradoId = matricula.getGrupo().getGrado().getId();
                    List<GradoAsignatura> lista = gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(gradoId, anioActivo.getId());
                    if (lista.isEmpty()) {
                        lista = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);
                    }
                    return lista.stream().map(gradoAsignaturaMapper::toResponse).toList();
                })
                .orElse(Collections.emptyList());
    }
}
