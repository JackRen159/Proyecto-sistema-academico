package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AsignacionAcademicaRequest;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsignacionAcademicaMapper;
import com.jackren.sistemaacademico.repository.AsignacionAcademicaRepository;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsignacionAcademicaService {

    private final AsignacionAcademicaRepository asignacionRepository;
    private final ProfesorRepository profesorRepository;
    private final GrupoRepository grupoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final AsignacionAcademicaMapper asignacionMapper;

    public AsignacionAcademicaService(AsignacionAcademicaRepository asignacionRepository,
                                     ProfesorRepository profesorRepository,
                                     GrupoRepository grupoRepository,
                                     AsignaturaRepository asignaturaRepository,
                                     AsignacionAcademicaMapper asignacionMapper) {
        this.asignacionRepository = asignacionRepository;
        this.profesorRepository = profesorRepository;
        this.grupoRepository = grupoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.asignacionMapper = asignacionMapper;
    }

    public AsignacionAcademicaResponse crear(AsignacionAcademicaRequest request) {
        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + request.getProfesorId()));

        if (!Boolean.TRUE.equals(profesor.getActivo())) {
            throw new ConflictoNegocioException("No se puede asignar un profesor en estado inactivo");
        }

        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (!Boolean.TRUE.equals(grupo.getActivo())) {
            throw new ConflictoNegocioException("No se pueden realizar asignaciones en un grupo inactivo");
        }

        if (grupo.getAnioLectivo() != null && grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden realizar asignaciones académicas en un año lectivo cerrado");
        }

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID " + request.getAsignaturaId()));

        if (!Boolean.TRUE.equals(asignatura.getActivo())) {
            throw new ConflictoNegocioException("No se puede asignar una asignatura inactiva");
        }

        if (asignacionRepository.existsByGrupoIdAndAsignaturaId(grupo.getId(), asignatura.getId())) {
            throw new ConflictoNegocioException("Ya existe una asignación académica para la asignatura '" +
                    asignatura.getNombre() + "' en el grupo '" + grupo.getNombre() + "'");
        }

        if (Boolean.TRUE.equals(request.getEsDirectorGrupo())) {
            validarDirectorGrupoUnico(grupo.getId(), null);
        }

        AsignacionAcademica asignacion = asignacionMapper.toEntity(request, profesor, grupo, asignatura);
        AsignacionAcademica guardada = asignacionRepository.save(asignacion);
        return asignacionMapper.toResponse(guardada);
    }

    public AsignacionAcademicaResponse actualizar(Long id, AsignacionAcademicaRequest request) {
        AsignacionAcademica asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + id));

        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + request.getProfesorId()));

        if (!Boolean.TRUE.equals(profesor.getActivo())) {
            throw new ConflictoNegocioException("No se puede asignar un profesor en estado inactivo");
        }

        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (grupo.getAnioLectivo() != null && grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden modificar asignaciones académicas en un año lectivo cerrado");
        }

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID " + request.getAsignaturaId()));

        if (asignacionRepository.existsByGrupoIdAndAsignaturaIdAndIdNot(grupo.getId(), asignatura.getId(), id)) {
            throw new ConflictoNegocioException("Ya existe otra asignación académica para la asignatura '" +
                    asignatura.getNombre() + "' en el grupo '" + grupo.getNombre() + "'");
        }

        if (Boolean.TRUE.equals(request.getEsDirectorGrupo())) {
            validarDirectorGrupoUnico(grupo.getId(), id);
        }

        asignacionMapper.updateEntityFromRequest(asignacion, request, profesor, grupo, asignatura);
        AsignacionAcademica actualizada = asignacionRepository.save(asignacion);
        return asignacionMapper.toResponse(actualizada);
    }

    @Transactional(readOnly = true)
    public AsignacionAcademicaResponse obtenerPorId(Long id) {
        AsignacionAcademica asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + id));
        return asignacionMapper.toResponse(asignacion);
    }

    @Transactional(readOnly = true)
    public List<AsignacionAcademicaResponse> listarPorProfesor(Long profesorId) {
        return asignacionRepository.findByProfesorIdAndActivoTrue(profesorId).stream()
                .map(asignacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignacionAcademicaResponse> listarPorGrupo(Long grupoId) {
        return asignacionRepository.findByGrupoIdAndActivoTrue(grupoId).stream()
                .map(asignacionMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AsignacionAcademicaResponse> listarPorAnioLectivo(Long anioLectivoId) {
        return asignacionRepository.findByAnioLectivoId(anioLectivoId).stream()
                .map(asignacionMapper::toResponse)
                .toList();
    }

    public void desactivar(Long id) {
        AsignacionAcademica asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + id));
        asignacion.setActivo(false);
        asignacionRepository.save(asignacion);
    }

    /**
     * Regla de autorización contextual (Spec S2):
     * Valida en backend que el docente autenticado esté formalmente asignado
     * a la asignatura y grupo sobre los cuales desea operar.
     */
    @Transactional(readOnly = true)
    public boolean tieneAutorizacionDocente(Long usuarioId, Long grupoId, Long asignaturaId) {
        if (usuarioId == null || grupoId == null || asignaturaId == null) {
            return false;
        }
        return asignacionRepository.existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue(
                usuarioId, grupoId, asignaturaId
        );
    }

    private void validarDirectorGrupoUnico(Long grupoId, Long asignacionIdExcluida) {
        Optional<AsignacionAcademica> directorOpt = asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(grupoId);
        if (directorOpt.isPresent()) {
            AsignacionAcademica director = directorOpt.get();
            if (asignacionIdExcluida == null || !director.getId().equals(asignacionIdExcluida)) {
                throw new ConflictoNegocioException("El grupo ya cuenta con un director de grupo asignado (" +
                        director.getProfesor().getNombres() + " " + director.getProfesor().getApellidos() + ")");
            }
        }
    }
}
