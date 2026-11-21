package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Debido Proceso (UC-PROV-25), Medidas/Sanciones (UC-24) y Comité (UC-PROV-26)")
class DebidoProcesoYMedidasServiceTest {

    @Mock private ExpedienteDebidoProcesoRepository expedienteRepository;
    @Mock private DescargoDebidoProcesoRepository descargoRepository;
    @Mock private RecursoImpugnacionRepository recursoRepository;
    @Mock private MedidaSancionConvivenciaRepository medidaRepository;
    @Mock private PlanContinuidadAcademicaRepository planContinuidadRepository;
    @Mock private CasoConvivenciaRepository casoRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProfesorRepository profesorRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private DiaNoLectivoRepository diaNoLectivoRepository;
    @Mock private ActaComiteConvivenciaRepository actaRepository;
    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;

    private DebidoProcesoService debidoProcesoService;
    private MedidaSancionService medidaSancionService;
    private ComiteConvivenciaService comiteConvivenciaService;

    private Estudiante estudiante;
    private Usuario rector;
    private Usuario coordinador;
    private CasoConvivencia caso;
    private AnioLectivo anioLectivo;

    @BeforeEach
    void setUp() {
        debidoProcesoService = new DebidoProcesoService(
                expedienteRepository,
                descargoRepository,
                recursoRepository,
                medidaRepository,
                casoRepository,
                estudianteRepository,
                usuarioRepository,
                diaNoLectivoRepository,
                auditoriaGlobalService
        );

        medidaSancionService = new MedidaSancionService(
                medidaRepository,
                planContinuidadRepository,
                expedienteRepository,
                usuarioRepository,
                profesorRepository,
                matriculaRepository,
                auditoriaGlobalService
        );

        comiteConvivenciaService = new ComiteConvivenciaService(
                actaRepository,
                anioLectivoRepository,
                usuarioRepository,
                auditoriaGlobalService
        );

        estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Juan");
        estudiante.setApellidos("Gómez");

        rector = new Usuario("rector1", "hash", Rol.RECTOR);
        rector.setId(100L);

        coordinador = new Usuario("coord1", "hash", Rol.COORDINADOR_CONVIVENCIA);
        coordinador.setId(101L);

        anioLectivo = new AnioLectivo();
        anioLectivo.setId(1L);
        anioLectivo.setAnio(2026);

        caso = new CasoConvivencia();
        caso.setId(20L);
        caso.setCodigoExpediente("EXP-CONV-2026-0001");
        caso.setAnioLectivo(anioLectivo);
        caso.setEstudianteImplicado(estudiante);
    }

    @Test
    @DisplayName("UC-PROV-25: Apertura de expediente con cálculo de días hábiles lectivos")
    void abrirExpediente_Exitoso() {
        AperturaCargosRequest request = new AperturaCargosRequest(
                20L, 10L,
                "Hechos ocurridos el 10 de mayo de 2026 en el pasillo de bachillerato",
                "Art. 28 Manual de Convivencia vigente",
                "Testimonio 1 y 2 anonimizados",
                5, true, "Acompañamiento preventivo"
        );

        when(casoRepository.findById(20L)).thenReturn(Optional.of(caso));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(101L)).thenReturn(Optional.of(coordinador));
        when(diaNoLectivoRepository.esDiaNoLectivo(eq(1L), any(LocalDate.class))).thenReturn(false);
        when(expedienteRepository.save(any(ExpedienteDebidoProceso.class))).thenAnswer(i -> {
            ExpedienteDebidoProceso e = i.getArgument(0);
            e.setId(30L);
            return e;
        });

        ExpedienteDebidoProcesoResponse response = debidoProcesoService.abrirExpedienteYNotificarCargos(request, 101L);

        assertNotNull(response);
        assertEquals(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS, response.estadoExpediente());
        assertEquals(5, response.diasHabilesDescargos());
        assertNotNull(response.fechaLimiteDescargos());
    }

    @Test
    @DisplayName("UC-PROV-25 E1: Rechazo de apertura si faltan normas del Manual o pruebas trasladadas")
    void abrirExpediente_FaltanNormas_LanzaValidacionException() {
        AperturaCargosRequest request = new AperturaCargosRequest(
                20L, 10L,
                "Hechos ocurridos el 10 de mayo de 2026 en el pasillo de bachillerato",
                "", // Normas vacías
                "Pruebas",
                5, false, null
        );

        when(casoRepository.findById(20L)).thenReturn(Optional.of(caso));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(101L)).thenReturn(Optional.of(coordinador));

        assertThrows(ValidacionException.class, () -> debidoProcesoService.abrirExpedienteYNotificarCargos(request, 101L));
    }

    @Test
    @DisplayName("UC-PROV-25: Radicación de descargos habilita estado para decisión en UC-24")
    void radicarDescargos_Exitoso_HabilitaDecision() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(30L);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS);

        RadicarDescargosRequest request = new RadicarDescargosRequest(
                "Versión y argumentos del estudiante con acompañamiento de Personería",
                "Copia de comunicación", false, true
        );

        when(expedienteRepository.findById(30L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(new Usuario("estudiante1", "hash", Rol.ESTUDIANTE)));
        when(descargoRepository.save(any(DescargoDebidoProceso.class))).thenAnswer(i -> {
            DescargoDebidoProceso d = i.getArgument(0);
            d.setId(40L);
            return d;
        });

        DescargoDebidoProcesoResponse response = debidoProcesoService.radicarDescargos(30L, request, 10L);

        assertNotNull(response);
        assertEquals(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION, exp.getEstadoExpediente());
    }

    @Test
    @DisplayName("UC-24 RN-07 / E1: Bloqueo de decisión sancionatoria si el debido proceso sigue en término de descargos")
    void decidirMedida_DebidoProcesoIncompleto_LanzaConflictoNegocioException() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(30L);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS);

        MedidaSancionRequest request = new MedidaSancionRequest(
                30L, TipoMedidaSancion.AMONESTACION_ESCRITA, false,
                "Falta formativa analizada", "Proporcional", null, null, null, null, null, null
        );

        when(expedienteRepository.findById(30L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(101L)).thenReturn(Optional.of(coordinador));

        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(request, 101L));
    }

    @Test
    @DisplayName("UC-24 RN-08 / E4: Bloqueo de suspensión presencial sin Plan de Continuidad Académica")
    void decidirSancion_SuspensionSinPlanContinuidad_LanzaConflictoNegocioException() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(30L);
        exp.setCasoConvivencia(caso);
        exp.setEstudiante(estudiante);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);

        MedidaSancionRequest request = new MedidaSancionRequest(
                30L, TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, true,
                "Suspensión de 3 días", "Proporcional", 3,
                LocalDate.now(), LocalDate.now().plusDays(3), null, null, null // Sin plan
        );

        when(expedienteRepository.findById(30L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(rector));

        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(request, 100L));
    }

    @Test
    @DisplayName("UC-24 A3 / E3: Bloqueo de expulsión si falta el Acta del Consejo Directivo")
    void decidirSancion_ExpulsionSinActaConsejoDirectivo_LanzaConflictoNegocioException() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(30L);
        exp.setCasoConvivencia(caso);
        exp.setEstudiante(estudiante);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);

        MedidaSancionRequest request = new MedidaSancionRequest(
                30L, TipoMedidaSancion.CANCELACION_MATRICULA_EXPULSION, true,
                "Cancelación de matrícula", "Máxima gravedad", null,
                null, null, null, null, null // Sin acta CD
        );

        when(expedienteRepository.findById(30L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(rector));

        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(request, 100L));
    }

    @Test
    @DisplayName("UC-24 RN-08 / A1: Suspensión con Plan de Continuidad Académica se registra exitosamente")
    void decidirSancion_SuspensionConPlanContinuidad_Exitoso() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(30L);
        exp.setCasoConvivencia(caso);
        exp.setEstudiante(estudiante);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);

        PlanContinuidadRequest planReq = new PlanContinuidadRequest(
                "Guías pedagógicas de matemáticas y ciencias",
                "Entrega sincrónica y evaluación cualitativa sin notas punitivas",
                null
        );

        MedidaSancionRequest request = new MedidaSancionRequest(
                30L, TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, true,
                "Suspensión temporal por 3 días hábiles", "Análisis de gradualidad y no reiteración",
                3, LocalDate.now(), LocalDate.now().plusDays(3), null, null, planReq
        );

        when(expedienteRepository.findById(30L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(rector));
        when(medidaRepository.save(any(MedidaSancionConvivencia.class))).thenAnswer(i -> {
            MedidaSancionConvivencia m = i.getArgument(0);
            m.setId(60L);
            return m;
        });
        when(planContinuidadRepository.save(any(PlanContinuidadAcademica.class))).thenAnswer(i -> {
            PlanContinuidadAcademica p = i.getArgument(0);
            p.setId(70L);
            return p;
        });

        MedidaSancionResponse response = medidaSancionService.decidirMedidaOSancion(request, 100L);

        assertNotNull(response);
        assertEquals(TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, response.tipoMedida());
        assertNotNull(response.planContinuidad());
        assertEquals("Guías pedagógicas de matemáticas y ciencias", response.planContinuidad().guiasTalleresDescripcion());
    }

    @Test
    @DisplayName("UC-PROV-25 E3: Segregación de instancias en apelación (misma autoridad rechazada)")
    void resolverRecurso_ApelacionMismaAutoridad_LanzaConflictoNegocioException() {
        MedidaSancionConvivencia medida = new MedidaSancionConvivencia();
        medida.setId(60L);
        medida.setAutoridadFirmante(rector); // Sancionada por rector

        RecursoImpugnacion recurso = new RecursoImpugnacion();
        recurso.setId(80L);
        recurso.setMedidaSancion(medida);
        recurso.setTipoRecurso(TipoRecursoImpugnacion.APELACION);

        ResolverRecursoRequest request = new ResolverRecursoRequest("CONFIRMAR_DECISION", "Motivo", "Acta CD");

        when(recursoRepository.findById(80L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(rector)); // El mismo rector intenta resolver

        assertThrows(ConflictoNegocioException.class, () -> debidoProcesoService.resolverRecurso(80L, request, 100L));
    }

    @Test
    @DisplayName("UC-PROV-26 RN-SEG-01: Registro de acta del Comité Escolar de Convivencia exitoso")
    void registrarActaComite_Exitoso() {
        ActaComiteConvivenciaRequest request = new ActaComiteConvivenciaRequest(
                1L, "ACT-COMITE-2026-001", LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(11, 0),
                "Sala de Consejo", TipoSesionComiteConvivencia.ORDINARIA,
                "[\"Rector: Dr. López\",\"Personero: Juan P.\",\"Orientadora: Lic. Clara\"]",
                "Sin impedimentos",
                "[\"EXP-CONV-2026-0001\"]",
                "[\"Compromisos formativos acordados\"]",
                "Talleres de prevención del ciberacoso",
                101L, 100L
        );

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(usuarioRepository.findById(101L)).thenReturn(Optional.of(coordinador));
        when(usuarioRepository.findById(100L)).thenReturn(Optional.of(rector));
        when(actaRepository.save(any(ActaComiteConvivencia.class))).thenAnswer(i -> {
            ActaComiteConvivencia a = i.getArgument(0);
            a.setId(90L);
            return a;
        });

        ActaComiteConvivenciaResponse response = comiteConvivenciaService.registrarActa(request);

        assertNotNull(response);
        assertEquals("ACT-COMITE-2026-001", response.numeroActa());
    }
}
