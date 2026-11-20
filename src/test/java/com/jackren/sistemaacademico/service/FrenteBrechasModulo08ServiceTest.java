package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 08: Asistencia Escolar V2 al 100%")
class FrenteBrechasModulo08ServiceTest {

    @Mock
    private ConfiguracionAlertaAsistenciaRepository alertaRepository;

    @Mock
    private SesionAsistenciaRepository sesionRepository;

    @Mock
    private RegistroAsistenciaRepository registroRepository;

    @Mock
    private AuditoriaAsistenciaRepository auditoriaRepository;

    @Mock
    private JustificacionAsistenciaRepository justificacionRepository;

    @Mock
    private SolicitudExcusaAsistenciaRepository solicitudExcusaRepository;

    @Mock
    private AutorizacionSalidaAnticipadaRepository salidaRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private PeriodoAcademicoRepository periodoRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private DiaNoLectivoRepository diaNoLectivoRepository;

    @Mock
    private ConfiguracionSieeVersionRepository sieeVersionRepository;

    @Mock
    private CalificacionActividadRepository calificacionActividadRepository;

    @Mock
    private ActuacionSeguimientoAusentismoRepository actuacionRepository;

    private AsistenciaMapper asistenciaMapper;
    private AsistenciaService asistenciaService;
    private ExcusaAsistenciaService excusaService;
    private SalidaAnticipadaService salidaService;
    private SeguimientoAusentismoService seguimientoService;

    private AnioLectivo anio;
    private PeriodoAcademico periodo;
    private Grado grado;
    private Grupo grupo;
    private Profesor profesor;
    private Asignatura asignatura;
    private AsignacionAcademica asignacion;
    private Estudiante estudiante;
    private Matricula matricula;
    private ConfiguracionAlertaAsistencia alertaConfig;

    @BeforeEach
    void setUp() {
        asistenciaMapper = new AsistenciaMapper();

        asistenciaService = new AsistenciaService(
                alertaRepository, sesionRepository, registroRepository, auditoriaRepository,
                justificacionRepository, solicitudExcusaRepository, salidaRepository,
                asignacionRepository, grupoRepository, periodoRepository, anioLectivoRepository,
                matriculaRepository, usuarioRepository, profesorRepository, estudianteRepository,
                estudianteAcudienteRepository, diaNoLectivoRepository, sieeVersionRepository, asistenciaMapper
        );

        excusaService = new ExcusaAsistenciaService(
                solicitudExcusaRepository, registroRepository, calificacionActividadRepository,
                matriculaRepository, usuarioRepository, asistenciaMapper
        );

        salidaService = new SalidaAnticipadaService(
                salidaRepository, registroRepository, matriculaRepository, estudianteAcudienteRepository,
                acudienteRepository, usuarioRepository, asistenciaMapper
        );

        seguimientoService = new SeguimientoAusentismoService(
                actuacionRepository, matriculaRepository, periodoRepository, usuarioRepository, asistenciaMapper
        );

        anio = new AnioLectivo();
        anio.setId(1L);
        anio.setAnio(2026);
        anio.setEstado(EstadoAnioLectivo.ACTIVO);

        periodo = new PeriodoAcademico();
        periodo.setId(10L);
        periodo.setNombre("Primer Periodo");
        periodo.setNumero(1);
        periodo.setAnioLectivo(anio);
        periodo.setEstado(EstadoPeriodo.ABIERTO);
        periodo.setFechaInicio(LocalDate.of(2026, 2, 1));
        periodo.setFechaFin(LocalDate.of(2026, 4, 30));

        alertaConfig = new ConfiguracionAlertaAsistencia(
                anio,
                new BigDecimal("15.00"),
                new BigDecimal("25.00"),
                "Alertas estándar institucionales"
        );

        grado = new Grado();
        grado.setId(5L);
        grado.setNombre("Noveno");
        grado.setNivel(NivelEducativo.BASICA_SECUNDARIA);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("9-B");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anio);

        profesor = new Profesor();
        profesor.setId(30L);
        profesor.setNombres("Marie");
        profesor.setApellidos("Curie");
        profesor.setActivo(true);

        asignatura = new Asignatura();
        asignatura.setId(50L);
        asignatura.setNombre("Química");

        asignacion = new AsignacionAcademica();
        asignacion.setId(60L);
        asignacion.setGrupo(grupo);
        asignacion.setProfesor(profesor);
        asignacion.setAsignatura(asignatura);
        asignacion.setActivo(true);

        estudiante = new Estudiante();
        estudiante.setId(70L);
        estudiante.setNombres("Niels");
        estudiante.setApellidos("Bohr");
        estudiante.setDocumento("99887766");

        matricula = new Matricula();
        matricula.setId(100L);
        matricula.setNumeroMatricula("MAT-2026-0099");
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anio);
        matricula.setEstado(EstadoMatricula.MATRICULADA);
        matricula.setFechaEfectivaInicio(LocalDate.of(2026, 2, 1));
    }

    // =========================================================================
    // UC-19: REGISTRAR ASISTENCIA, DIAS NO LECTIVOS, EVASIÓN Y AUDITORÍA
    // =========================================================================

    @Test
    @DisplayName("UC-19 E1: Registrar sesión en día no lectivo del calendario escolar debe ser rechazado")
    void testUC19_E1_DiaNoLectivo_DebeLanzarConflicto() {
        SesionAsistenciaRequest req = new SesionAsistenciaRequest();
        req.setGrupoId(20L);
        req.setPeriodoAcademicoId(10L);
        req.setAsignacionAcademicaId(60L);
        req.setFecha(LocalDate.of(2026, 3, 23)); // Lunes festivo / día no lectivo

        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(diaNoLectivoRepository.esDiaNoLectivo(1L, LocalDate.of(2026, 3, 23))).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.crearSesion(req, 1L, "COORDINADOR"));
    }

    @Test
    @DisplayName("UC-19 E1: Docente no asignado a la materia debe ser rechazado con AccessDeniedException")
    void testUC19_E1_DocenteNoAsignado_DebeLanzarAccessDenied() {
        SesionAsistenciaRequest req = new SesionAsistenciaRequest();
        req.setGrupoId(20L);
        req.setPeriodoAcademicoId(10L);
        req.setAsignacionAcademicaId(60L);
        req.setFecha(LocalDate.of(2026, 3, 10));

        Profesor otroDocente = new Profesor();
        otroDocente.setId(999L);

        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(diaNoLectivoRepository.esDiaNoLectivo(1L, LocalDate.of(2026, 3, 10))).thenReturn(false);
        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion));
        when(profesorRepository.findByUsuarioId(200L)).thenReturn(Optional.of(otroDocente));

        assertThrows(AccessDeniedException.class, () -> asistenciaService.crearSesion(req, 200L, "PROFESOR"));
    }

    @Test
    @DisplayName("UC-19 A2 / RN-ASI-07: Detección automática de EVASION_INTERNA_CLASE si asistió a clases previas sin permiso")
    void testUC19_A2_DeteccionEvasionInternaClase() {
        SesionAsistencia sesionTarde = new SesionAsistencia();
        sesionTarde.setId(2L);
        sesionTarde.setGrupo(grupo);
        sesionTarde.setPeriodoAcademico(periodo);
        sesionTarde.setFecha(LocalDate.of(2026, 3, 10));
        sesionTarde.setBloqueHorario(4);

        SesionAsistencia sesionManana = new SesionAsistencia();
        sesionManana.setId(1L);

        RegistroAsistencia regManana = new RegistroAsistencia();
        regManana.setSesionAsistencia(sesionManana);
        regManana.setMatricula(matricula);
        regManana.setEstado(EstadoAsistencia.PRESENTE); // Estuvo presente en la mañana

        RegistroAsistenciaItemRequest item = new RegistroAsistenciaItemRequest();
        item.setMatriculaId(100L);
        item.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA); // Docente de la tarde reporta que no está

        PlanillaAsistenciaLoteRequest planilla = new PlanillaAsistenciaLoteRequest(2L, List.of(item));

        when(sesionRepository.findById(2L)).thenReturn(Optional.of(sesionTarde));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(registroRepository.findByMatriculaIdAndSesionAsistenciaFecha(100L, LocalDate.of(2026, 3, 10)))
                .thenReturn(List.of(regManana));
        when(salidaRepository.findByMatriculaIdAndFecha(100L, LocalDate.of(2026, 3, 10)))
                .thenReturn(Optional.empty()); // No tiene salida autorizada
        when(solicitudExcusaRepository.findAprobadasCubriendoFecha(100L, LocalDate.of(2026, 3, 10)))
                .thenReturn(List.of());
        when(registroRepository.findBySesionAsistenciaIdAndMatriculaId(2L, 100L))
                .thenReturn(Optional.empty());
        when(registroRepository.save(any(RegistroAsistencia.class))).thenAnswer(i -> i.getArgument(0));

        List<RegistroAsistenciaResponse> resps = asistenciaService.registrarPlanillaLote(planilla, 1L, "admin", "COORDINADOR");

        assertNotNull(resps);
        assertEquals(1, resps.size());
        assertEquals(EstadoAsistencia.EVASION_INTERNA_CLASE, resps.get(0).getEstado());
    }

    @Test
    @DisplayName("UC-19 E3 / RN-10: Modificar estado de asistencia previo sin justificación obligatoria debe ser rechazado")
    void testUC19_E3_ModificarAsistenciaSinMotivo_DebeLanzarValidacionException() {
        SesionAsistencia sesion = new SesionAsistencia();
        sesion.setId(1L);
        sesion.setGrupo(grupo);
        sesion.setPeriodoAcademico(periodo);
        sesion.setFecha(LocalDate.of(2026, 3, 10));

        RegistroAsistencia regExistente = new RegistroAsistencia();
        regExistente.setId(10L);
        regExistente.setSesionAsistencia(sesion);
        regExistente.setMatricula(matricula);
        regExistente.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA);

        RegistroAsistenciaItemRequest item = new RegistroAsistenciaItemRequest();
        item.setMatriculaId(100L);
        item.setEstado(EstadoAsistencia.RETARDO);
        item.setMinutosRetardo(10);
        item.setMotivoCorreccion("   "); // Vacío

        PlanillaAsistenciaLoteRequest planilla = new PlanillaAsistenciaLoteRequest(1L, List.of(item));

        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(registroRepository.findBySesionAsistenciaIdAndMatriculaId(1L, 100L)).thenReturn(Optional.of(regExistente));

        assertThrows(ValidacionException.class, () ->
                asistenciaService.registrarPlanillaLote(planilla, 1L, "admin", "COORDINADOR"));
    }

    @Test
    @DisplayName("UC-19 A1 / RN-10: Modificar asistencia con justificación registra traza en AuditoriaAsistencia")
    void testUC19_A1_ModificarAsistenciaConMotivo_RegistraAuditoria() {
        SesionAsistencia sesion = new SesionAsistencia();
        sesion.setId(1L);
        sesion.setGrupo(grupo);
        sesion.setPeriodoAcademico(periodo);
        sesion.setFecha(LocalDate.of(2026, 3, 10));

        RegistroAsistencia regExistente = new RegistroAsistencia();
        regExistente.setId(10L);
        regExistente.setSesionAsistencia(sesion);
        regExistente.setMatricula(matricula);
        regExistente.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA);

        RegistroAsistenciaItemRequest item = new RegistroAsistenciaItemRequest();
        item.setMatriculaId(100L);
        item.setEstado(EstadoAsistencia.RETARDO);
        item.setMinutosRetardo(10);
        item.setMotivoCorreccion("Ingresó 10 minutos tarde con permiso de portería");

        PlanillaAsistenciaLoteRequest planilla = new PlanillaAsistenciaLoteRequest(1L, List.of(item));

        when(sesionRepository.findById(1L)).thenReturn(Optional.of(sesion));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(registroRepository.findBySesionAsistenciaIdAndMatriculaId(1L, 100L)).thenReturn(Optional.of(regExistente));
        when(registroRepository.save(any(RegistroAsistencia.class))).thenAnswer(i -> i.getArgument(0));

        List<RegistroAsistenciaResponse> resps = asistenciaService.registrarPlanillaLote(planilla, 1L, "admin", "COORDINADOR");

        assertNotNull(resps);
        assertEquals(EstadoAsistencia.RETARDO, resps.get(0).getEstado());
        verify(auditoriaRepository, times(1)).save(any(AuditoriaAsistencia.class));
    }

    // =========================================================================
    // UC-03 y UC-09: CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR
    // =========================================================================

    @Test
    @DisplayName("UC-03: Estudiante autenticado consulta exclusivamente su propio récord cronológico de asistencia")
    void testUC03_ConsultaEstudianteContextual_Exitoso() {
        Usuario usuarioEst = new Usuario();
        usuarioEst.setId(500L);
        usuarioEst.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);
        usuarioEst.setPersonaId(70L);

        when(usuarioRepository.findById(500L)).thenReturn(Optional.of(usuarioEst));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(estudiante));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(matriculaRepository.findByEstudianteIdAndAnioLectivoIdAndEstado(70L, 1L, EstadoMatricula.MATRICULADA))
                .thenReturn(Optional.of(matricula));
        when(registroRepository.findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(100L, 10L))
                .thenReturn(List.of());
        when(solicitudExcusaRepository.findByMatriculaIdOrderByFechaRadicacionDesc(100L)).thenReturn(List.of());

        ConsultaAsistenciaEstudianteResponse resp = asistenciaService.consultarMisAsistenciasEstudiante(500L, 10L);

        assertNotNull(resp);
        assertEquals("Niels Bohr", resp.getEstudianteNombreCompleto());
        assertEquals("MAT-2026-0099", resp.getNumeroMatricula());
    }

    @Test
    @DisplayName("UC-09 E1: Acudiente con BLOQUEADO_RESTRICCION_LEGAL debe ser rechazado con AccessDeniedException")
    void testUC09_E1_AcudienteRestriccionLegal_DebeDenegar() {
        Usuario usuarioAcud = new Usuario();
        usuarioAcud.setId(600L);
        usuarioAcud.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);
        usuarioAcud.setPersonaId(80L);

        Acudiente acud = new Acudiente();
        acud.setId(80L);

        EstudianteAcudiente vinculoBloqueado = new EstudianteAcudiente();
        vinculoBloqueado.setEstudiante(estudiante);
        vinculoBloqueado.setAcudiente(acud);
        vinculoBloqueado.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(usuarioRepository.findById(600L)).thenReturn(Optional.of(usuarioAcud));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(estudiante));
        when(estudianteAcudienteRepository.findByEstudiante(estudiante)).thenReturn(List.of(vinculoBloqueado));

        assertThrows(AccessDeniedException.class, () ->
                asistenciaService.consultarAsistenciasAcudiente(600L, 70L, 10L));
    }

    // =========================================================================
    // UC-PROV-18: EXCUSAS POR RANGO DE FECHAS Y SALIDAS EN PORTERÍA
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-18: Radicación y aprobación de excusa que propaga a AUSENCIA_JUSTIFICADA")
    void testUCPROV18_Excusa_AprobacionYPropagacion() {
        Usuario solicitante = new Usuario();
        solicitante.setId(600L);
        solicitante.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);

        SolicitudExcusaAsistenciaRequest req = new SolicitudExcusaAsistenciaRequest();
        req.setMatriculaId(100L);
        req.setTipoJustificacion(TipoJustificacionAsistencia.INCAPACIDAD_MEDICA_EPS);
        req.setFechaInicio(LocalDate.of(2026, 3, 10));
        req.setFechaFin(LocalDate.of(2026, 3, 12));
        req.setMotivoExplicacion("Incapacidad médica por cuadro respiratorio");
        req.setEsDatoSaludSensible(true);

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(usuarioRepository.findById(600L)).thenReturn(Optional.of(solicitante));
        when(solicitudExcusaRepository.count()).thenReturn(0L);
        when(solicitudExcusaRepository.save(any(SolicitudExcusaAsistencia.class))).thenAnswer(i -> {
            SolicitudExcusaAsistencia s = i.getArgument(0);
            s.setId(10L);
            return s;
        });

        SolicitudExcusaAsistenciaResponse resp = excusaService.radicarExcusa(req, 600L);

        assertNotNull(resp);
        assertEquals(EstadoSolicitudExcusa.PENDIENTE_REVISION, resp.getEstado());
        assertTrue(resp.getNumeroRadicado().startsWith("EXC-"));

        // Aprobación por Coordinación
        SolicitudExcusaAsistencia excusaGuardada = new SolicitudExcusaAsistencia();
        excusaGuardada.setId(10L);
        excusaGuardada.setMatricula(matricula);
        excusaGuardada.setFechaInicio(LocalDate.of(2026, 3, 10));
        excusaGuardada.setFechaFin(LocalDate.of(2026, 3, 12));
        excusaGuardada.setEstado(EstadoSolicitudExcusa.PENDIENTE_REVISION);

        RegistroAsistencia falta1 = new RegistroAsistencia();
        falta1.setMatricula(matricula);
        falta1.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA);

        when(solicitudExcusaRepository.findById(10L)).thenReturn(Optional.of(excusaGuardada));
        when(registroRepository.findByMatriculaIdAndSesionAsistenciaFechaBetween(100L, LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 12)))
                .thenReturn(List.of(falta1));

        ResolverSolicitudExcusaRequest resolucion = new ResolverSolicitudExcusaRequest();
        resolucion.setDecision(DecisionSolicitudExcusa.APROBAR);
        resolucion.setObservacionesResolucion("Incapacidad validada con EPS");

        SolicitudExcusaAsistenciaResponse resuelta = excusaService.resolverExcusa(10L, resolucion, 1L);

        assertNotNull(resuelta);
        assertEquals(EstadoSolicitudExcusa.APROBADA, resuelta.getEstado());
        assertEquals(EstadoAsistencia.AUSENTE_JUSTIFICADA, falta1.getEstado());
        verify(registroRepository, times(1)).save(falta1);
    }

    @Test
    @DisplayName("UC-PROV-18 A1 / E1: Salida anticipada con adulto en BLOQUEADO_RESTRICCION_LEGAL debe ser bloqueada")
    void testUCPROV18_SalidaAnticipada_RestriccionLegal_BloqueoPorteria() {
        AutorizacionSalidaAnticipadaRequest req = new AutorizacionSalidaAnticipadaRequest();
        req.setMatriculaId(100L);
        req.setFecha(LocalDate.of(2026, 3, 15));
        req.setHoraSalida("11:30");
        req.setMotivo("Retiro urgente");
        req.setAdultoRetiraNombre("Tío Bloqueado");
        req.setAdultoRetiraDocumento("11223344");
        req.setParentescoAutorizado("TIO");

        Acudiente acudBloqueado = new Acudiente();
        acudBloqueado.setDocumento("11223344");

        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudBloqueado);
        vinculo.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(estudianteAcudienteRepository.findByEstudiante(estudiante)).thenReturn(List.of(vinculo));
        when(salidaRepository.count()).thenReturn(0L);

        assertThrows(AccessDeniedException.class, () ->
                salidaService.registrarSalidaAnticipada(req, 1L));
        verify(salidaRepository, times(1)).save(any(AutorizacionSalidaAnticipada.class));
    }

    @Test
    @DisplayName("UC-PROV-18 A1: Salida anticipada autorizada marca sesiones restantes del día")
    void testUCPROV18_SalidaAnticipada_AutorizadaExitosamente() {
        AutorizacionSalidaAnticipadaRequest req = new AutorizacionSalidaAnticipadaRequest();
        req.setMatriculaId(100L);
        req.setFecha(LocalDate.of(2026, 3, 15));
        req.setHoraSalida("11:30");
        req.setMotivo("Cita médica");
        req.setAdultoRetiraNombre("Mamá Autorizada");
        req.setAdultoRetiraDocumento("55667788");
        req.setParentescoAutorizado("MADRE");

        Acudiente acudOk = new Acudiente();
        acudOk.setDocumento("55667788");

        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudOk);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        RegistroAsistencia faltaTarde = new RegistroAsistencia();
        faltaTarde.setMatricula(matricula);
        faltaTarde.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA);

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(estudianteAcudienteRepository.findByEstudiante(estudiante)).thenReturn(List.of(vinculo));
        when(salidaRepository.count()).thenReturn(0L);
        when(salidaRepository.save(any(AutorizacionSalidaAnticipada.class))).thenAnswer(i -> i.getArgument(0));
        when(registroRepository.findByMatriculaIdAndSesionAsistenciaFecha(100L, LocalDate.of(2026, 3, 15)))
                .thenReturn(List.of(faltaTarde));

        AutorizacionSalidaAnticipadaResponse resp = salidaService.registrarSalidaAnticipada(req, 1L);

        assertNotNull(resp);
        assertEquals(EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA, resp.getEstado());
        assertEquals(EstadoAsistencia.SALIDA_ANTICIPADA_AUTORIZADA, faltaTarde.getEstado());
    }

    // =========================================================================
    // UC-PROV-19: PROTOCOLO DE DESERCIÓN Y SÁBANA GRUPAL
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-19 E2 / RN-ASI-08: Intentar emitir constancia de deserción sin citaciones previas debe ser bloqueado")
    void testUCPROV19_E2_DesercionSinCitacionesPrevias_DebeLanzarConflicto() {
        ActuacionSeguimientoAusentismoRequest req = new ActuacionSeguimientoAusentismoRequest();
        req.setMatriculaId(100L);
        req.setPeriodoAcademicoId(10L);
        req.setTipoActuacion(TipoActuacionSeguimiento.CONSTANCIA_PROTOCOLO_DESERCION_AGOTADO);
        req.setDescripcionGestion("Emisión directa de deserción");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actuacionRepository.existsByMatriculaIdAndTipoActuacion(100L, TipoActuacionSeguimiento.CITACION_ACUDIENTE)).thenReturn(false);
        when(actuacionRepository.existsByMatriculaIdAndTipoActuacion(100L, TipoActuacionSeguimiento.CONTACTO_TELEFONICO)).thenReturn(false);

        assertThrows(ConflictoNegocioException.class, () ->
                seguimientoService.registrarActuacion(req, 1L));
    }

    @Test
    @DisplayName("UC-PROV-19: Emisión válida de constancia de deserción tras agotar citaciones con acudiente")
    void testUCPROV19_DesercionConCitacionesPrevias_Exitoso() {
        ActuacionSeguimientoAusentismoRequest req = new ActuacionSeguimientoAusentismoRequest();
        req.setMatriculaId(100L);
        req.setPeriodoAcademicoId(10L);
        req.setTipoActuacion(TipoActuacionSeguimiento.CONSTANCIA_PROTOCOLO_DESERCION_AGOTADO);
        req.setDescripcionGestion("Agotadas 3 citaciones formales y visita sin respuesta");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actuacionRepository.existsByMatriculaIdAndTipoActuacion(100L, TipoActuacionSeguimiento.CITACION_ACUDIENTE)).thenReturn(true);
        when(actuacionRepository.save(any(ActuacionSeguimientoAusentismo.class))).thenAnswer(i -> {
            ActuacionSeguimientoAusentismo a = i.getArgument(0);
            a.setId(50L);
            return a;
        });

        ActuacionSeguimientoAusentismoResponse resp = seguimientoService.registrarActuacion(req, 1L);

        assertNotNull(resp);
        assertEquals(TipoActuacionSeguimiento.CONSTANCIA_PROTOCOLO_DESERCION_AGOTADO, resp.getTipoActuacion());
    }

    @Test
    @DisplayName("UC-PROV-19 E1: Docente que no es Director de Grupo es rechazado de consultar la sábana")
    void testUCPROV19_E1_NoDirectorGrupo_DebeLanzarAccessDenied() {
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));

        Profesor docente = new Profesor();
        docente.setId(999L);
        when(profesorRepository.findByUsuarioId(150L)).thenReturn(Optional.of(docente));
        when(asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(20L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                asistenciaService.consultarSabanaGrupal(20L, 10L, 150L, "PROFESOR"));
    }
}
