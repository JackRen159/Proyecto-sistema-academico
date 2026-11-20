package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
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
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 07: Calificaciones y Evaluación V2 al 100%")
class FrenteBrechasModulo07ServiceTest {

    @Mock
    private ConfiguracionEscalaSieeRepository escalaRepository;

    @Mock
    private ConfiguracionSieeVersionRepository sieeVersionRepository;

    @Mock
    private ActividadEvaluativaRepository actividadRepository;

    @Mock
    private CalificacionActividadRepository calificacionActividadRepository;

    @Mock
    private CalificacionPeriodoRepository calificacionPeriodoRepository;

    @Mock
    private CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository;

    @Mock
    private AuditoriaCalificacionRepository auditoriaRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

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
    private GradoAsignaturaRepository gradoAsignaturaRepository;

    @Mock
    private ActaNivelacionRepository actaNivelacionRepository;

    @Mock
    private ConvalidacionCalificacionRepository convalidacionRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private ReclamacionCalificacionRepository reclamacionRepository;

    private CalificacionMapper calificacionMapper;
    private CalificacionService calificacionService;
    private NivelacionCalificacionService nivelacionService;
    private ReclamacionCalificacionService reclamacionService;
    private ConvalidacionCalificacionService convalidacionService;

    private AnioLectivo anio;
    private PeriodoAcademico periodo;
    private ConfiguracionEscalaSiee escala;
    private Grado grado;
    private Grupo grupo;
    private Profesor profesor;
    private Asignatura asignatura;
    private Area area;
    private AsignacionAcademica asignacion;
    private Estudiante estudiante;
    private Matricula matricula;

    @BeforeEach
    void setUp() {
        calificacionMapper = new CalificacionMapper();

        calificacionService = new CalificacionService(
                escalaRepository, sieeVersionRepository, actividadRepository, calificacionActividadRepository,
                calificacionPeriodoRepository, calificacionAreaPeriodoRepository, auditoriaRepository,
                asignacionRepository, periodoRepository, anioLectivoRepository, matriculaRepository,
                usuarioRepository, profesorRepository, estudianteRepository, estudianteAcudienteRepository,
                gradoAsignaturaRepository, actaNivelacionRepository, convalidacionRepository, calificacionMapper
        );

        nivelacionService = new NivelacionCalificacionService(
                actaNivelacionRepository, calificacionPeriodoRepository, calificacionService,
                auditoriaRepository, usuarioRepository, profesorRepository, calificacionMapper
        );

        reclamacionService = new ReclamacionCalificacionService(
                reclamacionRepository, calificacionActividadRepository, calificacionPeriodoRepository,
                auditoriaRepository, matriculaRepository, usuarioRepository, calificacionService, calificacionMapper
        );

        convalidacionService = new ConvalidacionCalificacionService(
                convalidacionRepository, calificacionPeriodoRepository, asignaturaRepository,
                matriculaRepository, periodoRepository, usuarioRepository, calificacionService, calificacionMapper
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

        escala = new ConfiguracionEscalaSiee(
                anio,
                new BigDecimal("1.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00"),
                new BigDecimal("3.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.60")
        );
        escala.setDecimalesRedondeo(1);

        grado = new Grado();
        grado.setId(5L);
        grado.setNombre("Décimo");
        grado.setNivel(NivelEducativo.MEDIA);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("10-A");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anio);

        profesor = new Profesor();
        profesor.setId(30L);
        profesor.setNombres("Albert");
        profesor.setApellidos("Einstein");
        profesor.setActivo(true);

        area = new Area();
        area.setId(40L);
        area.setNombre("Ciencias Naturales");

        asignatura = new Asignatura();
        asignatura.setId(50L);
        asignatura.setNombre("Física");
        asignatura.setArea(area);

        asignacion = new AsignacionAcademica();
        asignacion.setId(60L);
        asignacion.setGrupo(grupo);
        asignacion.setProfesor(profesor);
        asignacion.setAsignatura(asignatura);
        asignacion.setActivo(true);

        estudiante = new Estudiante();
        estudiante.setId(70L);
        estudiante.setNombres("Isaac");
        estudiante.setApellidos("Newton");
        estudiante.setDocumento("10203040");

        matricula = new Matricula();
        matricula.setId(100L);
        matricula.setNumeroMatricula("MAT-2026-0001");
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anio);
        matricula.setEstado(EstadoMatricula.MATRICULADA);
    }

    // =========================================================================
    // UC-16: CREAR Y CONFIGURAR ACTIVIDAD EVALUATIVA
    // =========================================================================

    @Test
    @DisplayName("UC-16: Crear actividad evaluativa válida en periodo abierto con SIEE versionado")
    void testUC16_CrearActividad_Exitoso() {
        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(60L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Taller de Ondas");
        req.setPorcentaje(new BigDecimal("25.00"));
        req.setFechaEvaluacion(LocalDate.of(2026, 3, 15));
        req.setCategoriaEvaluativa(CategoriaEvaluativa.COGNITIVA);
        req.setTipoComponente(TipoComponenteEvaluativo.TALLER);

        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actividadRepository.sumPorcentajeByAsignacionAndPeriodo(60L, 10L)).thenReturn(BigDecimal.ZERO);
        when(actividadRepository.save(any(ActividadEvaluativa.class))).thenAnswer(invocation -> {
            ActividadEvaluativa a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        ActividadEvaluativaResponse resp = calificacionService.crearActividad(req);

        assertNotNull(resp);
        assertEquals("Taller de Ondas", resp.getNombre());
        assertEquals(new BigDecimal("25.00"), resp.getPorcentaje());
        assertEquals(CategoriaEvaluativa.COGNITIVA, resp.getCategoriaEvaluativa());
    }

    @Test
    @DisplayName("UC-16 E1: Suma de ponderaciones > 100.00% debe ser rechazada con ConflictoNegocioException")
    void testUC16_E1_SumaPonderacionesMayor100_DebeLanzarExcepcion() {
        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(60L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Examen Final");
        req.setPorcentaje(new BigDecimal("30.00"));

        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actividadRepository.sumPorcentajeByAsignacionAndPeriodo(60L, 10L)).thenReturn(new BigDecimal("80.00"));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.crearActividad(req));
    }

    @Test
    @DisplayName("UC-16 E2: Fecha programada fuera del periodo debe ser rechazada")
    void testUC16_E2_FechaFueraDePeriodo_DebeLanzarExcepcion() {
        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(60L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Quiz");
        req.setPorcentaje(new BigDecimal("10.00"));
        req.setFechaEvaluacion(LocalDate.of(2026, 5, 10)); // Fuera del periodo que finaliza en 2026-04-30

        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.crearActividad(req));
    }

    @Test
    @DisplayName("UC-16 E4: Borrado físico de actividad con calificaciones registradas debe bloquearse")
    void testUC16_E4_EliminarActividadConNotas_DebeLanzarExcepcion() {
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setId(1L);
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(act));
        when(calificacionActividadRepository.existsByActividadId(1L)).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.eliminarActividad(1L, null, "COORDINADOR"));
    }

    @Test
    @DisplayName("UC-16 E5: Docente que no dicta la asignatura debe ser rechazado con AccessDeniedException")
    void testUC16_E5_DocenteNoAsignado_DebeLanzarAccessDenied() {
        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(60L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Taller");
        req.setPorcentaje(new BigDecimal("10.00"));

        Profesor otroDocente = new Profesor();
        otroDocente.setId(99L);

        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion));
        when(profesorRepository.findByUsuarioId(200L)).thenReturn(Optional.of(otroDocente));

        assertThrows(AccessDeniedException.class, () -> calificacionService.crearActividad(req, 200L, "PROFESOR"));
    }

    @Test
    @DisplayName("UC-16 A4: Modificación de actividad ya calificada exige justificación obligatoria")
    void testUC16_A4_ModificarActividadConNotas_SinJustificacion_DebeLanzarValidacionException() {
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setId(1L);
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);
        act.setPorcentaje(new BigDecimal("20.00"));

        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setPorcentaje(new BigDecimal("25.00"));

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(act));
        when(calificacionActividadRepository.existsByActividadId(1L)).thenReturn(true);

        assertThrows(ValidacionException.class, () ->
                calificacionService.actualizarActividad(1L, req, "   ", 1L, "COORDINADOR"));
    }

    // =========================================================================
    // UC-17: REGISTRAR Y CALIFICAR ACTIVIDADES Y DEFINITIVAS
    // =========================================================================

    @Test
    @DisplayName("UC-17 A1: Calificación con inasistencia justificada queda PENDIENTE_POR_EXCUSA sin nota cero")
    void testUC17_A1_InasistenciaJustificada_NoPenalizaConCero() {
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setId(1L);
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);
        act.setPorcentaje(new BigDecimal("20.00"));
        act.setActivo(true);

        RegistroCalificacionRequest notaReq = new RegistroCalificacionRequest();
        notaReq.setMatriculaId(100L);
        notaReq.setEstado(EstadoCalificacionActividad.PENDIENTE_POR_EXCUSA_JUSTIFICADA);
        notaReq.setFechaSupletoria(LocalDate.of(2026, 4, 10));

        RegistroCalificacionesLoteRequest lote = new RegistroCalificacionesLoteRequest(1L, List.of(notaReq));

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(act));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(calificacionActividadRepository.findByActividadIdAndMatriculaId(1L, 100L)).thenReturn(Optional.empty());
        when(calificacionActividadRepository.save(any(CalificacionActividad.class))).thenAnswer(i -> i.getArgument(0));

        List<CalificacionActividadResponse> respuestas = calificacionService.registrarCalificacionesLote(lote, 1L, "admin", "COORDINADOR");

        assertNotNull(respuestas);
        assertEquals(1, respuestas.size());
        assertEquals(EstadoCalificacionActividad.PENDIENTE_POR_EXCUSA_JUSTIFICADA, respuestas.get(0).getEstado());
        assertNull(respuestas.get(0).getValor());
    }

    @Test
    @DisplayName("UC-17 E5: Modificación de calificación existente sin justificación obligatoria debe ser rechazada")
    void testUC17_E5_CorreccionSinJustificacion_DebeLanzarExcepcion() {
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setId(1L);
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);
        act.setPorcentaje(new BigDecimal("20.00"));
        act.setActivo(true);

        CalificacionActividad caExistente = new CalificacionActividad(act, matricula, new BigDecimal("3.00"), "Nota previa");

        RegistroCalificacionRequest notaReq = new RegistroCalificacionRequest();
        notaReq.setMatriculaId(100L);
        notaReq.setValor(new BigDecimal("4.00"));
        notaReq.setMotivoCorreccion("   "); // Vacío

        RegistroCalificacionesLoteRequest lote = new RegistroCalificacionesLoteRequest(1L, List.of(notaReq));

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(act));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(calificacionActividadRepository.findByActividadIdAndMatriculaId(1L, 100L)).thenReturn(Optional.of(caExistente));

        assertThrows(ValidacionException.class, () ->
                calificacionService.registrarCalificacionesLote(lote, 1L, "admin", "COORDINADOR"));
    }

    @Test
    @DisplayName("UC-17 A4 / RN-CAL-06: Bloquear calificaciones con fecha posterior a retiro efectivo")
    void testUC17_A4_BloqueoPostRetiro_DebeLanzarConflicto() {
        ActividadEvaluativa act = new ActividadEvaluativa();
        act.setId(1L);
        act.setAsignacionAcademica(asignacion);
        act.setPeriodoAcademico(periodo);
        act.setFechaEvaluacion(LocalDate.of(2026, 4, 15));
        act.setActivo(true);

        matricula.setFechaEfectivaRetiro(LocalDate.of(2026, 3, 31)); // Retirado antes de la evaluación

        RegistroCalificacionRequest notaReq = new RegistroCalificacionRequest();
        notaReq.setMatriculaId(100L);
        notaReq.setValor(new BigDecimal("3.50"));

        RegistroCalificacionesLoteRequest lote = new RegistroCalificacionesLoteRequest(1L, List.of(notaReq));

        when(actividadRepository.findById(1L)).thenReturn(Optional.of(act));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));

        assertThrows(ConflictoNegocioException.class, () ->
                calificacionService.registrarCalificacionesLote(lote, 1L, "admin", "COORDINADOR"));
    }

    @Test
    @DisplayName("UC-17 Paso 4: Consolidar automáticamente el Área Curricular ponderada según Plan de Estudios")
    void testUC17_ConsolidarAreaCurricular_CalculoExitoso() {
        GradoAsignatura ga = new GradoAsignatura();
        ga.setGrado(grado);
        ga.setAsignatura(asignatura);
        ga.setPesoPorcentualEnArea(new BigDecimal("100.00"));

        CalificacionPeriodo cp = new CalificacionPeriodo(matricula, asignatura, periodo, new BigDecimal("4.20"), DesempenoNacional.ALTO, true);

        when(gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(5L, 1L)).thenReturn(List.of(ga));
        when(calificacionPeriodoRepository.findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(100L, 50L, 10L)).thenReturn(Optional.of(cp));
        when(calificacionAreaPeriodoRepository.findByMatriculaIdAndAreaIdAndPeriodoAcademicoId(100L, 40L, 10L)).thenReturn(Optional.empty());

        calificacionService.consolidarAreaCurricular(matricula, area, periodo, escala);

        verify(calificacionAreaPeriodoRepository, times(1)).save(any(CalificacionAreaPeriodo.class));
    }

    // =========================================================================
    // UC-18: GESTIONAR ACTIVIDADES DE APOYO Y ACTAS DE NIVELACIÓN
    // =========================================================================

    @Test
    @DisplayName("UC-18: Registro de Acta de Nivelación con no desmejoramiento si nota obtenida es menor")
    void testUC18_Nivelacion_NoDesmejoramiento() {
        CalificacionPeriodo cp = new CalificacionPeriodo(matricula, asignatura, periodo, new BigDecimal("2.50"), DesempenoNacional.BAJO, false);
        cp.setId(5L);

        ActaNivelacionRequest req = new ActaNivelacionRequest();
        req.setCalificacionPeriodoId(5L);
        req.setFechaSustentacion(LocalDate.of(2026, 4, 25));
        req.setAsistencia(true);
        req.setNotaObtenida(new BigDecimal("2.00")); // Inferior a la original de 2.50

        when(calificacionPeriodoRepository.findById(5L)).thenReturn(Optional.of(cp));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(actaNivelacionRepository.existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(100L, 50L, 10L)).thenReturn(false);
        when(actaNivelacionRepository.countByPeriodoAcademicoId(10L)).thenReturn(0L);
        when(actaNivelacionRepository.save(any(ActaNivelacion.class))).thenAnswer(i -> i.getArgument(0));

        ActaNivelacionResponse resp = nivelacionService.registrarActaNivelacion(req, 1L, "admin");

        assertNotNull(resp);
        // Debe conservar la nota original de 2.50 en la definitiva vigente
        assertEquals(new BigDecimal("2.5"), resp.getNotaFinalResultante());
        assertEquals(DesempenoNacional.BAJO, resp.getDesempenoResultante());
    }

    @Test
    @DisplayName("UC-18: Registro de Acta de Nivelación exitosa que aprueba la asignatura")
    void testUC18_Nivelacion_AprobacionExitosa() {
        CalificacionPeriodo cp = new CalificacionPeriodo(matricula, asignatura, periodo, new BigDecimal("2.50"), DesempenoNacional.BAJO, false);
        cp.setId(5L);

        ActaNivelacionRequest req = new ActaNivelacionRequest();
        req.setCalificacionPeriodoId(5L);
        req.setFechaSustentacion(LocalDate.of(2026, 4, 25));
        req.setAsistencia(true);
        req.setNotaObtenida(new BigDecimal("3.80")); // Supera la original

        when(calificacionPeriodoRepository.findById(5L)).thenReturn(Optional.of(cp));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(actaNivelacionRepository.existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(100L, 50L, 10L)).thenReturn(false);
        when(actaNivelacionRepository.countByPeriodoAcademicoId(10L)).thenReturn(1L);
        when(actaNivelacionRepository.save(any(ActaNivelacion.class))).thenAnswer(i -> i.getArgument(0));

        ActaNivelacionResponse resp = nivelacionService.registrarActaNivelacion(req, 1L, "admin");

        assertNotNull(resp);
        assertEquals(new BigDecimal("3.8"), resp.getNotaFinalResultante());
        assertEquals(DesempenoNacional.BASICO, resp.getDesempenoResultante());
    }

    @Test
    @DisplayName("UC-18 E3: Intentar registrar dos actas de nivelación para el mismo corte debe fallar")
    void testUC18_E3_ActaDuplicada_DebeLanzarConflicto() {
        CalificacionPeriodo cp = new CalificacionPeriodo(matricula, asignatura, periodo, new BigDecimal("2.50"), DesempenoNacional.BAJO, false);
        cp.setId(5L);

        ActaNivelacionRequest req = new ActaNivelacionRequest();
        req.setCalificacionPeriodoId(5L);

        when(calificacionPeriodoRepository.findById(5L)).thenReturn(Optional.of(cp));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(actaNivelacionRepository.existsByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(100L, 50L, 10L)).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> nivelacionService.registrarActaNivelacion(req, 1L, "admin"));
    }

    // =========================================================================
    // UC-02 y UC-08: CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR
    // =========================================================================

    @Test
    @DisplayName("UC-02: Consulta contextual del estudiante titular entrega jerarquía completa")
    void testUC02_ConsultaEstudianteContextual_Exitoso() {
        Usuario usuarioEst = new Usuario();
        usuarioEst.setId(500L);
        usuarioEst.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);
        usuarioEst.setPersonaId(70L);

        when(usuarioRepository.findById(500L)).thenReturn(Optional.of(usuarioEst));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(estudiante));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(matriculaRepository.findByEstudianteIdAndAnioLectivoIdAndEstado(70L, 1L, EstadoMatricula.MATRICULADA))
                .thenReturn(Optional.of(matricula));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));

        ConsultaCalificacionesEstudianteResponse resp = calificacionService.consultarMisCalificacionesEstudiante(500L, 1L, 10L);

        assertNotNull(resp);
        assertEquals("Isaac Newton", resp.getEstudianteNombreCompleto());
        assertEquals("MAT-2026-0001", resp.getNumeroMatricula());
    }

    @Test
    @DisplayName("UC-08 E1: Acudiente sin vínculo verificado debe ser rechazado con AccessDeniedException")
    void testUC08_E1_AcudienteSinVinculoVerificado_DebeDenegar() {
        Usuario usuarioAcud = new Usuario();
        usuarioAcud.setId(600L);
        usuarioAcud.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);
        usuarioAcud.setPersonaId(80L);

        when(usuarioRepository.findById(600L)).thenReturn(Optional.of(usuarioAcud));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(estudiante));
        when(estudianteAcudienteRepository.findByEstudiante(estudiante)).thenReturn(List.of()); // Sin vínculo

        assertThrows(AccessDeniedException.class, () ->
                calificacionService.consultarCalificacionesAcudiente(600L, 70L, 1L, 10L));
    }

    // =========================================================================
    // UC-PROV-15: RECLAMACIONES ACADÉMICAS CON DOBLE INSTANCIA
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-15: Radicación y resolución en primera instancia con rectificación atómica")
    void testUCPROV15_Reclamacion_RadicacionYResolucionPrimeraInstancia() {
        Usuario solicitante = new Usuario();
        solicitante.setId(500L);
        solicitante.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);

        ReclamacionCalificacionRequest req = new ReclamacionCalificacionRequest();
        req.setTipoCalificacion(TipoCalificacionReclamacion.ACTIVIDAD);
        req.setReferenciaId(1L);
        req.setMatriculaId(100L);
        req.setMotivoReclamacion("Error en la suma de rúbrica");

        when(usuarioRepository.findById(500L)).thenReturn(Optional.of(solicitante));
        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(reclamacionRepository.existsByTipoCalificacionAndReferenciaIdAndEstadoIn(any(), any(), any())).thenReturn(false);
        when(reclamacionRepository.count()).thenReturn(0L);
        when(reclamacionRepository.save(any(ReclamacionCalificacion.class))).thenAnswer(i -> {
            ReclamacionCalificacion r = i.getArgument(0);
            r.setId(10L);
            return r;
        });

        ReclamacionCalificacionResponse resp = reclamacionService.radicarReclamacion(req, 500L);

        assertNotNull(resp);
        assertEquals(EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA, resp.getEstado());
        assertTrue(resp.getNumeroRadicado().startsWith("REC-"));

        // Resolver favorable en 1ra instancia
        ReclamacionCalificacion r = new ReclamacionCalificacion();
        r.setId(10L);
        r.setNumeroRadicado(resp.getNumeroRadicado());
        r.setTipoCalificacion(TipoCalificacionReclamacion.ACTIVIDAD);
        r.setReferenciaId(1L);
        r.setMatricula(matricula);
        r.setEstado(EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA);

        ResolverReclamacionRequest resolucion = new ResolverReclamacionRequest();
        resolucion.setDecision(DecisionReclamacion.APROBAR_RECTIFICACION);
        resolucion.setMotivo("Verificado error aritmético en hoja física");
        resolucion.setNuevaNotaAprobada(new BigDecimal("4.20"));

        CalificacionActividad ca = new CalificacionActividad();
        ca.setId(1L);
        ca.setValor(new BigDecimal("3.50"));
        ca.setMatricula(matricula);

        when(reclamacionRepository.findById(10L)).thenReturn(Optional.of(r));
        when(calificacionActividadRepository.findById(1L)).thenReturn(Optional.of(ca));

        ReclamacionCalificacionResponse resuelto = reclamacionService.resolverPrimeraInstancia(10L, resolucion, 30L, "profesor1");

        assertNotNull(resuelto);
        assertEquals(EstadoReclamacionCalificacion.RESUELTA_FAVORABLE_DOCENTE, resuelto.getEstado());
        assertEquals(new BigDecimal("4.20"), ca.getValor());
        verify(auditoriaRepository, times(1)).save(any(AuditoriaCalificacion.class));
    }

    // =========================================================================
    // UC-PROV-16: CONVALIDACIÓN CURRICULAR DE PERIODOS PREVIOS
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-16: Convalidación de periodos previos por traslado entrante asienta notas oficiales")
    void testUCPROV16_Convalidacion_TrasladoEntrante() {
        ConvalidacionDetalleRequest det = new ConvalidacionDetalleRequest(50L, new BigDecimal("4.00"), "Homologado");
        ConvalidacionCalificacionRequest req = new ConvalidacionCalificacionRequest();
        req.setMatriculaId(100L);
        req.setPeriodoAcademicoId(10L);
        req.setTipoOrigen(TipoOrigenConvalidacion.TRASLADO_ENTRANTE);
        req.setInstitucionOrigen("Colegio San José");
        req.setDetalles(List.of(det));

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(asignaturaRepository.findById(50L)).thenReturn(Optional.of(asignatura));
        when(convalidacionRepository.save(any(ConvalidacionCalificacion.class))).thenAnswer(i -> {
            ConvalidacionCalificacion c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        ConvalidacionCalificacionResponse resp = convalidacionService.registrarConvalidacion(req, 1L);

        assertNotNull(resp);
        assertEquals(TipoOrigenConvalidacion.TRASLADO_ENTRANTE, resp.getTipoOrigen());
        verify(calificacionPeriodoRepository, times(1)).save(any(CalificacionPeriodo.class));
    }

    // =========================================================================
    // UC-PROV-17: SÁBANA CONSOLIDADA GRUPAL Y TABLERO DE AVANCE
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-17 E1: Docente que no es Director de Grupo es rechazado de consultar la sábana")
    void testUCPROV17_E1_NoDirectorGrupo_DebeLanzarAccessDenied() {
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(matriculaRepository.findByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));

        Profesor docente = new Profesor();
        docente.setId(99L);
        when(profesorRepository.findByUsuarioId(150L)).thenReturn(Optional.of(docente));
        when(asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(20L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                calificacionService.consultarSabanaConsolidada(20L, 10L, 150L, "PROFESOR"));
    }

    @Test
    @DisplayName("UC-PROV-17: Director de Grupo consulta sábana consolidada con conteo de alertas de riesgo")
    void testUCPROV17_DirectorGrupo_ConsultaSabanaConAlertas() {
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(matriculaRepository.findByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));

        Profesor docenteDir = new Profesor();
        docenteDir.setId(30L);
        AsignacionAcademica asigDir = new AsignacionAcademica();
        asigDir.setProfesor(docenteDir);
        asigDir.setEsDirectorGrupo(true);

        when(profesorRepository.findByUsuarioId(150L)).thenReturn(Optional.of(docenteDir));
        when(asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(20L)).thenReturn(Optional.of(asigDir));

        CalificacionPeriodo cpBaja = new CalificacionPeriodo(matricula, asignatura, periodo, new BigDecimal("2.00"), DesempenoNacional.BAJO, false);
        when(calificacionPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(100L, 10L)).thenReturn(List.of(cpBaja));
        when(calificacionAreaPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(100L, 10L)).thenReturn(List.of());

        SabanaConsolidadaResponse sabana = calificacionService.consultarSabanaConsolidada(20L, 10L, 150L, "PROFESOR");

        assertNotNull(sabana);
        assertEquals(1, sabana.getTotalEstudiantes());
        assertEquals(1, sabana.getTotalEstudiantesEnRiesgo());
        assertTrue(sabana.getEstudiantes().get(0).getEnRiesgoAcademico());
        assertEquals(1, sabana.getEstudiantes().get(0).getAsignaturasReprobadas());
    }
}
