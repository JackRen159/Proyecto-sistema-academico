package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.CalificacionMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - CalificacionService (Reglas SIEE, Validaciones y Cálculo de Definitivas)")
class CalificacionServiceTest {

    @Mock
    private ConfiguracionEscalaSieeRepository escalaRepository;

    @Mock
    private ActividadEvaluativaRepository actividadRepository;

    @Mock
    private CalificacionActividadRepository calificacionActividadRepository;

    @Mock
    private CalificacionPeriodoRepository calificacionPeriodoRepository;

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

    @Spy
    private CalificacionMapper calificacionMapper = new CalificacionMapper();

    @InjectMocks
    private CalificacionService calificacionService;

    private AnioLectivo anioLectivo;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    private Asignatura asignatura;
    private AsignacionAcademica asignacion;
    private ConfiguracionEscalaSiee escala;
    private Matricula matricula;

    @BeforeEach
    void setUp() {
        anioLectivo = new AnioLectivo();
        anioLectivo.setId(1L);
        anioLectivo.setAnio(2026);
        anioLectivo.setEstado(EstadoAnioLectivo.ACTIVO);

        periodo = new PeriodoAcademico();
        periodo.setId(10L);
        periodo.setAnioLectivo(anioLectivo);
        periodo.setNumero(1);
        periodo.setNombre("Primer Periodo");
        periodo.setEstado(EstadoPeriodo.ABIERTO);

        Grado grado = new Grado();
        grado.setId(2L);
        grado.setNombre("Décimo");

        grupo = new Grupo();
        grupo.setId(5L);
        grupo.setNombre("10-A");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anioLectivo);
        grupo.setActivo(true);

        asignatura = new Asignatura();
        asignatura.setId(100L);
        asignatura.setNombre("Física");

        Profesor profesor = new Profesor();
        profesor.setId(20L);
        profesor.setNombres("Albert");
        profesor.setApellidos("Einstein");

        asignacion = new AsignacionAcademica();
        asignacion.setId(50L);
        asignacion.setProfesor(profesor);
        asignacion.setGrupo(grupo);
        asignacion.setAsignatura(asignatura);
        asignacion.setActivo(true);

        escala = new ConfiguracionEscalaSiee(
                anioLectivo,
                new BigDecimal("1.00"),
                new BigDecimal("5.00"),
                new BigDecimal("3.00"),
                new BigDecimal("3.00"),
                new BigDecimal("4.00"),
                new BigDecimal("4.60")
        );
        escala.setId(1L);
        escala.setDecimalesRedondeo(1);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(7L);
        estudiante.setNombres("Isaac");
        estudiante.setApellidos("Newton");
        estudiante.setDocumento("10203040");

        matricula = new Matricula();
        matricula.setId(1000L);
        matricula.setNumeroMatricula("MAT-2026-0001");
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anioLectivo);
        matricula.setEstado(EstadoMatricula.MATRICULADA);
    }

    @Test
    @DisplayName("guardarOActualizarEscala debe rechazar configuración en año lectivo cerrado")
    void guardarEscala_AnioCerrado_DebeLanzarExcepcion() {
        anioLectivo.setEstado(EstadoAnioLectivo.CERRADO);
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));

        ConfiguracionEscalaSieeRequest req = new ConfiguracionEscalaSieeRequest();
        req.setAnioLectivoId(1L);

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.guardarOActualizarEscala(req));
    }

    @Test
    @DisplayName("guardarOActualizarEscala debe rechazar umbrales incoherentes")
    void guardarEscala_UmbralesIncoherentes_DebeLanzarExcepcion() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));

        ConfiguracionEscalaSieeRequest req = new ConfiguracionEscalaSieeRequest();
        req.setAnioLectivoId(1L);
        req.setNotaMinima(new BigDecimal("3.50")); // Incoherente: minima >= basico
        req.setEscalaBasicoMin(new BigDecimal("3.00"));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.guardarOActualizarEscala(req));
    }

    @Test
    @DisplayName("crearActividad debe rechazar actividad si periodo está cerrado")
    void crearActividad_PeriodoCerrado_DebeLanzarExcepcion() {
        periodo.setEstado(EstadoPeriodo.CERRADO);
        when(asignacionRepository.findById(50L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));

        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(50L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Quiz Cinemática");
        req.setPorcentaje(new BigDecimal("20.00"));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.crearActividad(req));
    }

    @Test
    @DisplayName("crearActividad debe rechazar si la suma de porcentajes excede 100%")
    void crearActividad_ExcesoPorcentaje_DebeLanzarExcepcion() {
        when(asignacionRepository.findById(50L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actividadRepository.sumPorcentajeByAsignacionAndPeriodo(50L, 10L)).thenReturn(new BigDecimal("85.00"));

        ActividadEvaluativaRequest req = new ActividadEvaluativaRequest();
        req.setAsignacionAcademicaId(50L);
        req.setPeriodoAcademicoId(10L);
        req.setNombre("Examen Parcial");
        req.setPorcentaje(new BigDecimal("20.00")); // 85 + 20 = 105% > 100%

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.crearActividad(req));
    }

    @Test
    @DisplayName("registrarCalificacionesLote debe rechazar nota fuera del rango de la escala")
    void registrarCalificaciones_NotaFueraDeRango_DebeLanzarExcepcion() {
        ActividadEvaluativa actividad = new ActividadEvaluativa();
        actividad.setId(100L);
        actividad.setAsignacionAcademica(asignacion);
        actividad.setPeriodoAcademico(periodo);
        actividad.setActivo(true);

        when(actividadRepository.findById(100L)).thenReturn(Optional.of(actividad));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));

        RegistroCalificacionRequest notaReq = new RegistroCalificacionRequest(1000L, new BigDecimal("5.50"), "Nota inválida");
        RegistroCalificacionesLoteRequest lote = new RegistroCalificacionesLoteRequest(100L, List.of(notaReq));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.registrarCalificacionesLote(lote, 1L, "admin"));
    }

    @Test
    @DisplayName("registrarCalificacionesLote debe rechazar estudiante en estado RETIRADA")
    void registrarCalificaciones_EstudianteRetirado_DebeLanzarExcepcion() {
        ActividadEvaluativa actividad = new ActividadEvaluativa();
        actividad.setId(100L);
        actividad.setAsignacionAcademica(asignacion);
        actividad.setPeriodoAcademico(periodo);
        actividad.setActivo(true);

        matricula.setEstado(EstadoMatricula.RETIRADA);

        when(actividadRepository.findById(100L)).thenReturn(Optional.of(actividad));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(matriculaRepository.findById(1000L)).thenReturn(Optional.of(matricula));

        RegistroCalificacionRequest notaReq = new RegistroCalificacionRequest(1000L, new BigDecimal("4.00"), "Nota");
        RegistroCalificacionesLoteRequest lote = new RegistroCalificacionesLoteRequest(100L, List.of(notaReq));

        assertThrows(ConflictoNegocioException.class, () -> calificacionService.registrarCalificacionesLote(lote, 1L, "admin"));
    }

    @Test
    @DisplayName("calcularDefinitivasPeriodo debe calcular promedio ponderado y homologar desempeño ALTO")
    void calcularDefinitivasPeriodo_CalculoPonderadoYDesempenoAlto() {
        ActividadEvaluativa act1 = new ActividadEvaluativa();
        act1.setId(1L);
        act1.setPorcentaje(new BigDecimal("40.00"));

        ActividadEvaluativa act2 = new ActividadEvaluativa();
        act2.setId(2L);
        act2.setPorcentaje(new BigDecimal("60.00"));

        when(asignacionRepository.findById(50L)).thenReturn(Optional.of(asignacion));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(actividadRepository.findByAsignacionAcademicaIdAndPeriodoAcademicoIdAndActivoTrue(50L, 10L))
                .thenReturn(List.of(act1, act2));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(matriculaRepository.findByGrupoIdAndEstado(5L, EstadoMatricula.MATRICULADA))
                .thenReturn(List.of(matricula));

        // act1 = 4.0 (40%), act2 = 4.5 (60%) -> Promedio = (4.0*40 + 4.5*60)/100 = (160 + 270)/100 = 4.30 -> ALTO
        CalificacionActividad nota1 = new CalificacionActividad(act1, matricula, new BigDecimal("4.00"), null);
        CalificacionActividad nota2 = new CalificacionActividad(act2, matricula, new BigDecimal("4.50"), null);

        when(calificacionActividadRepository.findByActividadIdAndMatriculaId(1L, 1000L)).thenReturn(Optional.of(nota1));
        when(calificacionActividadRepository.findByActividadIdAndMatriculaId(2L, 1000L)).thenReturn(Optional.of(nota2));
        when(calificacionPeriodoRepository.findByMatriculaIdAndAsignaturaIdAndPeriodoAcademicoId(1000L, 100L, 10L))
                .thenReturn(Optional.empty());
        when(calificacionPeriodoRepository.save(any(CalificacionPeriodo.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<CalificacionPeriodoResponse> result = calificacionService.calcularDefinitivasPeriodo(50L, 10L);

        assertNotNull(result);
        assertEquals(1, result.size());
        CalificacionPeriodoResponse r = result.get(0);
        assertEquals(new BigDecimal("4.3"), r.getNotaDefinitiva());
        assertEquals(DesempenoNacional.ALTO, r.getDesempeno());
        assertTrue(r.getAprobada());
    }

    @Test
    @DisplayName("registrarRecuperacion debe actualizar nota final, desempeño y generar registro de auditoría")
    void registrarRecuperacion_ActualizaNotaYRegistraAuditoria() {
        CalificacionPeriodo calif = new CalificacionPeriodo(
                matricula, asignatura, periodo, new BigDecimal("2.50"), DesempenoNacional.BAJO, false
        );
        calif.setId(200L);

        when(calificacionPeriodoRepository.findById(200L)).thenReturn(Optional.of(calif));
        when(escalaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(escala));
        when(calificacionPeriodoRepository.save(any(CalificacionPeriodo.class))).thenAnswer(inv -> inv.getArgument(0));

        CalificacionPeriodoResponse res = calificacionService.registrarRecuperacion(
                200L, new BigDecimal("3.50"), "Aprobó plan de nivelación con taller y sustentación", 1L, "coordinador1"
        );

        assertNotNull(res);
        assertEquals(new BigDecimal("3.50"), res.getNotaRecuperacion());
        assertEquals(new BigDecimal("3.50"), res.getNotaFinal());
        assertEquals(DesempenoNacional.BASICO, res.getDesempeno());
        assertTrue(res.getAprobada());

        verify(auditoriaRepository, times(1)).save(any(AuditoriaCalificacion.class));
    }
}
