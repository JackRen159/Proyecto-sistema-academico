package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AsignacionAcademicaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AsignacionAcademicaService {

    private final AsignacionAcademicaRepository asignacionRepository;
    private final ProfesorRepository profesorRepository;
    private final GrupoRepository grupoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final MatriculaRepository matriculaRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;
    private final AsignacionAcademicaMapper asignacionMapper;

    @Autowired
    public AsignacionAcademicaService(AsignacionAcademicaRepository asignacionRepository,
                                     ProfesorRepository profesorRepository,
                                     GrupoRepository grupoRepository,
                                     AsignaturaRepository asignaturaRepository,
                                     GradoAsignaturaRepository gradoAsignaturaRepository,
                                     MatriculaRepository matriculaRepository,
                                     EstudianteAcudienteRepository estudianteAcudienteRepository,
                                     UsuarioRepository usuarioRepository,
                                     AuditoriaGlobalService auditoriaGlobalService,
                                     AsignacionAcademicaMapper asignacionMapper) {
        this.asignacionRepository = asignacionRepository;
        this.profesorRepository = profesorRepository;
        this.grupoRepository = grupoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.matriculaRepository = matriculaRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
        this.asignacionMapper = asignacionMapper;
    }

    public AsignacionAcademicaService(AsignacionAcademicaRepository asignacionRepository,
                                     ProfesorRepository profesorRepository,
                                     GrupoRepository grupoRepository,
                                     AsignaturaRepository asignaturaRepository,
                                     AsignacionAcademicaMapper asignacionMapper) {
        this(asignacionRepository, profesorRepository, grupoRepository, asignaturaRepository,
                null, null, null, null, null, asignacionMapper);
    }

    public AsignacionAcademicaResponse crear(AsignacionAcademicaRequest request) {
        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + request.getProfesorId()));

        if (!Boolean.TRUE.equals(profesor.getActivo())) {
            throw new ConflictoNegocioException("No se puede asignar un profesor en estado inactivo");
        }

        // Validación de situación administrativa e inhabilidades (Ley 1918 de 2018, RN-PRO-02, UC-15 E1)
        if (profesor.getEstadoAdministrativo() != null && profesor.getEstadoAdministrativo() != EstadoAdministrativoDocente.ACTIVO) {
            throw new ConflictoNegocioException("No se puede asignar carga académica a un docente en situación de "
                    + profesor.getEstadoAdministrativo() + " (RN-PRO-02, UC-15 E1).");
        }

        if (Boolean.FALSE.equals(profesor.getVerificadoInhabilidades())) {
            throw new ConflictoNegocioException("El docente no cuenta con verificación vigente de inhabilidades Ley 1918 de 2018 (RN-PRO-02).");
        }

        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (!Boolean.TRUE.equals(grupo.getActivo())) {
            throw new ConflictoNegocioException("No se pueden realizar asignaciones en un grupo inactivo");
        }

        if (grupo.getAnioLectivo() != null && (grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO || grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO)) {
            throw new ConflictoNegocioException("No se pueden realizar asignaciones académicas en un año lectivo cerrado");
        }

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID " + request.getAsignaturaId()));

        if (!Boolean.TRUE.equals(asignatura.getActivo())) {
            throw new ConflictoNegocioException("No se puede asignar una asignatura inactiva");
        }

        // Validación contra el Plan de Estudios del grado del grupo (RN-PRO-04, UC-15 E3)
        if (gradoAsignaturaRepository != null) {
            boolean perteneceAlPlan = gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(grupo.getGrado().getId(), asignatura.getId());
            if (!perteneceAlPlan) {
                throw new ConflictoNegocioException("La asignatura '" + asignatura.getNombre()
                        + "' no pertenece al Plan de Estudios del grado " + grupo.getGrado().getNombre() + " (RN-PRO-04, UC-15 E3).");
            }
        }

        if (asignacionRepository.existsByGrupoIdAndAsignaturaId(grupo.getId(), asignatura.getId())) {
            throw new ConflictoNegocioException("Ya existe una asignación académica para la asignatura '" +
                    asignatura.getNombre() + "' en el grupo '" + grupo.getNombre() + "'");
        }

        // Validación de topes de carga horaria semanal (RN-PRO-03, UC-15 E2, A4)
        validarTopesDeCargaHoraria(profesor, request.getIntensidadHorariaSemanal(), request.getActaRectoralHorasExtras());

        // Verificación de alerta de conflicto de interés familiar en el grupo (RN-PRO-08, UC-15 paso 3)
        verificarAlertaConflictoInteres(profesor, grupo);

        if (Boolean.TRUE.equals(request.getEsDirectorGrupo())) {
            validarDirectorGrupoUnico(grupo.getId(), null);
        }

        AsignacionAcademica asignacion = asignacionMapper.toEntity(request, profesor, grupo, asignatura);
        AsignacionAcademica guardada = asignacionRepository.save(asignacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_ASIGNACION_ACADEMICA",
                    "AsignacionAcademica",
                    guardada.getId() != null ? guardada.getId().toString() : "0",
                    null,
                    "Docente: " + profesor.getNombres() + " " + profesor.getApellidos() + " -> " + asignatura.getNombre() + " (" + grupo.getNombre() + ")",
                    "Asignación de carga académica",
                    "EXITOSO"
            );
        }

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

        if (grupo.getAnioLectivo() != null && (grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO || grupo.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO)) {
            throw new ConflictoNegocioException("No se pueden modificar asignaciones académicas en un año lectivo cerrado");
        }

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignatura no encontrada con ID " + request.getAsignaturaId()));

        if (gradoAsignaturaRepository != null) {
            boolean perteneceAlPlan = gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(grupo.getGrado().getId(), asignatura.getId());
            if (!perteneceAlPlan) {
                throw new ConflictoNegocioException("La asignatura '" + asignatura.getNombre()
                        + "' no pertenece al Plan de Estudios del grado " + grupo.getGrado().getNombre() + " (RN-PRO-04, UC-15 E3).");
            }
        }

        if (asignacionRepository.existsByGrupoIdAndAsignaturaIdAndIdNot(grupo.getId(), asignatura.getId(), id)) {
            throw new ConflictoNegocioException("Ya existe otra asignación académica para la asignatura '" +
                    asignatura.getNombre() + "' en el grupo '" + grupo.getNombre() + "'");
        }

        if (Boolean.TRUE.equals(request.getEsDirectorGrupo())) {
            validarDirectorGrupoUnico(grupo.getId(), id);
        }

        asignacionMapper.updateEntityFromRequest(asignacion, request, profesor, grupo, asignatura);
        AsignacionAcademica actualizada = asignacionRepository.save(asignacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_ASIGNACION_ACADEMICA",
                    "AsignacionAcademica",
                    actualizada.getId().toString(),
                    null,
                    "Actualización carga: " + profesor.getNombres() + " -> " + asignatura.getNombre(),
                    "Actualización de parámetros de asignación",
                    "EXITOSO"
            );
        }

        return asignacionMapper.toResponse(actualizada);
    }

    public AsignacionAcademicaResponse relevarDocente(Long asignacionId, RelevoDocenteRequest request, Long usuarioId) {
        AsignacionAcademica asignacionOriginal = asignacionRepository.findById(asignacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + asignacionId));

        Profesor docenteReemplazo = profesorRepository.findById(request.getDocenteReemplazoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Docente de reemplazo no encontrado con ID " + request.getDocenteReemplazoId()));

        if (!Boolean.TRUE.equals(docenteReemplazo.getActivo())) {
            throw new ConflictoNegocioException("El docente de reemplazo debe encontrarse en estado activo.");
        }

        if (docenteReemplazo.getId().equals(asignacionOriginal.getProfesor().getId())) {
            throw new ConflictoNegocioException("El docente de reemplazo no puede ser el mismo docente titular actual.");
        }

        Profesor titularOriginal = asignacionOriginal.getProfesor();
        LocalDate inicio = request.getFechaInicioVigencia() != null ? request.getFechaInicioVigencia() : LocalDate.now();

        if (request.getTipoRelevo() == EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL) {
            asignacionOriginal.setEstadoAsignacion(EstadoAsignacionAcademica.SUSPENDIDA_POR_LICENCIA);
        } else {
            asignacionOriginal.setEstadoAsignacion(EstadoAsignacionAcademica.FINALIZADA_POR_RELEVO);
            asignacionOriginal.setActivo(false);
        }
        asignacionOriginal.setFechaFinVigencia(inicio);
        asignacionRepository.save(asignacionOriginal);

        // Crear la asignación del docente suplente / nuevo titular
        AsignacionAcademica nuevaAsignacion = new AsignacionAcademica();
        nuevaAsignacion.setProfesor(docenteReemplazo);
        nuevaAsignacion.setGrupo(asignacionOriginal.getGrupo());
        nuevaAsignacion.setAsignatura(asignacionOriginal.getAsignatura());
        nuevaAsignacion.setIntensidadHorariaSemanal(asignacionOriginal.getIntensidadHorariaSemanal());
        nuevaAsignacion.setEsDirectorGrupo(asignacionOriginal.getEsDirectorGrupo());
        nuevaAsignacion.setEstadoAsignacion(request.getTipoRelevo());
        nuevaAsignacion.setFechaInicioVigencia(inicio);
        nuevaAsignacion.setFechaFinVigencia(request.getFechaFinVigencia());
        nuevaAsignacion.setEsReemplazoTemporal(request.getTipoRelevo() == EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL);
        nuevaAsignacion.setDocenteTitularRelevado(titularOriginal);
        nuevaAsignacion.setMotivoRelevo(request.getMotivo());
        nuevaAsignacion.setActivo(true);

        AsignacionAcademica guardada = asignacionRepository.save(nuevaAsignacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "RELEVO_DOCENTE",
                    "AsignacionAcademica",
                    guardada.getId().toString(),
                    "Titular saliente: " + titularOriginal.getNombres() + " " + titularOriginal.getApellidos(),
                    "Docente suplente: " + docenteReemplazo.getNombres() + " " + docenteReemplazo.getApellidos() + " (Tipo: " + request.getTipoRelevo() + ")",
                    request.getMotivo(),
                    "EXITOSO"
            );
        }

        return asignacionMapper.toResponse(guardada);
    }

    public List<AsignacionAcademicaResponse> asignarBloqueCurricular(BloqueCurricularAsignacionRequest request) {
        Profesor profesor = profesorRepository.findById(request.getProfesorId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Profesor no encontrado con ID " + request.getProfesorId()));

        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (gradoAsignaturaRepository == null) {
            throw new ConflictoNegocioException("Repositorio de Plan de Estudios no disponible");
        }

        List<GradoAsignatura> materiasDelGrado = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(grupo.getGrado().getId());
        List<AsignacionAcademicaResponse> asignadas = new ArrayList<>();
        boolean directorAsignado = false;

        for (GradoAsignatura ga : materiasDelGrado) {
            if (request.getAsignaturasExcluidasIds() != null && request.getAsignaturasExcluidasIds().contains(ga.getAsignatura().getId())) {
                continue;
            }

            if (asignacionRepository.existsByGrupoIdAndAsignaturaId(grupo.getId(), ga.getAsignatura().getId())) {
                continue; // Ya asignada
            }

            AsignacionAcademica asig = new AsignacionAcademica();
            asig.setProfesor(profesor);
            asig.setGrupo(grupo);
            asig.setAsignatura(ga.getAsignatura());
            asig.setIntensidadHorariaSemanal(ga.getIntensidadHoraria());
            if (Boolean.TRUE.equals(request.getEsDirectorGrupo()) && !directorAsignado) {
                asig.setEsDirectorGrupo(true);
                directorAsignado = true;
            } else {
                asig.setEsDirectorGrupo(false);
            }
            asig.setActivo(true);
            asig.setEstadoAsignacion(EstadoAsignacionAcademica.TITULAR);
            asig.setFechaInicioVigencia(LocalDate.now());

            AsignacionAcademica guardada = asignacionRepository.save(asig);
            asignadas.add(asignacionMapper.toResponse(guardada));
        }

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ASIGNACION_BLOQUE_CURRICULAR",
                    "AsignacionAcademica",
                    grupo.getId().toString(),
                    null,
                    "Asignadas " + asignadas.size() + " materias en bloque a " + profesor.getNombres() + " " + profesor.getApellidos(),
                    "Asignación masiva modelo unidocente",
                    "EXITOSO"
            );
        }

        return asignadas;
    }

    @Transactional(readOnly = true)
    public List<MateriaVacanteResponse> obtenerMatrizVacantes(Long anioLectivoId, Long grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + grupoId));

        if (gradoAsignaturaRepository == null) {
            return Collections.emptyList();
        }

        List<GradoAsignatura> materiasGrado = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(grupo.getGrado().getId());
        List<AsignacionAcademica> asignacionesGrupo = asignacionRepository.findByGrupoIdAndActivoTrue(grupoId);

        List<MateriaVacanteResponse> resultado = new ArrayList<>();
        for (GradoAsignatura ga : materiasGrado) {
            MateriaVacanteResponse res = new MateriaVacanteResponse();
            res.setGrupoId(grupo.getId());
            res.setGrupoNombre(grupo.getNombre());
            res.setGradoId(grupo.getGrado().getId());
            res.setGradoNombre(grupo.getGrado().getNombre());
            res.setAsignaturaId(ga.getAsignatura().getId());
            res.setAsignaturaNombre(ga.getAsignatura().getNombre());
            res.setAsignaturaCodigo(ga.getAsignatura().getCodigo());
            res.setIntensidadHoraria(ga.getIntensidadHoraria());

            Optional<AsignacionAcademica> asigOpt = asignacionesGrupo.stream()
                    .filter(a -> a.getAsignatura().getId().equals(ga.getAsignatura().getId()))
                    .findFirst();

            if (asigOpt.isPresent()) {
                AsignacionAcademica a = asigOpt.get();
                res.setTieneDocenteAsignado(true);
                res.setProfesorId(a.getProfesor().getId());
                res.setProfesorNombre(a.getProfesor().getNombres() + " " + a.getProfesor().getApellidos());
                res.setEstadoAsignacion(a.getEstadoAsignacion().name());
            } else {
                res.setTieneDocenteAsignado(false);
                res.setProfesorId(null);
                res.setProfesorNombre("VACANTE / PENDIENTE");
                res.setEstadoAsignacion("VACANTE");
            }
            resultado.add(res);
        }
        return resultado;
    }

    @Transactional(readOnly = true)
    public List<AsignacionAcademicaResponse> obtenerEquipoDocenteGrupo(Long grupoId) {
        return asignacionRepository.findByGrupoIdAndActivoTrue(grupoId).stream()
                .map(a -> {
                    AsignacionAcademicaResponse r = asignacionMapper.toResponse(a);
                    r.setProfesorDocumento(null); // Minimización de datos privados (RN-09)
                    return r;
                })
                .toList();
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
        asignacion.setEstadoAsignacion(EstadoAsignacionAcademica.FINALIZADA_POR_RELEVO);
        asignacionRepository.save(asignacion);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "DESACTIVACION_ASIGNACION_ACADEMICA",
                    "AsignacionAcademica",
                    id.toString(),
                    "Activo: true",
                    "Activo: false",
                    "Desactivación de asignación de carga académica",
                    "EXITOSO"
            );
        }
    }

    @Transactional(readOnly = true)
    public boolean tieneAutorizacionDocente(Long usuarioId, Long grupoId, Long asignaturaId) {
        if (usuarioId == null || grupoId == null || asignaturaId == null) {
            return false;
        }
        return asignacionRepository.existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue(
                usuarioId, grupoId, asignaturaId
        );
    }

    private void validarTopesDeCargaHoraria(Profesor profesor, int horasNuevas, String actaHorasExtras) {
        Integer horasActuales = asignacionRepository.sumHorasAsignadasPorProfesor(profesor.getId());
        int acumulado = (horasActuales != null ? horasActuales : 0) + horasNuevas;
        int topeOrdinario = profesor.getHorasSemanalesOrdinarias();
        int topeMaximo = topeOrdinario + profesor.getHorasExtrasPermitidas();

        if (acumulado > topeMaximo && (actaHorasExtras == null || actaHorasExtras.isBlank())) {
            throw new ConflictoNegocioException("La asignación supera el tope máximo de carga horaria semanal del docente ("
                    + topeMaximo + " horas). Total acumulado resultante: " + acumulado
                    + " horas. Se requiere autorización rectoral de horas extras (RN-PRO-03, UC-15 E2, A4).");
        }
    }

    private void verificarAlertaConflictoInteres(Profesor profesor, Grupo grupo) {
        if (profesor.getDocumento() == null || matriculaRepository == null || estudianteAcudienteRepository == null) {
            return;
        }
        List<Matricula> matriculas = matriculaRepository.findByGrupoIdAndEstado(grupo.getId(), EstadoMatricula.MATRICULADA);
        for (Matricula m : matriculas) {
            List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository.findByEstudiante(m.getEstudiante());
            for (EstudianteAcudiente v : vinculos) {
                if (v.getAcudiente() != null && profesor.getDocumento().equals(v.getAcudiente().getDocumento())) {
                    if (auditoriaGlobalService != null) {
                        auditoriaGlobalService.registrarEvento(
                                "ALERTA_CONFLICTO_INTERES_DOCENTE",
                                "AsignacionAcademica",
                                grupo.getId().toString(),
                                null,
                                "Docente " + profesor.getNombres() + " " + profesor.getApellidos() + " es acudiente en grupo " + grupo.getNombre(),
                                "Alerta preventiva de conflicto de interés familiar (RN-PRO-08)",
                                "EXITOSO"
                        );
                    }
                    return;
                }
            }
        }
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
