package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.*;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Módulo 03 V2 (Estructura Académica: A# y E#)")
class FrenteBrechasModulo03ServiceTest {

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private GradoAsignaturaRepository gradoAsignaturaRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionAcademicaRepository;

    @Mock
    private DiaNoLectivoRepository diaNoLectivoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Spy
    private AnioLectivoMapper anioLectivoMapper = new AnioLectivoMapper();

    @Spy
    private PeriodoAcademicoMapper periodoAcademicoMapper = new PeriodoAcademicoMapper();

    @Spy
    private GrupoMapper grupoMapper = new GrupoMapper();

    @Spy
    private GradoAsignaturaMapper gradoAsignaturaMapper = new GradoAsignaturaMapper();

    @Spy
    private DiaNoLectivoMapper diaNoLectivoMapper = new DiaNoLectivoMapper();

    @InjectMocks
    private AnioLectivoService anioLectivoService;

    @InjectMocks
    private PeriodoAcademicoService periodoAcademicoService;

    @InjectMocks
    private GrupoService grupoService;

    @InjectMocks
    private GradoAsignaturaService gradoAsignaturaService;

    @InjectMocks
    private DiaNoLectivoService diaNoLectivoService;

    @InjectMocks
    private EstructuraAcademicaContextualService contextualService;

    private AnioLectivo anio2026;
    private PeriodoAcademico periodo1;
    private Grado gradoDecimo;
    private Grupo grupo10A;
    private Area areaHumanidades;
    private Asignatura asignaturaCastellano;

    @BeforeEach
    void setUp() {
        anio2026 = new AnioLectivo(2026, "Año Escolar 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio2026.setId(1L);

        periodo1 = new PeriodoAcademico(anio2026, 1, "Primer Periodo",
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 4, 15), new BigDecimal("25.00"));
        periodo1.setId(10L);
        periodo1.setEstado(EstadoPeriodo.ABIERTO);

        gradoDecimo = new Grado("10", "Décimo", NivelEducativo.MEDIA, 10);
        gradoDecimo.setId(100L);

        grupo10A = new Grupo(anio2026, gradoDecimo, "10-A", Jornada.MANANA, 35);
        grupo10A.setId(200L);

        areaHumanidades = new Area("HUM", "Humanidades y Lengua Castellana", "Área fundamental");
        areaHumanidades.setId(300L);

        asignaturaCastellano = new Asignatura(areaHumanidades, "LENG", "Lengua Castellana", "Asignatura");
        asignaturaCastellano.setId(400L);
    }

    // =========================================================================
    // UC-14: Configurar año lectivo y periodos (E1, E2, E4, A1, A4, A5)
    // =========================================================================

    @Test
    @DisplayName("UC-14 E1: Debe rechazar periodo con fechas que se solapan con otro periodo del mismo año")
    void uc14_E1_debeRechazarSolapamientoDePeriodos() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        // Periodo 1 va del 15 de enero al 15 de abril
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo1));

        // Intento crear Periodo 2 que inicia el 10 de abril (antes del fin del Periodo 1)
        PeriodoAcademicoRequest requestSolapado = new PeriodoAcademicoRequest(
                2, "Segundo Periodo",
                LocalDate.of(2026, 4, 10), LocalDate.of(2026, 6, 20),
                new BigDecimal("25.00")
        );

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.guardar(1L, requestSolapado));

        assertTrue(ex.getMessage().contains("se solapan con el periodo 1"));
    }

    @Test
    @DisplayName("UC-14 E2: Debe rechazar periodo si la suma de porcentajes acumulada supera 100.00%")
    void uc14_E2_debeRechazarPorcentajeMayorA100() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        // Periodo 1 ya tiene 25%
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo1));

        PeriodoAcademicoRequest requestExcesivo = new PeriodoAcademicoRequest(
                2, "Segundo Periodo",
                LocalDate.of(2026, 4, 16), LocalDate.of(2026, 6, 20),
                new BigDecimal("80.00") // 25 + 80 = 105 > 100
        );

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.guardar(1L, requestExcesivo));

        assertTrue(ex.getMessage().contains("no puede superar el 100%"));
    }

    @Test
    @DisplayName("UC-14 E2: Debe validar que al publicar o activar año la distribución de periodos sume exactamente 100.00%")
    void uc14_E2_debeExigirSumaExacta100AlValidarDistribucion() {
        // Solo 75% configurado
        PeriodoAcademico p2 = new PeriodoAcademico(anio2026, 2, "Segundo Periodo",
                LocalDate.of(2026, 4, 16), LocalDate.of(2026, 6, 20), new BigDecimal("50.00"));
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo1, p2));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.validarDistribucionCompleta(1L));

        assertTrue(ex.getMessage().contains("debe ser exactamente 100.00%"));
    }

    @Test
    @DisplayName("UC-14 E4: Debe impedir actualización ordinaria sobre un periodo académico CERRADO")
    void uc14_E4_debeImpedirActualizacionOrdinariaSobrePeriodoCerrado() {
        periodo1.setEstado(EstadoPeriodo.CERRADO);
        when(periodoAcademicoRepository.findById(10L)).thenReturn(Optional.of(periodo1));

        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(
                1, "Primer Periodo Modificado",
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 4, 15),
                new BigDecimal("25.00")
        );

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.actualizar(10L, request));

        assertTrue(ex.getMessage().contains("No se puede modificar ordinariamente un periodo académico CERRADO"));
    }

    @Test
    @DisplayName("UC-14 A5: Debe permitir ajuste excepcional rectoral justificado sobre periodo cerrado con resolución")
    void uc14_A5_debePermitirAjusteExcepcionalRectoralSobrePeriodoCerrado() {
        periodo1.setEstado(EstadoPeriodo.CERRADO);
        when(periodoAcademicoRepository.findById(10L)).thenReturn(Optional.of(periodo1));
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo1));
        when(periodoAcademicoRepository.save(any(PeriodoAcademico.class))).thenAnswer(i -> i.getArgument(0));

        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(
                1, "Primer Periodo Ajustado",
                LocalDate.of(2026, 1, 15), LocalDate.of(2026, 4, 20),
                new BigDecimal("25.00")
        );

        PeriodoAcademicoResponse response = periodoAcademicoService.ajusteExcepcionalRectoral(
                10L, request, "Calamidad climática y suspensión", "RESOLUCION-045-2026"
        );

        assertNotNull(response);
        assertEquals("Primer Periodo Ajustado", response.getNombre());
        assertEquals(LocalDate.of(2026, 4, 20), response.getFechaFin());
        verify(auditoriaService).registrarEvento(eq("AJUSTE_EXCEPCIONAL_PERIODO"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-14 A1: Debe clonar grupos y asignaciones del año origen al año destino en planeación")
    void uc14_A1_debeClonarEstructuraAcademicaCorrectamente() {
        AnioLectivo anio2027 = new AnioLectivo(2027, "Año Escolar 2027", LocalDate.of(2027, 1, 15), LocalDate.of(2027, 11, 30));
        anio2027.setId(2L);
        anio2027.setEstado(EstadoAnioLectivo.EN_PLANEACION);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(anioLectivoRepository.findById(2L)).thenReturn(Optional.of(anio2027));

        when(grupoRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(grupo10A));
        when(grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(2L, 100L, "10-A")).thenReturn(false);

        GradoAsignatura ga = new GradoAsignatura(gradoDecimo, asignaturaCastellano, 4, false);
        ga.setId(500L);
        when(gradoAsignaturaRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(ga));
        when(gradoAsignaturaRepository.findByGradoIdAndAsignaturaIdAndAnioLectivoId(100L, 400L, 2L)).thenReturn(Optional.empty());

        AnioLectivoResponse response = anioLectivoService.clonarEstructuraAnio(1L, 2L);

        assertNotNull(response);
        assertEquals(2027, response.getAnio());
        verify(grupoRepository).save(any(Grupo.class));
        verify(gradoAsignaturaRepository).save(any(GradoAsignatura.class));
        verify(auditoriaService).registrarEvento(eq("CLONACION_ESTRUCTURA_ANIO"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-14 A4: Debe registrar y validar fechas no lectivas dentro de los límites del año")
    void uc14_A4_debeRegistrarFechaNoLectivaExitosamente() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(diaNoLectivoRepository.save(any(DiaNoLectivo.class))).thenAnswer(inv -> {
            DiaNoLectivo d = inv.getArgument(0);
            d.setId(600L);
            return d;
        });

        DiaNoLectivoRequest request = new DiaNoLectivoRequest(
                "Semana Santa",
                LocalDate.of(2026, 3, 30), LocalDate.of(2026, 4, 3),
                TipoDiaNoLectivo.RECESO_ESTUDIANTIL, "Receso religioso institucional"
        );

        DiaNoLectivoResponse response = diaNoLectivoService.guardar(1L, request);

        assertNotNull(response);
        assertEquals("Semana Santa", response.getNombre());
        assertEquals(TipoDiaNoLectivo.RECESO_ESTUDIANTIL, response.getTipo());
        verify(diaNoLectivoRepository).save(any(DiaNoLectivo.class));
    }

    // =========================================================================
    // UC-PROV-06: Administrar grupos y cupos (E1, E2, A2)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-06 E1: Debe rechazar reducción de cupo máximo por debajo de matriculados actuales")
    void ucProv06_E1_debeRechazarReduccionDeCupoPorDebajoDeMatriculados() {
        when(grupoRepository.findById(200L)).thenReturn(Optional.of(grupo10A));
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(gradoRepository.findById(100L)).thenReturn(Optional.of(gradoDecimo));

        // Supongamos que ya existen 30 estudiantes matriculados en 10-A
        when(matriculaRepository.countByGrupoIdAndEstado(200L, EstadoMatricula.MATRICULADA)).thenReturn(30L);

        // Intento reducir el cupo a 25
        GrupoRequest requestInvalido = new GrupoRequest(1L, 100L, "10-A", Jornada.MANANA, 25);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> grupoService.actualizar(200L, requestInvalido));

        assertTrue(ex.getMessage().contains("No se puede reducir el cupo máximo"));
        assertTrue(ex.getMessage().contains("30 estudiantes formalmente matriculados"));
    }

    @Test
    @DisplayName("UC-PROV-06 E2: Debe impedir desactivar o cerrar un grupo que posee estudiantes matriculados activos")
    void ucProv06_E2_debeImpedirCierreDeGrupoConMatriculados() {
        when(grupoRepository.findById(200L)).thenReturn(Optional.of(grupo10A));
        when(matriculaRepository.countByGrupoIdAndEstado(200L, EstadoMatricula.MATRICULADA)).thenReturn(15L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> grupoService.desactivar(200L));

        assertTrue(ex.getMessage().contains("No se puede desactivar o cerrar el grupo"));
        assertTrue(ex.getMessage().contains("15 estudiantes matriculados activos"));
        verify(grupoRepository, never()).save(any());
    }

    @Test
    @DisplayName("UC-PROV-06 A2: Debe permitir cierre lógico sin cupos cuando el grupo no tiene estudiantes matriculados")
    void ucProv06_A2_debePermitirCierreLogicoSinMatriculados() {
        when(grupoRepository.findById(200L)).thenReturn(Optional.of(grupo10A));
        when(matriculaRepository.countByGrupoIdAndEstado(200L, EstadoMatricula.MATRICULADA)).thenReturn(0L);
        when(grupoRepository.save(any(Grupo.class))).thenAnswer(i -> i.getArgument(0));

        grupoService.desactivar(200L);

        assertFalse(grupo10A.getActivo());
        assertEquals(EstadoGrupo.CERRADO_SIN_CUPOS, grupo10A.getEstado());
        verify(grupoRepository).save(grupo10A);
    }

    // =========================================================================
    // UC-PROV-07: Plan de estudios y consolidación de áreas (E1, E2, A1)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-07 E1: Debe rechazar asignación a un área si la suma de ponderaciones supera 100.00%")
    void ucProv07_E1_debeRechazarPonderacionDeAreaExcesiva() {
        when(gradoRepository.findById(100L)).thenReturn(Optional.of(gradoDecimo));

        Asignatura lit = new Asignatura(areaHumanidades, "LIT", "Literatura", "Materia");
        lit.setId(401L);
        when(asignaturaRepository.findById(401L)).thenReturn(Optional.of(lit));

        // En décimo, Lengua Castellana ya tiene 60%
        GradoAsignatura gaExistente = new GradoAsignatura(gradoDecimo, asignaturaCastellano, 3, false);
        gaExistente.setPesoPorcentualEnArea(new BigDecimal("60.00"));
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(100L)).thenReturn(List.of(gaExistente));

        // Intento asignar Literatura con 50% (60 + 50 = 110 > 100)
        GradoAsignaturaRequest request = new GradoAsignaturaRequest(100L, 401L, 2, false);
        request.setPesoPorcentualEnArea(new BigDecimal("50.00"));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> gradoAsignaturaService.asignar(request));

        assertTrue(ex.getMessage().contains("no puede superar el 100.00%"));
    }

    @Test
    @DisplayName("UC-PROV-07 E2: Debe bloquear retiro de asignatura del plan de estudios si tiene asignaciones docentes activas")
    void ucProv07_E2_debeBloquearRetiroDeAsignaturaConAsignacionesActivas() {
        GradoAsignatura ga = new GradoAsignatura(gradoDecimo, asignaturaCastellano, 4, false);
        ga.setId(550L);
        when(gradoAsignaturaRepository.findById(550L)).thenReturn(Optional.of(ga));

        when(grupoRepository.findAll()).thenReturn(List.of(grupo10A));
        when(asignacionAcademicaRepository.existsByGrupoIdAndAsignaturaId(200L, 400L)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> gradoAsignaturaService.desactivar(550L));

        assertTrue(ex.getMessage().contains("No se puede retirar la asignatura"));
        assertTrue(ex.getMessage().contains("asignaciones académicas docentes activas"));
    }

    @Test
    @DisplayName("UC-PROV-07 A1: Debe parametrizar régimen cualitativo por dimensiones en Grado de Preescolar")
    void ucProv07_A1_debeParametrizarPreescolarCualitativo() {
        Grado transicion = new Grado("TRA", "Transición", NivelEducativo.PREESCOLAR, 0);

        assertEquals(RegimenEvaluacion.CUALITATIVO_DIMENSIONES, transicion.getRegimenEvaluacion());
        assertFalse(transicion.getEsGradoTerminal());
    }

    // =========================================================================
    // UC-PROV-08: Consultas contextuales por rol (Estudiante / Acudiente)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-08: Estudiante autenticado consulta el plan de estudios vigente de su grado matriculado")
    void ucProv08_debeConsultarMiPlanEstudiosContextual() {
        Usuario usuarioEstudiante = new Usuario("estudiante1", "hash", Rol.ESTUDIANTE);
        usuarioEstudiante.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);
        usuarioEstudiante.setPersonaId(88L);
        when(usuarioRepository.findByUsername("estudiante1")).thenReturn(Optional.of(usuarioEstudiante));

        when(anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)).thenReturn(Optional.of(anio2026));

        Estudiante estudiante = new Estudiante();
        estudiante.setId(88L);
        estudiante.setNombres("Estudiante");
        estudiante.setApellidos("Uno");
        estudiante.setDocumento("1001");
        estudiante.setFechaNacimiento(LocalDate.of(2010, 5, 10));
        when(estudianteRepository.findById(88L)).thenReturn(Optional.of(estudiante));

        Matricula matricula = new Matricula("MAT-001", estudiante, grupo10A, anio2026);
        when(matriculaRepository.findByEstudianteIdAndAnioLectivoIdAndEstado(88L, 1L, EstadoMatricula.MATRICULADA))
                .thenReturn(Optional.of(matricula));

        GradoAsignatura ga = new GradoAsignatura(gradoDecimo, asignaturaCastellano, 4, false);
        ga.setId(777L);
        when(gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(100L, 1L)).thenReturn(List.of(ga));

        List<GradoAsignaturaResponse> plan = contextualService.consultarMiPlanEstudios("estudiante1");

        assertNotNull(plan);
        assertEquals(1, plan.size());
        assertEquals("Lengua Castellana", plan.getFirst().getAsignaturaNombre());
    }

    // =========================================================================
    // Durable Probes Adicionales: Estructura Académica V2 (S2, S3, S5)
    // =========================================================================

    @Test
    @DisplayName("S2 / UC-14 E2: Debe impedir la activación de un año lectivo si los periodos no suman exactamente 100.00%")
    void s2_uc14_E2_debeRechazarActivacionAnioConPeriodosIncompletos() {
        anio2026.setEstado(EstadoAnioLectivo.EN_PLANEACION);
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)).thenReturn(Optional.empty());

        // Periodo 1 con solo 25.00%
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo1));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> anioLectivoService.activar(1L));

        assertTrue(ex.getMessage().contains("debe ser exactamente 100.00%"));
    }

    @Test
    @DisplayName("S3 / UC-PROV-06 A1: Debe calcular dinámicamente los cupos disponibles restando matriculados al cupo máximo")
    void s3_ucProv06_A1_debeCalcularCuposDisponiblesCorrectamente() {
        when(grupoRepository.findById(200L)).thenReturn(Optional.of(grupo10A));
        // Cupo máximo = 35, matriculados = 12 -> cupos disponibles = 23
        when(matriculaRepository.countByGrupoIdAndEstado(200L, EstadoMatricula.MATRICULADA)).thenReturn(12L);

        GrupoResponse response = grupoService.obtenerPorId(200L);

        assertNotNull(response);
        assertEquals(35, response.getCupoMaximo());
        assertEquals(12, response.getMatriculadosActuales());
        assertEquals(23, response.getCuposDisponibles());
    }

    @Test
    @DisplayName("S5 / UC-PROV-08 E1: Usuario sin rol o sin vinculación activa no expone planes de estudio ajenos")
    void s5_ucProv08_E1_usuarioSinVinculoRetornaListaVacia() {
        Usuario usuarioSinPersona = new Usuario("usuario_anonimo", "hash", Rol.ESTUDIANTE);
        when(usuarioRepository.findByUsername("usuario_anonimo")).thenReturn(Optional.of(usuarioSinPersona));
        when(anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)).thenReturn(Optional.of(anio2026));

        List<GradoAsignaturaResponse> plan = contextualService.consultarMiPlanEstudios("usuario_anonimo");

        assertNotNull(plan);
        assertTrue(plan.isEmpty());
    }
}
