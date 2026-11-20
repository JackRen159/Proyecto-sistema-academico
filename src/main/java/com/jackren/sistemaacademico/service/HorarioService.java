package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.HorarioMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HorarioService {

    private final AulaRepository aulaRepository;
    private final PlantillaFranjaHorariaRepository franjaRepository;
    private final HorarioGrupoVersionRepository versionRepository;
    private final HorarioBloqueRepository bloqueRepository;
    private final NovedadHorarioRepository novedadRepository;
    private final GrupoRepository grupoRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final MatriculaRepository matriculaRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final ProfesorRepository profesorRepository;
    private final UsuarioRepository usuarioRepository;
    private final DiaNoLectivoRepository diaNoLectivoRepository;
    private final ValidadorColisionesHorarioService validadorColisionesService;
    private final HorarioMapper horarioMapper;

    public HorarioService(AulaRepository aulaRepository,
                          PlantillaFranjaHorariaRepository franjaRepository,
                          HorarioGrupoVersionRepository versionRepository,
                          HorarioBloqueRepository bloqueRepository,
                          NovedadHorarioRepository novedadRepository,
                          GrupoRepository grupoRepository,
                          AsignacionAcademicaRepository asignacionRepository,
                          AnioLectivoRepository anioLectivoRepository,
                          MatriculaRepository matriculaRepository,
                          EstudianteRepository estudianteRepository,
                          EstudianteAcudienteRepository estudianteAcudienteRepository,
                          ProfesorRepository profesorRepository,
                          UsuarioRepository usuarioRepository,
                          DiaNoLectivoRepository diaNoLectivoRepository,
                          ValidadorColisionesHorarioService validadorColisionesService,
                          HorarioMapper horarioMapper) {
        this.aulaRepository = aulaRepository;
        this.franjaRepository = franjaRepository;
        this.versionRepository = versionRepository;
        this.bloqueRepository = bloqueRepository;
        this.novedadRepository = novedadRepository;
        this.grupoRepository = grupoRepository;
        this.asignacionRepository = asignacionRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.matriculaRepository = matriculaRepository;
        this.estudianteRepository = estudianteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.profesorRepository = profesorRepository;
        this.usuarioRepository = usuarioRepository;
        this.diaNoLectivoRepository = diaNoLectivoRepository;
        this.validadorColisionesService = validadorColisionesService;
        this.horarioMapper = horarioMapper;
    }

    // =========================================================================
    // 1. CATÁLOGO DE AULAS Y ESPACIOS FÍSICOS (UC-PROV-20)
    // =========================================================================

    public AulaResponse crearAula(AulaRequest request) {
        if (aulaRepository.existsBySedeAndCodigo(request.getSede(), request.getCodigo())) {
            throw new ConflictoNegocioException("Ya existe un aula con el código " + request.getCodigo() +
                    " en la sede " + request.getSede());
        }
        Aula aula = horarioMapper.toEntity(request);
        Aula guardada = aulaRepository.save(aula);
        return horarioMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<AulaResponse> listarAulasPorSede(String sede) {
        List<Aula> lista = (sede != null && !sede.trim().isEmpty())
                ? aulaRepository.findBySedeAndEstado(sede, EstadoAula.DISPONIBLE)
                : aulaRepository.findAll();
        return lista.stream().map(horarioMapper::toResponse).toList();
    }

    public AulaResponse actualizarEstadoAula(Long aulaId, EstadoAula nuevoEstado) {
        Aula aula = aulaRepository.findById(aulaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aula no encontrada con ID " + aulaId));
        aula.setEstado(nuevoEstado);
        Aula actualizada = aulaRepository.save(aula);
        return horarioMapper.toResponse(actualizada);
    }

    // =========================================================================
    // 2. PLANTILLAS DE FRANJAS HORARIAS (UC-PROV-20, DEC. 1850/2002)
    // =========================================================================

    public PlantillaFranjaHorariaResponse crearFranja(PlantillaFranjaHorariaRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Año lectivo no encontrado con ID " + request.getAnioLectivoId()));

        if (!request.getHoraInicio().isBefore(request.getHoraFin())) {
            throw new ValidacionException("La hora de inicio debe ser estrictamente anterior a la hora de fin de la franja");
        }

        PlantillaFranjaHoraria franja = horarioMapper.toEntity(request, anio);
        PlantillaFranjaHoraria guardada = franjaRepository.save(franja);
        return horarioMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public List<PlantillaFranjaHorariaResponse> listarFranjasPorJornada(Long anioLectivoId, Jornada jornada) {
        return franjaRepository.findByAnioLectivoIdAndJornadaOrderByNumeroBloqueAsc(anioLectivoId, jornada).stream()
                .map(horarioMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // 3. ESTRUCTURACIÓN Y PUBLICACIÓN DE HORARIOS (UC-PROV-20)
    // =========================================================================

    public HorarioGrupoVersionResponse crearHorarioBorrador(Long grupoId, Long usuarioId) {
        Grupo grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + grupoId));

        int siguienteVersion = (int) versionRepository.countByGrupoId(grupoId) + 1;
        HorarioGrupoVersion version = new HorarioGrupoVersion(grupo, siguienteVersion);
        version.setEstado(EstadoHorarioVersion.BORRADOR);

        HorarioGrupoVersion guardada = versionRepository.save(version);
        return horarioMapper.toResponse(guardada);
    }

    public HorarioBloqueResponse agregarBloqueHorario(Long horarioVersionId, HorarioBloqueRequest request) {
        HorarioGrupoVersion version = versionRepository.findById(horarioVersionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión de horario no encontrada con ID " + horarioVersionId));

        if (version.getEstado() == EstadoHorarioVersion.HISTORICO) {
            throw new ConflictoNegocioException("No se pueden agregar bloques a un horario en estado HISTORICO (inmutabilidad)");
        }

        // Validador de Triple No-Colisión y Capacidad (RN-HOR-01, RN-HOR-05)
        validadorColisionesService.validarBloque(request, horarioVersionId, null);

        AsignacionAcademica asig = asignacionRepository.findById(request.getAsignacionAcademicaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada"));
        Aula aula = aulaRepository.findById(request.getAulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Aula no encontrada"));
        PlantillaFranjaHoraria franja = franjaRepository.findById(request.getFranjaHorariaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Franja horaria no encontrada"));

        HorarioBloque bloque = new HorarioBloque(version, asig, aula, request.getDiaSemana(), franja);
        HorarioBloque guardado = bloqueRepository.save(bloque);
        version.addBloque(guardado);

        return horarioMapper.toResponse(guardado);
    }

    public void eliminarBloqueHorario(Long bloqueId) {
        HorarioBloque bloque = bloqueRepository.findById(bloqueId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Bloque de horario no encontrado con ID " + bloqueId));

        if (bloque.getHorarioVersion().getEstado() == EstadoHorarioVersion.HISTORICO) {
            throw new ConflictoNegocioException("No se pueden eliminar bloques de una versión histórica de horario");
        }

        bloqueRepository.delete(bloque);
    }

    @Transactional(readOnly = true)
    public ValidacionHorarioResponse validarHorario(Long horarioVersionId) {
        HorarioGrupoVersion version = versionRepository.findById(horarioVersionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión de horario no encontrada con ID " + horarioVersionId));
        return validadorColisionesService.validarHorarioCompleto(version);
    }

    public HorarioGrupoVersionResponse publicarHorario(Long horarioVersionId, LocalDate fechaInicioVigencia, Long usuarioId) {
        HorarioGrupoVersion version = versionRepository.findById(horarioVersionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Versión de horario no encontrada con ID " + horarioVersionId));

        // UC-PROV-20 E2: Validación de cumplimiento exacto de IHS antes de publicar (RN-HOR-02)
        ValidacionHorarioResponse validacion = validadorColisionesService.validarHorarioCompleto(version);
        if (!Boolean.TRUE.equals(validacion.getValido())) {
            throw new ConflictoNegocioException("No se puede publicar el horario: existen horas semanales faltantes o sobrantes frente al Plan de Estudios (RN-HOR-02, UC-PROV-20 E2): " +
                    String.join("; ", validacion.getErroresColision()));
        }

        LocalDate inicio = fechaInicioVigencia != null ? fechaInicioVigencia : LocalDate.now();

        // Archivar la versión vigente anterior del grupo como HISTORICA
        versionRepository.findFirstByGrupoIdAndEstadoOrderByNumeroVersionDesc(version.getGrupo().getId(), EstadoHorarioVersion.PUBLICADO_VIGENTE)
                .ifPresent(anterior -> {
                    if (!anterior.getId().equals(version.getId())) {
                        anterior.setEstado(EstadoHorarioVersion.HISTORICO);
                        anterior.setFechaFinVigencia(inicio.minusDays(1));
                        versionRepository.save(anterior);
                    }
                });

        Usuario publicador = usuarioId != null ? usuarioRepository.findById(usuarioId).orElse(null) : null;
        version.setEstado(EstadoHorarioVersion.PUBLICADO_VIGENTE);
        version.setFechaInicioVigencia(inicio);
        version.setUsuarioPublicador(publicador);
        version.setFechaPublicacion(LocalDateTime.now());

        HorarioGrupoVersion guardada = versionRepository.save(version);
        return horarioMapper.toResponse(guardada);
    }

    // =========================================================================
    // 4. CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR (UC-04, UC-10, UC-PROV-22)
    // =========================================================================

    @Transactional(readOnly = true)
    public ConsultaHorarioEstudianteResponse consultarHorarioEstudiante(Long usuarioId, LocalDate fecha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ESTUDIANTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Estudiante activo (UC-04 E1)");
        }

        Estudiante est = estudianteRepository.findById(usuario.getPersonaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Expediente de estudiante no encontrado"));

        LocalDate fechaFiltro = fecha != null ? fecha : LocalDate.now();
        return construirHorarioEstudiante(est, fechaFiltro);
    }

    @Transactional(readOnly = true)
    public ConsultaHorarioEstudianteResponse consultarHorarioAcudiente(Long usuarioId, Long estudianteId, LocalDate fecha) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con ID " + usuarioId));

        if (usuario.getPersonaTipo() != TipoPersonaUsuario.ACUDIENTE || usuario.getPersonaId() == null) {
            throw new AccessDeniedException("El usuario autenticado no posee perfil de Acudiente activo (UC-10 E1)");
        }

        Estudiante est = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID " + estudianteId));

        // UC-10 E1 / RN-HOR-07: Validar vínculo y medidas de protección legal
        List<EstudianteAcudiente> vinculos = estudianteAcudienteRepository.findByEstudiante(est);
        boolean vinculoValido = vinculos.stream().anyMatch(v ->
                v.getAcudiente().getId().equals(usuario.getPersonaId()) &&
                        v.getEstadoVinculo() == EstadoVinculo.VERIFICADO
        );

        if (!vinculoValido) {
            throw new AccessDeniedException("Acceso denegado: el acudiente no posee un vínculo verificado y activo con el menor o registra medida de protección (UC-10 E1, RN-HOR-07)");
        }

        LocalDate fechaFiltro = fecha != null ? fecha : LocalDate.now();
        return construirHorarioEstudiante(est, fechaFiltro);
    }

    private ConsultaHorarioEstudianteResponse construirHorarioEstudiante(Estudiante est, LocalDate fecha) {
        Matricula mat = matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(est.getId()).stream()
                .filter(m -> m.getEstado() == EstadoMatricula.MATRICULADA)
                .findFirst()
                .orElseThrow(() -> new ConflictoNegocioException("El estudiante no registra matrícula activa (UC-04 E3)"));

        Grupo grupo = mat.getGrupo();

        // Buscar versión publicada
        HorarioGrupoVersion version = versionRepository
                .findFirstByGrupoIdAndEstadoOrderByNumeroVersionDesc(grupo.getId(), EstadoHorarioVersion.PUBLICADO_VIGENTE)
                .orElseThrow(() -> new ConflictoNegocioException("El horario del grupo aún se encuentra en borrador o pendiente de publicación oficial (UC-04 A2, RN-HOR-06)"));

        ConsultaHorarioEstudianteResponse resp = new ConsultaHorarioEstudianteResponse();
        resp.setMatriculaId(mat.getId());
        resp.setNumeroMatricula(mat.getNumeroMatricula());
        resp.setEstudianteId(est.getId());
        resp.setEstudianteNombreCompleto(est.getNombres() + " " + est.getApellidos());
        resp.setGrupoId(grupo.getId());
        resp.setGrupoNombre(grupo.getNombre());
        resp.setSede(grupo.getSede());
        resp.setJornada(grupo.getJornada() != null ? grupo.getJornada() : Jornada.MANANA);
        resp.setNumeroVersion(version.getNumeroVersion());
        resp.setEstado(version.getEstado());
        resp.setFechaInicioVigencia(version.getFechaInicioVigencia());

        for (HorarioBloque b : version.getBloques()) {
            resp.getBloques().add(horarioMapper.toResponse(b));
        }

        // Consultar días no lectivos de la semana para advertencias familiares (UC-14 A4)
        if (grupo.getAnioLectivo() != null) {
            diaNoLectivoRepository.findByAnioLectivoIdAndActivoTrueOrderByFechaInicioAsc(grupo.getAnioLectivo().getId())
                    .forEach(d -> resp.getDiasNoLectivosSemana().add(
                            new ConsultaHorarioEstudianteResponse.DiaNoLectivoResponse(d.getFechaInicio(), d.getMotivo())
                    ));
        }

        return resp;
    }

    // UC-PROV-22: Agenda individual docente
    @Transactional(readOnly = true)
    public AgendaDocenteResponse consultarAgendaDocente(Long usuarioId) {
        Profesor prof = profesorRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new AccessDeniedException("El usuario autenticado no posee perfil de Docente (UC-PROV-22)"));

        List<HorarioBloque> agenda = bloqueRepository.findAgendaDocenteVigente(prof.getId());

        AgendaDocenteResponse resp = new AgendaDocenteResponse();
        resp.setProfesorId(prof.getId());
        resp.setProfesorNombreCompleto(prof.getNombres() + " " + prof.getApellidos());
        resp.setDocumento(prof.getDocumento());
        resp.setTotalBloquesSemanales((long) agenda.size());

        for (HorarioBloque b : agenda) {
            resp.getBloques().add(horarioMapper.toResponse(b));
        }

        return resp;
    }

    // UC-PROV-22 A2: Mapa de disponibilidad de aulas
    @Transactional(readOnly = true)
    public List<DisponibilidadAulaResponse> consultarDisponibilidadAulas(String sede, DiaSemana dia, Long franjaId) {
        List<Aula> aulas = (sede != null && !sede.trim().isEmpty())
                ? aulaRepository.findBySedeAndEstado(sede, EstadoAula.DISPONIBLE)
                : aulaRepository.findByEstado(EstadoAula.DISPONIBLE);

        List<DisponibilidadAulaResponse> resps = new ArrayList<>();

        for (Aula a : aulas) {
            DisponibilidadAulaResponse item = new DisponibilidadAulaResponse();
            item.setAulaId(a.getId());
            item.setSede(a.getSede());
            item.setCodigo(a.getCodigo());
            item.setNombre(a.getNombre());
            item.setTipoEspacio(a.getTipoEspacio());
            item.setCapacidadMaxima(a.getCapacidadMaxima());

            List<HorarioBloque> colisiones = bloqueRepository.findColisionesAula(
                    a.getId(), dia, franjaId, EstadoHorarioVersion.PUBLICADO_VIGENTE, -1L
            );

            if (colisiones.isEmpty()) {
                item.setDisponible(true);
            } else {
                item.setDisponible(false);
                HorarioBloque c = colisiones.get(0);
                item.setGrupoOcupante(c.getAsignacionAcademica().getGrupo().getNombre());
                item.setAsignaturaOcupante(c.getAsignacionAcademica().getAsignatura().getNombre());
                if (c.getAsignacionAcademica().getProfesor() != null) {
                    item.setDocenteOcupante(c.getAsignacionAcademica().getProfesor().getNombres() + " " +
                            c.getAsignacionAcademica().getProfesor().getApellidos());
                }
            }

            resps.add(item);
        }

        return resps;
    }

    // UC-PROV-22 A1: Ubicación en tiempo real
    @Transactional(readOnly = true)
    public UbicacionActualResponse consultarUbicacionActual(String tipoConsulta, Long idConsulta) {
        UbicacionActualResponse resp = new UbicacionActualResponse();
        resp.setTipoConsulta(tipoConsulta);
        resp.setId(idConsulta);

        // Ubicación actual simulada frente a la primera sesión de clase vigente
        if ("GRUPO".equalsIgnoreCase(tipoConsulta)) {
            Grupo g = grupoRepository.findById(idConsulta)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado"));
            resp.setGrupoNombre(g.getNombre());
            resp.setSede(g.getSede());
            resp.setAulaCodigo(g.getAulaBase() != null ? g.getAulaBase() : "101");
            resp.setClaseEnCurso(true);
        } else if ("DOCENTE".equalsIgnoreCase(tipoConsulta)) {
            Profesor p = profesorRepository.findById(idConsulta)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Docente no encontrado"));
            resp.setProfesorNombre(p.getNombres() + " " + p.getApellidos());
            resp.setClaseEnCurso(true);
        } else if ("ESTUDIANTE".equalsIgnoreCase(tipoConsulta)) {
            Estudiante e = estudianteRepository.findById(idConsulta)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado"));
            resp.setNombre(e.getNombres() + " " + e.getApellidos());
            resp.setClaseEnCurso(true);
        }

        return resp;
    }
}
