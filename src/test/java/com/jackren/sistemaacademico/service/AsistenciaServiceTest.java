package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.AsistenciaMapper;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AsistenciaService (Validaciones, Toma de Planilla y Alertas de Ausentismo)")
class AsistenciaServiceTest {

    @Mock
    private ConfiguracionAlertaAsistenciaRepository alertaRepository;

    @Mock
    private SesionAsistenciaRepository sesionRepository;

    @Mock
    private RegistroAsistenciaRepository registroRepository;

    @Mock
    private JustificacionAsistenciaRepository justificacionRepository;

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

    @Spy
    private AsistenciaMapper asistenciaMapper = new AsistenciaMapper();

    @InjectMocks
    private AsistenciaService asistenciaService;

    private AnioLectivo anioLectivo;
    private PeriodoAcademico periodo;
    private Grupo grupo;
    private Asignatura asignatura;
    private AsignacionAcademica asignacion;
    private Matricula matricula;
    private ConfiguracionAlertaAsistencia alertaConfig;

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
        asignatura.setNombre("Matemáticas");

        Profesor profesor = new Profesor();
        profesor.setId(20L);
        profesor.setNombres("Carl");
        profesor.setApellidos("Gauss");

        asignacion = new AsignacionAcademica();
        asignacion.setId(50L);
        asignacion.setProfesor(profesor);
        asignacion.setGrupo(grupo);
        asignacion.setAsignatura(asignatura);
        asignacion.setActivo(true);

        Estudiante estudiante = new Estudiante();
        estudiante.setId(7L);
        estudiante.setNombres("Leonhard");
        estudiante.setApellidos("Euler");
        estudiante.setDocumento("10203040");

        matricula = new Matricula();
        matricula.setId(1000L);
        matricula.setNumeroMatricula("MAT-2026-0001");
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anioLectivo);
        matricula.setEstado(EstadoMatricula.MATRICULADA);

        alertaConfig = new ConfiguracionAlertaAsistencia(
                anioLectivo,
                new BigDecimal("15.00"),
                new BigDecimal("25.00"),
                "Configuración institucional"
        );
        alertaConfig.setId(1L);
    }

    @Test
    @DisplayName("guardarOActualizarConfiguracionAlerta debe rechazar si alerta amarilla es mayor o igual a roja")
    void guardarAlerta_PorcentajesIncoherentes_DebeLanzarExcepcion() {
        ConfiguracionAlertaAsistenciaRequest req = new ConfiguracionAlertaAsistenciaRequest();
        req.setAnioLectivoId(1L);
        req.setPorcentajeAlertaAmarilla(new BigDecimal("25.00"));
        req.setPorcentajeAlertaRoja(new BigDecimal("20.00")); // Incoherente: amarilla > roja

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.guardarOActualizarConfiguracionAlerta(req));
    }

    @Test
    @DisplayName("crearSesion debe rechazar registro si periodo académico está cerrado")
    void crearSesion_PeriodoCerrado_DebeLanzarExcepcion() {
        periodo.setEstado(EstadoPeriodo.CERRADO);
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));

        SesionAsistenciaRequest req = new SesionAsistenciaRequest();
        req.setGrupoId(5L);
        req.setPeriodoAcademicoId(10L);
        req.setTipoRegistro(TipoRegistroAsistencia.SESION_ASIGNATURA);

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.crearSesion(req, 1L));
    }

    @Test
    @DisplayName("crearSesion debe rechazar sesión duplicada en misma fecha y bloque")
    void crearSesion_SesionDuplicada_DebeLanzarExcepcion() {
        LocalDate hoy = LocalDate.now();
        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupo));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(asignacionRepository.findById(50L)).thenReturn(Optional.of(asignacion));
        when(sesionRepository.findByAsignacionAcademicaIdAndFechaAndBloqueHorario(50L, hoy, 1))
                .thenReturn(Optional.of(new SesionAsistencia()));

        SesionAsistenciaRequest req = new SesionAsistenciaRequest();
        req.setAsignacionAcademicaId(50L);
        req.setGrupoId(5L);
        req.setPeriodoAcademicoId(10L);
        req.setFecha(hoy);
        req.setBloqueHorario(1);
        req.setTipoRegistro(TipoRegistroAsistencia.SESION_ASIGNATURA);

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.crearSesion(req, 1L));
    }

    @Test
    @DisplayName("registrarPlanillaLote debe rechazar estudiante en estado RETIRADA")
    void registrarPlanilla_EstudianteRetirado_DebeLanzarExcepcion() {
        SesionAsistencia sesion = new SesionAsistencia();
        sesion.setId(200L);
        sesion.setGrupo(grupo);
        sesion.setPeriodoAcademico(periodo);

        matricula.setEstado(EstadoMatricula.RETIRADA);

        when(sesionRepository.findById(200L)).thenReturn(Optional.of(sesion));
        when(matriculaRepository.findById(1000L)).thenReturn(Optional.of(matricula));

        RegistroAsistenciaItemRequest item = new RegistroAsistenciaItemRequest(1000L, EstadoAsistencia.PRESENTE, 0, null);
        PlanillaAsistenciaLoteRequest lote = new PlanillaAsistenciaLoteRequest(200L, List.of(item));

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.registrarPlanillaLote(lote));
    }

    @Test
    @DisplayName("justificarInasistencia debe rechazar justificación para estado PRESENTE")
    void justificarInasistencia_EstadoPresente_DebeLanzarExcepcion() {
        RegistroAsistencia reg = new RegistroAsistencia();
        reg.setId(300L);
        reg.setEstado(EstadoAsistencia.PRESENTE);

        when(registroRepository.findById(300L)).thenReturn(Optional.of(reg));

        JustificacionAsistenciaRequest req = new JustificacionAsistenciaRequest("Cita médica", "RAD-001", LocalDate.now(), null);

        assertThrows(ConflictoNegocioException.class, () -> asistenciaService.justificarInasistencia(300L, req, 1L, "coord"));
    }

    @Test
    @DisplayName("justificarInasistencia debe cambiar AUSENTE_INJUSTIFICADA a AUSENTE_JUSTIFICADA")
    void justificarInasistencia_Exito() {
        RegistroAsistencia reg = new RegistroAsistencia();
        reg.setId(300L);
        reg.setMatricula(matricula);
        reg.setEstado(EstadoAsistencia.AUSENTE_INJUSTIFICADA);

        when(registroRepository.findById(300L)).thenReturn(Optional.of(reg));
        when(justificacionRepository.findByRegistroAsistenciaId(300L)).thenReturn(Optional.empty());
        when(justificacionRepository.save(any(JustificacionAsistencia.class))).thenAnswer(inv -> inv.getArgument(0));

        JustificacionAsistenciaRequest req = new JustificacionAsistenciaRequest("Incapacidad EPS", "RAD-001", LocalDate.now(), null);
        JustificacionAsistenciaResponse res = asistenciaService.justificarInasistencia(300L, req, 1L, "coord");

        assertNotNull(res);
        assertEquals(EstadoAsistencia.AUSENTE_JUSTIFICADA, reg.getEstado());
        assertEquals("Incapacidad EPS", res.getMotivo());
        verify(registroRepository, times(1)).save(reg);
    }

    @Test
    @DisplayName("obtenerResumenEstudianteAsignatura debe calcular 20% inasistencia y clasificar en ALERTA AMARILLA")
    void obtenerResumenEstudiante_AlertaAmarilla() {
        when(matriculaRepository.findById(1000L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(asignacionRepository.findById(50L)).thenReturn(Optional.of(asignacion));
        when(alertaRepository.findByAnioLectivoId(1L)).thenReturn(Optional.of(alertaConfig));

        // 10 sesiones totales: 8 presentes, 2 faltas injustificadas -> 20% inasistencia -> AMARILLA (umbral [15, 25))
        when(registroRepository.countTotalSesionesEstudianteAsignatura(1000L, 10L, 50L)).thenReturn(10L);
        when(registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(1000L, 10L, 50L, EstadoAsistencia.PRESENTE)).thenReturn(8L);
        when(registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(1000L, 10L, 50L, EstadoAsistencia.AUSENTE_INJUSTIFICADA)).thenReturn(2L);
        when(registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(1000L, 10L, 50L, EstadoAsistencia.AUSENTE_JUSTIFICADA)).thenReturn(0L);
        when(registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(1000L, 10L, 50L, EstadoAsistencia.RETARDO)).thenReturn(0L);
        when(registroRepository.countByMatriculaAndPeriodoAndAsignacionAndEstado(1000L, 10L, 50L, EstadoAsistencia.RETIRO_ANTICIPADO)).thenReturn(0L);

        ResumenAsistenciaEstudianteResponse res = asistenciaService.obtenerResumenEstudianteAsignatura(1000L, 10L, 50L);

        assertNotNull(res);
        assertEquals(10L, res.getTotalSesiones());
        assertEquals(new BigDecimal("20.00"), res.getPorcentajeInasistencia());
        assertEquals(new BigDecimal("80.00"), res.getPorcentajeAsistencia());
        assertEquals("AMARILLA", res.getNivelAlerta());
    }
}
