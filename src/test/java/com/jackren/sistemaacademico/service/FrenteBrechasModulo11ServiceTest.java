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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 11: Convivencia Escolar V2 al 100%")
class FrenteBrechasModulo11ServiceTest {

    @Mock private AnotacionObservadorRepository anotacionRepository;
    @Mock private AnotacionObservadorAdendaRepository adendaRepository;
    @Mock private CasoConvivenciaRepository casoRepository;
    @Mock private ActuacionRutaAtencionRepository actuacionRepository;
    @Mock private ExpedienteDebidoProcesoRepository expedienteRepository;
    @Mock private DescargoDebidoProcesoRepository descargoRepository;
    @Mock private MedidaSancionConvivenciaRepository medidaRepository;
    @Mock private PlanContinuidadAcademicaRepository planContinuidadRepository;
    @Mock private RecursoImpugnacionRepository recursoRepository;
    @Mock private ActaComiteConvivenciaRepository actaRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private PeriodoAcademicoRepository periodoAcademicoRepository;
    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ProfesorRepository profesorRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private AsignacionAcademicaRepository asignacionAcademicaRepository;
    @Mock private EstudianteAcudienteRepository estudianteAcudienteRepository;
    @Mock private AcudienteRepository acudienteRepository;
    @Mock private DiaNoLectivoRepository diaNoLectivoRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;

    private ObservadorService observadorService;
    private RutaAtencionService rutaAtencionService;
    private DebidoProcesoService debidoProcesoService;
    private MedidaSancionService medidaSancionService;
    private ComiteConvivenciaService comiteConvivenciaService;
    private ConvivenciaConsultaService consultaService;

    private Estudiante estudiante;
    private Usuario docente;
    private Usuario rector;
    private Usuario coordinadorConvivencia;
    private Usuario usuarioAcudiente;
    private Acudiente acudiente;
    private AnioLectivo anioLectivo;
    private PeriodoAcademico periodoAcademico;
    private CasoConvivencia caso;

    @BeforeEach
    void setUp() {
        rutaAtencionService = new RutaAtencionService(
                casoRepository, actuacionRepository, anioLectivoRepository,
                estudianteRepository, usuarioRepository, acudienteRepository, auditoriaGlobalService
        );

        observadorService = new ObservadorService(
                anotacionRepository, adendaRepository, estudianteRepository,
                periodoAcademicoRepository, usuarioRepository, matriculaRepository,
                asignacionAcademicaRepository, estudianteAcudienteRepository,
                acudienteRepository, rutaAtencionService, auditoriaGlobalService
        );

        debidoProcesoService = new DebidoProcesoService(
                expedienteRepository, descargoRepository, recursoRepository,
                medidaRepository, casoRepository, estudianteRepository,
                usuarioRepository, diaNoLectivoRepository, auditoriaGlobalService
        );

        medidaSancionService = new MedidaSancionService(
                medidaRepository, planContinuidadRepository, expedienteRepository,
                usuarioRepository, profesorRepository, matriculaRepository, auditoriaGlobalService
        );

        comiteConvivenciaService = new ComiteConvivenciaService(
                actaRepository, anioLectivoRepository, usuarioRepository, auditoriaGlobalService
        );

        consultaService = new ConvivenciaConsultaService(
                estudianteRepository, usuarioRepository, acudienteRepository,
                estudianteAcudienteRepository, asignacionAcademicaRepository,
                matriculaRepository, observadorService, rutaAtencionService,
                debidoProcesoService, medidaSancionService, planContinuidadRepository,
                casoRepository, expedienteRepository, medidaRepository, auditoriaGlobalService
        );

        estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Carlos");
        estudiante.setApellidos("Pérez");

        docente = new Usuario("docente1", "hash", Rol.PROFESOR);
        docente.setId(1L);

        rector = new Usuario("rector1", "hash", Rol.RECTOR);
        rector.setId(2L);

        coordinadorConvivencia = new Usuario("coord1", "hash", Rol.COORDINADOR_CONVIVENCIA);
        coordinadorConvivencia.setId(3L);

        acudiente = new Acudiente();
        acudiente.setId(20L);
        acudiente.setDocumento("52001002");

        usuarioAcudiente = new Usuario("52001002", "hash", Rol.ACUDIENTE);
        usuarioAcudiente.setId(200L);
        usuarioAcudiente.setPersonaId(20L);

        anioLectivo = new AnioLectivo();
        anioLectivo.setId(100L);
        anioLectivo.setAnio(2026);

        periodoAcademico = new PeriodoAcademico();
        periodoAcademico.setId(500L);
        periodoAcademico.setAnioLectivo(anioLectivo);

        caso = new CasoConvivencia();
        caso.setId(80L);
        caso.setCodigoExpediente("EXP-CONV-2026-0001");
        caso.setAnioLectivo(anioLectivo);
        caso.setEstudianteImplicado(estudiante);
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_I);
        caso.setEstadoCaso(EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION);
    }

    // =========================================================================
    // UC-22: OBSERVADOR DEL ESTUDIANTE
    // =========================================================================

    @Test
    @DisplayName("UC-22 Flujo Principal: Docente con asignación registra anotación en observador")
    void uc22_FlujoPrincipal_RegistroDocenteConAsignacion() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, 500L, TipoAnotacionObservador.POSITIVA_RECONOCIMIENTO,
                LocalDate.now(), LocalTime.now(), "Aula 101",
                "Felicitación por liderazgo positivo y cooperación en el proyecto transversal",
                "Reconocimiento en formación", false, false, null
        );

        Grupo grupo = new Grupo();
        grupo.setId(5L);
        Matricula mat = new Matricula();
        mat.setEstado(EstadoMatricula.MATRICULADA);
        mat.setGrupo(grupo);

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(List.of(mat));
        when(asignacionAcademicaRepository.existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(1L, 5L)).thenReturn(true);
        when(periodoAcademicoRepository.findById(500L)).thenReturn(Optional.of(periodoAcademico));
        when(anotacionRepository.save(any(AnotacionObservador.class))).thenAnswer(i -> {
            AnotacionObservador a = i.getArgument(0);
            a.setId(1L);
            return a;
        });

        AnotacionObservadorResponse resp = observadorService.registrarAnotacion(request, 1L);

        assertNotNull(resp);
        assertEquals(TipoAnotacionObservador.POSITIVA_RECONOCIMIENTO, resp.tipoAnotacion());
        assertTrue(resp.anotacionFirmada());
    }

    @Test
    @DisplayName("UC-22 A1: Derivación automática hacia Ruta de Atención Integral")
    void uc22_A1_DerivacionHaciaRutaAtencion() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, 500L, TipoAnotacionObservador.REPORTE_SITUACION_CONVIVENCIA,
                LocalDate.now(), LocalTime.now(), "Patio",
                "Se evidencia agresión verbal reiterada y hostigamiento entre compañeros",
                "Llamado a diálogo", true, true, TipoSituacionConvivencia.TIPO_II
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(periodoAcademicoRepository.findById(500L)).thenReturn(Optional.of(periodoAcademico));
        when(anioLectivoRepository.findById(100L)).thenReturn(Optional.of(anioLectivo));
        when(casoRepository.save(any(CasoConvivencia.class))).thenAnswer(i -> {
            CasoConvivencia c = i.getArgument(0);
            c.setId(99L);
            return c;
        });
        when(anotacionRepository.save(any(AnotacionObservador.class))).thenAnswer(i -> {
            AnotacionObservador a = i.getArgument(0);
            a.setId(2L);
            return a;
        });

        AnotacionObservadorResponse resp = observadorService.registrarAnotacion(request, 1L);

        assertNotNull(resp);
        assertTrue(resp.requiereActivacionRuta());
        assertNotNull(resp.casoConvivenciaId());
    }

    @Test
    @DisplayName("UC-22 A2: Garantía bilateral con versión libre del estudiante y enterado del acudiente")
    void uc22_A2_VersionLibreYEnteradoAcudiente() {
        AnotacionObservador anotacion = new AnotacionObservador();
        anotacion.setId(1L);
        anotacion.setEstudiante(estudiante);
        anotacion.setAutorUsuario(docente);

        when(anotacionRepository.findById(1L)).thenReturn(Optional.of(anotacion));
        when(anotacionRepository.save(any(AnotacionObservador.class))).thenAnswer(i -> i.getArgument(0));

        // 1. Versión del menor
        VersionEstudianteRequest vReq = new VersionEstudianteRequest("Acepto el diálogo y me comprometo a respetar a mis pares");
        AnotacionObservadorResponse respV = observadorService.registrarVersionEstudiante(1L, vReq, 10L);
        assertNotNull(respV.versionLibreEstudiante());

        // 2. Enterado acudiente
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioAcudiente));
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(vinculo));

        AnotacionObservadorResponse respA = observadorService.registrarEnteradoAcudiente(1L, 200L);
        assertTrue(respA.enteradoAcudiente());
    }

    @Test
    @DisplayName("UC-22 A3 / E4: Inmutabilidad estricta (edición in-situ rechazada y adenda permitida)")
    void uc22_A3_E4_InmutabilidadYAdendas() {
        // E4: Bloqueo in-situ
        assertThrows(ConflictoNegocioException.class, () -> observadorService.bloquearEdicionOBorradoDirecto(1L));

        // A3: Adenda vinculada
        AnotacionObservador anotacion = new AnotacionObservador();
        anotacion.setId(1L);
        anotacion.setEstudiante(estudiante);
        anotacion.setAutorUsuario(docente);

        when(anotacionRepository.findById(1L)).thenReturn(Optional.of(anotacion));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordinadorConvivencia));
        when(adendaRepository.save(any(AnotacionObservadorAdenda.class))).thenAnswer(i -> {
            AnotacionObservadorAdenda a = i.getArgument(0);
            a.setId(10L);
            return a;
        });

        AdendaObservadorRequest adendaReq = new AdendaObservadorRequest("Precisión horaria", "Ocurrió al finalizar la 3ra hora");
        AdendaObservadorResponse adendaResp = observadorService.agregarAdenda(1L, adendaReq, 3L);

        assertNotNull(adendaResp);
        assertEquals("Precisión horaria", adendaResp.motivoAdenda());
    }

    @Test
    @DisplayName("UC-22 E1: Descripción insuficiente (< 10 caracteres) rechazada con 400 Bad Request")
    void uc22_E1_DescripcionInsuficiente() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, 500L, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Aula",
                "Mal corte", null, true, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));

        assertThrows(ValidacionException.class, () -> observadorService.registrarAnotacion(request, 1L));
    }

    @Test
    @DisplayName("UC-22 E2: Docente sin asignación rechazado con 403 Forbidden")
    void uc22_E2_DocenteSinAsignacion() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, 500L, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Aula 101",
                "Descripción objetiva y detallada de los hechos observados",
                null, false, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(Collections.emptyList());

        assertThrows(AccessDeniedException.class, () -> observadorService.registrarAnotacion(request, 1L));
    }

    @Test
    @DisplayName("UC-22 E3: Inclusión de diagnósticos clínicos sensibles en observador general rechazada")
    void uc22_E3_DatosClinicosSensiblesRechazados() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, 500L, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Enfermería",
                "El estudiante adjunta diagnóstico psiquiátrico reservado en el formato",
                null, true, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));

        assertThrows(ValidacionException.class, () -> observadorService.registrarAnotacion(request, 1L));
    }

    // =========================================================================
    // UC-23: RUTA DE ATENCIÓN INTEGRAL (LEY 1620 DE 2013)
    // =========================================================================

    @Test
    @DisplayName("UC-23 A1 / RN-CON-05: Apertura preliminar urgente por atención médica prioritaria")
    void uc23_A1_AtencionMedicaUrgente() {
        CasoConvivenciaRequest request = new CasoConvivenciaRequest(
                100L, 10L, TipoSituacionConvivencia.TIPO_II,
                LocalDate.now(), "Laboratorio",
                "Accidente o lesión que amerita atención médica de urgencias",
                false, true, "Traslado en ambulancia a centro asistencial",
                false, null, false, null, null, null
        );

        when(anioLectivoRepository.findById(100L)).thenReturn(Optional.of(anioLectivo));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordinadorConvivencia));
        when(casoRepository.save(any(CasoConvivencia.class))).thenAnswer(i -> {
            CasoConvivencia c = i.getArgument(0);
            c.setId(85L);
            return c;
        });

        CasoConvivenciaResponse resp = rutaAtencionService.abrirCaso(request, 3L);

        assertNotNull(resp);
        assertEquals(EstadoCasoConvivencia.ABIERTO_PRELIMINAR_URGENTE, resp.estadoCaso());
        assertTrue(resp.atencionMedicaUrgente());
    }

    @Test
    @DisplayName("UC-23 A2: Reclasificación motivada de Tipo I a Tipo II por reiteración o acoso")
    void uc23_A2_ReclasificacionMotivada() {
        when(casoRepository.findById(80L)).thenReturn(Optional.of(caso));
        when(casoRepository.save(any(CasoConvivencia.class))).thenAnswer(i -> i.getArgument(0));

        ReclasificarCasoRequest req = new ReclasificarCasoRequest(
                TipoSituacionConvivencia.TIPO_II,
                "Se comprueba reiteración sistemática de burlas configurando acoso escolar (bullying)"
        );

        CasoConvivenciaResponse resp = rutaAtencionService.reclasificarCaso(80L, req, 3L);

        assertNotNull(resp);
        assertEquals(TipoSituacionConvivencia.TIPO_II, resp.tipoSituacion());
    }

    @Test
    @DisplayName("UC-23 A4 / RN-CON-06: Activación de reserva contra acudiente agresor")
    void uc23_A4_ReservaProteccionContraAcudienteAgresor() {
        CasoConvivenciaRequest request = new CasoConvivenciaRequest(
                100L, 10L, TipoSituacionConvivencia.TIPO_II,
                LocalDate.now(), "Hogar",
                "Presunta vulneración intrafamiliar",
                false, false, null,
                true, 20L, true, "ICBF Comisaría", LocalDate.now(), "RAD-ICBF-001"
        );

        when(anioLectivoRepository.findById(100L)).thenReturn(Optional.of(anioLectivo));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordinadorConvivencia));
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(casoRepository.save(any(CasoConvivencia.class))).thenAnswer(i -> {
            CasoConvivencia c = i.getArgument(0);
            c.setId(88L);
            return c;
        });

        CasoConvivenciaResponse resp = rutaAtencionService.abrirCaso(request, 3L);

        assertNotNull(resp);
        assertTrue(resp.reservaProteccionContraAcudiente());
        assertEquals(20L, resp.acudienteAgresorId());
    }

    @Test
    @DisplayName("UC-23 E3: Prohibición legal absoluta de conciliación o careo en situaciones Tipo III")
    void uc23_E3_ProhibicionConciliacionTipoIII() {
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_III);
        when(casoRepository.findById(80L)).thenReturn(Optional.of(caso));

        ActuacionRutaAtencionRequest req = new ActuacionRutaAtencionRequest(
                TipoActuacionRutaAtencion.MEDIACION_CONCILIACION,
                "Se propone encuentro entre las partes", null, null
        );

        assertThrows(ConflictoNegocioException.class, () -> rutaAtencionService.registrarActuacion(80L, req, 3L));
    }

    @Test
    @DisplayName("UC-23 E1 / E4: Bloqueo de cierre de caso Tipo II o III sin SIUCE ni actuaciones")
    void uc23_E1_E4_BloqueoCierreSinSiuceNiActuaciones() {
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_II);
        when(casoRepository.findById(80L)).thenReturn(Optional.of(caso));

        // Sin SIUCE
        assertThrows(ConflictoNegocioException.class, () -> rutaAtencionService.cerrarCaso(80L, "Cerrar", null, 3L));

        // Con SIUCE pero sin actuaciones registradas (E4)
        when(actuacionRepository.findByCasoConvivenciaIdOrderByFechaActuacionAsc(80L)).thenReturn(Collections.emptyList());
        assertThrows(ConflictoNegocioException.class, () -> rutaAtencionService.cerrarCaso(80L, "Cerrar", "SIUCE-12345", 3L));
    }

    // =========================================================================
    // UC-PROV-25: DEBIDO PROCESO Y RECURSOS
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-25 Flujo Principal / A2: Apertura de pliego y descargos en días hábiles")
    void ucProv25_AperturaYDescargos() {
        AperturaCargosRequest reqApertura = new AperturaCargosRequest(
                80L, 10L,
                "Hechos ocurridos el 10 de mayo de 2026 en el laboratorio de química",
                "Artículo 35 numeral 2 del Manual de Convivencia vigente",
                "Testimonio 1 y soporte fotográfico anonimizado",
                5, true, "Acompañamiento de Orientación Escolar"
        );

        when(casoRepository.findById(80L)).thenReturn(Optional.of(caso));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordinadorConvivencia));
        when(diaNoLectivoRepository.esDiaNoLectivo(eq(100L), any(LocalDate.class))).thenReturn(false);
        when(expedienteRepository.save(any(ExpedienteDebidoProceso.class))).thenAnswer(i -> {
            ExpedienteDebidoProceso e = i.getArgument(0);
            e.setId(50L);
            return e;
        });

        ExpedienteDebidoProcesoResponse expResp = debidoProcesoService.abrirExpedienteYNotificarCargos(reqApertura, 3L);

        assertNotNull(expResp);
        assertEquals(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS, expResp.estadoExpediente());

        // Radicación de descargos (A1: acompañamiento Personero)
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(50L);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS);

        when(expedienteRepository.findById(50L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(new Usuario("estudiante1", "hash", Rol.ESTUDIANTE)));
        when(descargoRepository.save(any(DescargoDebidoProceso.class))).thenAnswer(i -> {
            DescargoDebidoProceso d = i.getArgument(0);
            d.setId(60L);
            return d;
        });

        RadicarDescargosRequest descReq = new RadicarDescargosRequest(
                "Presento descargos explicando las circunstancias de modo y tiempo con acompañamiento del Personero",
                "Pruebas anexas", false, true
        );

        DescargoDebidoProcesoResponse descResp = debidoProcesoService.radicarDescargos(50L, descReq, 10L);

        assertNotNull(descResp);
        assertEquals(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION, exp.getEstadoExpediente());
    }

    @Test
    @DisplayName("UC-PROV-25 E3: Segregación estricta de instancias en resolución de apelación")
    void ucProv25_E3_SegregacionInstanciasApelacion() {
        MedidaSancionConvivencia medida = new MedidaSancionConvivencia();
        medida.setId(10L);
        medida.setAutoridadFirmante(rector); // Sancionada por rector en 1ra instancia

        RecursoImpugnacion recurso = new RecursoImpugnacion();
        recurso.setId(20L);
        recurso.setMedidaSancion(medida);
        recurso.setTipoRecurso(TipoRecursoImpugnacion.APELACION);

        when(recursoRepository.findById(20L)).thenReturn(Optional.of(recurso));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(rector)); // El mismo rector intenta resolver

        ResolverRecursoRequest req = new ResolverRecursoRequest("CONFIRMAR_DECISION", "Motivo", null);

        assertThrows(ConflictoNegocioException.class, () -> debidoProcesoService.resolverRecurso(20L, req, 2L));
    }

    // =========================================================================
    // UC-24: MEDIDAS Y SANCIONES CON PLAN DE CONTINUIDAD ACADÉMICA
    // =========================================================================

    @Test
    @DisplayName("UC-24 RN-07 / E1: Bloqueo de fallo sancionatorio si falta agotar el debido proceso")
    void uc24_E1_BloqueoSinDebidoProceso() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(50L);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.CARGOS_NOTIFICADOS_EN_TERMINO_DESCARGOS); // Término aún corriendo

        when(expedienteRepository.findById(50L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(rector));

        MedidaSancionRequest request = new MedidaSancionRequest(
                50L, TipoMedidaSancion.AMONESTACION_ESCRITA, false,
                "Falta demostrada", "Proporcional", null, null, null, null, null, null
        );

        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(request, 2L));
    }

    @Test
    @DisplayName("UC-24 RN-08 / E4 / A1: Plan de Continuidad Académica obligatorio en suspensiones presenciales")
    void uc24_RN08_PlanContinuidadObligatorio() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(50L);
        exp.setCasoConvivencia(caso);
        exp.setEstudiante(estudiante);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);

        when(expedienteRepository.findById(50L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(rector));

        // 1. E4: Intento de suspensión sin plan de continuidad
        MedidaSancionRequest reqSinPlan = new MedidaSancionRequest(
                50L, TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, true,
                "Suspensión de 3 días", "Proporcional", 3,
                LocalDate.now(), LocalDate.now().plusDays(3), null, null, null
        );
        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(reqSinPlan, 2L));

        // 2. A1: Suspensión con Plan de Continuidad Académica exitosa
        PlanContinuidadRequest planReq = new PlanContinuidadRequest(
                "Guías pedagógicas de profundización académica",
                "Entrega remota y retroalimentación sin notas punitivas (Sentencia T-004 de 2026)",
                null
        );
        MedidaSancionRequest reqConPlan = new MedidaSancionRequest(
                50L, TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, true,
                "Suspensión de 3 días", "Proporcional", 3,
                LocalDate.now(), LocalDate.now().plusDays(3), null, null, planReq
        );

        when(medidaRepository.save(any(MedidaSancionConvivencia.class))).thenAnswer(i -> {
            MedidaSancionConvivencia m = i.getArgument(0);
            m.setId(10L);
            return m;
        });
        when(planContinuidadRepository.save(any(PlanContinuidadAcademica.class))).thenAnswer(i -> {
            PlanContinuidadAcademica p = i.getArgument(0);
            p.setId(100L);
            return p;
        });

        MedidaSancionResponse resp = medidaSancionService.decidirMedidaOSancion(reqConPlan, 2L);

        assertNotNull(resp);
        assertEquals(TipoMedidaSancion.SUSPENSION_TEMPORAL_ASISTENCIA, resp.tipoMedida());
        assertNotNull(resp.planContinuidad());
    }

    @Test
    @DisplayName("UC-24 A3 / E3: Expulsión exige Acta del Consejo Directivo y Resolución Rectoral")
    void uc24_A3_ExpulsionExigeActaConsejoDirectivo() {
        ExpedienteDebidoProceso exp = new ExpedienteDebidoProceso();
        exp.setId(50L);
        exp.setCasoConvivencia(caso);
        exp.setEstudiante(estudiante);
        exp.setEstadoExpediente(EstadoExpedienteDebidoProceso.DESCARGOS_RECIBIDOS_LISTO_PARA_DECISION);

        when(expedienteRepository.findById(50L)).thenReturn(Optional.of(exp));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(rector));

        // E3: Falta acta Consejo Directivo
        MedidaSancionRequest reqSinCD = new MedidaSancionRequest(
                50L, TipoMedidaSancion.CANCELACION_MATRICULA_EXPULSION, true,
                "Cancelación de matrícula", "Máxima gravedad", null, null, null,
                null, null, null
        );
        assertThrows(ConflictoNegocioException.class, () -> medidaSancionService.decidirMedidaOSancion(reqSinCD, 2L));

        // A3: Con acta de Consejo Directivo
        MedidaSancionRequest reqConCD = new MedidaSancionRequest(
                50L, TipoMedidaSancion.CANCELACION_MATRICULA_EXPULSION, true,
                "Cancelación de matrícula aprobada en Consejo Directivo", "Máxima gravedad", null, null, null,
                "ACTA-CD-2026-05", LocalDate.now(), null
        );
        when(medidaRepository.save(any(MedidaSancionConvivencia.class))).thenAnswer(i -> {
            MedidaSancionConvivencia m = i.getArgument(0);
            m.setId(11L);
            return m;
        });

        MedidaSancionResponse resp = medidaSancionService.decidirMedidaOSancion(reqConCD, 2L);
        assertNotNull(resp);
        assertEquals("ACTA-CD-2026-05", resp.actaConsejoDirectivo());
    }

    // =========================================================================
    // UC-PROV-26: COMITÉ ESCOLAR DE CONVIVENCIA
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-26 RN-SEG-01 / A2: Acta del Comité con quórum nominativo e impedimentos")
    void ucProv26_ActaComiteConvivencia() {
        ActaComiteConvivenciaRequest request = new ActaComiteConvivenciaRequest(
                100L, "ACT-COMITE-2026-01", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(10, 0), "Sala de Consejo",
                TipoSesionComiteConvivencia.ORDINARIA,
                "[\"Rector\", \"Orientador\", \"Personero\"]",
                "[\"Docente líder se declara impedido en caso EXP-1 por parentesco\"]",
                "[\"EXP-CONV-2026-0001\"]",
                "[\"Compromisos formativos acordados\"]",
                "Campañas de prevención del acoso escolar",
                3L, 2L
        );

        when(anioLectivoRepository.findById(100L)).thenReturn(Optional.of(anioLectivo));
        when(usuarioRepository.findById(3L)).thenReturn(Optional.of(coordinadorConvivencia));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(rector));
        when(actaRepository.save(any(ActaComiteConvivencia.class))).thenAnswer(i -> {
            ActaComiteConvivencia a = i.getArgument(0);
            a.setId(99L);
            return a;
        });

        ActaComiteConvivenciaResponse resp = comiteConvivenciaService.registrarActa(request);

        assertNotNull(resp);
        assertEquals("ACT-COMITE-2026-01", resp.numeroActa());
        assertNotNull(resp.impedimentosDeclaradosJson());
    }
}
