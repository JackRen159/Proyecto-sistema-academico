package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.CierrePromocionMapper;
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
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 10: Cierre Académico y Promoción V2 al 100%")
class FrenteBrechasModulo10ServiceTest {

    @Mock
    private PeriodoAcademicoRepository periodoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private ActividadEvaluativaRepository actividadRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private ReclamacionCalificacionRepository reclamacionRepository;

    @Mock
    private SolicitudExcusaAsistenciaRepository excusaRepository;

    @Mock
    private InstitucionDatosLegalesRepository institucionRepository;

    @Mock
    private BoletinPublicadoVersionRepository boletinRepository;

    @Mock
    private CalificacionPeriodoRepository calificacionPeriodoRepository;

    @Mock
    private CalificacionAreaPeriodoRepository calificacionAreaPeriodoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaGlobalService auditoriaGlobalService;

    @Mock
    private ObservacionBoletinGrupoRepository observacionRepository;

    @Mock
    private ActaComisionEvaluacionPeriodicaRepository actaComisionRepository;

    @Mock
    private GradoRepository gradoRepository;

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private ActaPromocionAnualRepository actaPromocionRepository;

    @Mock
    private PromocionEstudianteAnualRepository promocionEstudianteRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private TitulacionGraduandoMediaRepository titulacionRepository;

    private CierrePromocionMapper cierrePromocionMapper;
    private ValidadorPreCierreService validadorPreCierreService;
    private CierrePeriodoService cierrePeriodoService;
    private ComisionPeriodicaService comisionPeriodicaService;
    private PromocionAnualService promocionAnualService;
    private GraduacionMediaService graduacionMediaService;

    private AnioLectivo anio;
    private PeriodoAcademico periodo;
    private Grado gradoNoveno;
    private Grado gradoOnce;
    private Grado gradoTransicion;
    private Grupo grupo;
    private Profesor profesor;
    private Estudiante estudiante;
    private Matricula matricula;

    @BeforeEach
    void setUp() {
        cierrePromocionMapper = new CierrePromocionMapper();

        validadorPreCierreService = new ValidadorPreCierreService(
                periodoRepository, grupoRepository, asignacionRepository,
                actividadRepository, matriculaRepository, reclamacionRepository,
                excusaRepository, institucionRepository
        );

        cierrePeriodoService = new CierrePeriodoService(
                periodoRepository, grupoRepository, matriculaRepository,
                boletinRepository, calificacionPeriodoRepository,
                calificacionAreaPeriodoRepository, usuarioRepository,
                validadorPreCierreService, auditoriaGlobalService,
                cierrePromocionMapper
        );

        comisionPeriodicaService = new ComisionPeriodicaService(
                observacionRepository, actaComisionRepository, matriculaRepository,
                periodoRepository, gradoRepository, profesorRepository,
                asignacionRepository, usuarioRepository, cierrePromocionMapper
        );

        promocionAnualService = new PromocionAnualService(
                actaPromocionRepository, promocionEstudianteRepository, grupoRepository,
                gradoRepository, anioLectivoRepository, matriculaRepository,
                periodoRepository, calificacionAreaPeriodoRepository,
                calificacionPeriodoRepository, usuarioRepository, auditoriaGlobalService,
                cierrePromocionMapper
        );

        graduacionMediaService = new GraduacionMediaService(
                titulacionRepository, matriculaRepository, promocionEstudianteRepository,
                anioLectivoRepository, periodoRepository, usuarioRepository,
                auditoriaGlobalService, cierrePromocionMapper
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

        gradoNoveno = new Grado();
        gradoNoveno.setId(9L);
        gradoNoveno.setNombre("Noveno");
        gradoNoveno.setNivel(NivelEducativo.BASICA_SECUNDARIA);

        Grado gradoDecimo = new Grado();
        gradoDecimo.setId(10L);
        gradoDecimo.setNombre("Décimo");
        gradoNoveno.setGradoSiguiente(gradoDecimo);

        gradoOnce = new Grado();
        gradoOnce.setId(11L);
        gradoOnce.setNombre("Once");
        gradoOnce.setNivel(NivelEducativo.MEDIA);

        gradoTransicion = new Grado();
        gradoTransicion.setId(0L);
        gradoTransicion.setNombre("Transición");
        gradoTransicion.setNivel(NivelEducativo.PREESCOLAR);

        Grado gradoPrimero = new Grado();
        gradoPrimero.setId(1L);
        gradoPrimero.setNombre("Primero");
        gradoTransicion.setGradoSiguiente(gradoPrimero);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("9-A");
        grupo.setGrado(gradoNoveno);
        grupo.setAnioLectivo(anio);

        profesor = new Profesor();
        profesor.setId(30L);
        profesor.setNombres("Gabriel");
        profesor.setApellidos("García Márquez");
        profesor.setActivo(true);

        estudiante = new Estudiante();
        estudiante.setId(70L);
        estudiante.setNombres("Aureliano");
        estudiante.setApellidos("Buendía");
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
    // UC-20: CIERRE Y REAPERTURA DE PERIODO ACADÉMICO (RN-CIE-01, RN-CIE-04)
    // =========================================================================

    @Test
    @DisplayName("UC-20 E1 / RN-CIE-01: Cierre bloqueado si existen reclamaciones abiertas o notas faltantes")
    void testUC20_E1_PreCierreDetectaBloqueos_RechazaCierre() {
        InstitucionDatosLegales inst = new InstitucionDatosLegales();
        inst.setCodigoDane("105001000001");

        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(institucionRepository.findFirstByActivoTrue()).thenReturn(Optional.of(inst));
        when(reclamacionRepository.countByEstadoIn(any())).thenReturn(2L); // 2 reclamos abiertos

        CierrePeriodoRequest req = new CierrePeriodoRequest();
        req.setPeriodoId(10L);

        assertThrows(ConflictoNegocioException.class, () ->
                cierrePeriodoService.cerrarPeriodo(req, 1L));
    }

    @Test
    @DisplayName("UC-20: Cierre exitoso generando snapshots inmutables de boletines versionados")
    void testUC20_CierrePeriodo_Exitoso() {
        InstitucionDatosLegales inst = new InstitucionDatosLegales();
        inst.setCodigoDane("105001000001");

        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(institucionRepository.findFirstByActivoTrue()).thenReturn(Optional.of(inst));
        when(reclamacionRepository.countByEstadoIn(any())).thenReturn(0L);
        when(excusaRepository.countByEstado(any())).thenReturn(0L);
        when(grupoRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(grupo));
        when(matriculaRepository.findByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));
        when(asignacionRepository.findByGrupoIdAndActivoTrue(20L)).thenReturn(List.of());
        when(boletinRepository.countByMatriculaIdAndPeriodoAcademicoId(100L, 10L)).thenReturn(0L);
        when(boletinRepository.save(any(BoletinPublicadoVersion.class))).thenAnswer(i -> {
            BoletinPublicadoVersion b = i.getArgument(0);
            b.setId(1L);
            return b;
        });

        CierrePeriodoRequest req = new CierrePeriodoRequest();
        req.setPeriodoId(10L);

        List<BoletinPublicadoResponse> boletines = cierrePeriodoService.cerrarPeriodo(req, 1L);

        assertNotNull(boletines);
        assertEquals(1, boletines.size());
        assertEquals(1, boletines.get(0).getNumeroVersion());
        assertEquals(EstadoBoletinVersion.PUBLICADO_OFICIAL, boletines.get(0).getEstadoBoletin());
        assertEquals(EstadoPeriodo.CERRADO, periodo.getEstado());
    }

    @Test
    @DisplayName("UC-20 A3 / RN-CIE-04: Reapertura excepcional rectoral pasa boletines a RECTIFICADO_HISTORICO")
    void testUC20_A3_ReaperturaExcepcionalRectoral() {
        periodo.setEstado(EstadoPeriodo.CERRADO);

        BoletinPublicadoVersion b1 = new BoletinPublicadoVersion();
        b1.setId(10L);
        b1.setEstadoBoletin(EstadoBoletinVersion.PUBLICADO_OFICIAL);

        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(grupoRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(grupo));
        when(matriculaRepository.findByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));
        when(boletinRepository.findFirstByMatriculaIdAndPeriodoAcademicoIdAndEstadoBoletinOrderByNumeroVersionDesc(100L, 10L, EstadoBoletinVersion.PUBLICADO_OFICIAL))
                .thenReturn(Optional.of(b1));

        ReaperturaPeriodoRequest req = new ReaperturaPeriodoRequest();
        req.setPeriodoId(10L);
        req.setMotivoReapertura("Corrección por fallo favorable de reclamación REC-001");

        cierrePeriodoService.reabrirPeriodo(req, 1L);

        assertEquals(EstadoPeriodo.ABIERTO, periodo.getEstado());
        assertEquals(EstadoBoletinVersion.RECTIFICADO_HISTORICO, b1.getEstadoBoletin());
        assertNotNull(b1.getFechaReapertura());
    }

    // =========================================================================
    // UC-21: EVALUAR PROMOCIÓN ANUAL, PREESCOLAR Y HABILITACIONES
    // =========================================================================

    @Test
    @DisplayName("UC-21 A4 / RN-CIE-05: Invariante legal de Preescolar - Promoción obligatoria sin reprobación")
    void testUC21_A4_Preescolar_PromocionAutomaticaObligatoria() {
        Grupo grupoTrans = new Grupo();
        grupoTrans.setId(15L);
        grupoTrans.setGrado(gradoTransicion);
        grupoTrans.setAnioLectivo(anio);

        Matricula matTrans = new Matricula();
        matTrans.setId(105L);
        matTrans.setNumeroMatricula("MAT-PRE-001");
        matTrans.setEstudiante(estudiante);
        matTrans.setGrupo(grupoTrans);

        when(grupoRepository.findById(15L)).thenReturn(Optional.of(grupoTrans));
        when(matriculaRepository.findByGrupoIdAndEstado(15L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matTrans));

        List<PreclasificacionPromocionResponse> resps = promocionAnualService.preclasificarPromocionGrupo(15L);

        assertNotNull(resps);
        assertEquals(1, resps.size());
        assertEquals(EstadoPromocionEstudiante.PROMOVIDO_PREESCOLAR, resps.get(0).getClasificacionSugerida());
        assertEquals(0, resps.get(0).getAreasReprobadas());
        assertEquals(1L, resps.get(0).getGradoDestinoSugeridoId()); // Grado Primero
    }

    @Test
    @DisplayName("UC-21 E4: Intento de reprobar a un alumno de Preescolar debe ser bloqueado con ConflictoNegocioException")
    void testUC21_E4_IntentoReprobarPreescolar_DebeLanzarConflicto() {
        periodo.setEstado(EstadoPeriodo.CERRADO);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(gradoRepository.findById(0L)).thenReturn(Optional.of(gradoTransicion));
        when(periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo));

        DecisionPromocionEstudianteItemRequest decisionInvalida = new DecisionPromocionEstudianteItemRequest();
        decisionInvalida.setMatriculaId(100L);
        decisionInvalida.setEstadoPromocion(EstadoPromocionEstudiante.NO_PROMOVIDO_REPROBADO); // Prohibido en Preescolar
        decisionInvalida.setPromedioGeneral(new BigDecimal("2.50"));

        ActaPromocionAnualRequest req = new ActaPromocionAnualRequest();
        req.setAnioLectivoId(1L);
        req.setGradoId(0L);
        req.setNumeroActa("ACT-PROM-PRE-01");
        req.setIntegrantesNominativos(List.of("Rector", "Coordinador"));
        req.setDecisionesEstudiantes(List.of(decisionInvalida));

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));

        assertThrows(ConflictoNegocioException.class, () ->
                promocionAnualService.registrarActaPromocionAnual(req, 1L));
    }

    @Test
    @DisplayName("UC-21 A1 / BRE-020: Estudiante con 1 o 2 áreas pendientes pasa a PENDIENTE_RECUPERACION_FINAL")
    void testUC21_A1_HabilitacionFinalAnual_Preclasificacion() {
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(matriculaRepository.findByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));

        CalificacionPeriodo cp1 = new CalificacionPeriodo();
        cp1.setAprobada(false);
        cp1.setNotaDefinitiva(new BigDecimal("2.50"));

        CalificacionPeriodo cp2 = new CalificacionPeriodo();
        cp2.setAprobada(true);
        cp2.setNotaDefinitiva(new BigDecimal("4.00"));

        when(calificacionPeriodoRepository.findByMatriculaId(100L)).thenReturn(List.of(cp1, cp2));

        List<PreclasificacionPromocionResponse> resps = promocionAnualService.preclasificarPromocionGrupo(20L);

        assertNotNull(resps);
        assertEquals(EstadoPromocionEstudiante.PENDIENTE_RECUPERACION_FINAL, resps.get(0).getClasificacionSugerida());
        assertEquals(1, resps.get(0).getAreasReprobadas());
    }

    @Test
    @DisplayName("UC-21 A3 / RN-CIE-08: Trámite exitoso de Promoción Anticipada de Grado (Decreto 1290 Art. 7)")
    void testUC21_A3_PromocionAnticipada_Exitoso() {
        PromocionAnticipadaRequest req = new PromocionAnticipadaRequest();
        req.setMatriculaId(100L);
        req.setActaConsejoAcademico("ACT-ACAD-2026-002");
        req.setActaConsejoDirectivo("ACT-DIR-2026-004");
        req.setConsentimientoAcudienteUrl("https://colegio.edu.co/cons.pdf");
        req.setGradoDestinoId(10L); // Ascenso a Décimo
        req.setMotivo("Estudiante con desempeño superior integral y madurez acreditada");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(gradoRepository.findById(10L)).thenReturn(Optional.of(new Grado()));
        when(actaPromocionRepository.count()).thenReturn(0L);
        when(actaPromocionRepository.save(any(ActaPromocionAnual.class))).thenAnswer(i -> {
            ActaPromocionAnual a = i.getArgument(0);
            a.setId(1L);
            return a;
        });
        when(promocionEstudianteRepository.findByMatriculaId(100L)).thenReturn(Optional.empty());
        when(promocionEstudianteRepository.save(any(PromocionEstudianteAnual.class))).thenAnswer(i -> i.getArgument(0));

        PromocionEstudianteResponse resp = promocionAnualService.tramitarPromocionAnticipada(req, 1L);

        assertNotNull(resp);
        assertEquals(EstadoPromocionEstudiante.PROMOCION_ANTICIPADA_APROBADA, resp.getEstadoPromocion());
    }

    // =========================================================================
    // UC-PROV-23: COMISIONES PERIÓDICAS Y OBSERVACIONES DE GRUPO
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-23 E1 / RN-09: Docente no director del grupo no puede registrar observaciones de boletín")
    void testUCPROV23_E1_NoDirectorGrupo_DebeLanzarAccessDenied() {
        ObservacionBoletinGrupoRequest req = new ObservacionBoletinGrupoRequest();
        req.setMatriculaId(100L);
        req.setPeriodoAcademicoId(10L);
        req.setObservacionFormativa("Observación formativa");

        Profesor docenteNoDirector = new Profesor();
        docenteNoDirector.setId(99L);

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(profesorRepository.findByUsuarioId(150L)).thenReturn(Optional.of(docenteNoDirector));
        when(asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(20L)).thenReturn(Optional.empty());

        assertThrows(AccessDeniedException.class, () ->
                comisionPeriodicaService.registrarObservacionGrupo(req, 150L, "PROFESOR"));
    }

    @Test
    @DisplayName("UC-PROV-23: Director de grupo registra exitosamente recomendaciones pedagógicas del boletín")
    void testUCPROV23_DirectorGrupo_RegistraObservacionExitosa() {
        ObservacionBoletinGrupoRequest req = new ObservacionBoletinGrupoRequest();
        req.setMatriculaId(100L);
        req.setPeriodoAcademicoId(10L);
        req.setObservacionFormativa("Excelente nivel de autonomía y trabajo cooperativo");
        req.setRecomendacionesPedagogicas("Continuar profundizando en experimentos");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));
        when(observacionRepository.findByMatriculaIdAndPeriodoAcademicoId(100L, 10L)).thenReturn(Optional.empty());
        when(observacionRepository.save(any(ObservacionBoletinGrupo.class))).thenAnswer(i -> {
            ObservacionBoletinGrupo o = i.getArgument(0);
            o.setId(1L);
            return o;
        });

        ObservacionBoletinGrupoResponse resp = comisionPeriodicaService.registrarObservacionGrupo(req, 1L, "COORDINADOR");

        assertNotNull(resp);
        assertEquals("Excelente nivel de autonomía y trabajo cooperativo", resp.getObservacionFormativa());
        assertEquals("Continuar profundizando en experimentos", resp.getRecomendacionesPedagogicas());
    }

    // =========================================================================
    // UC-PROV-24: GRADUACIÓN MEDIA Y CIERRE ANUAL (RN-CIE-09, BRE-021)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-24 A1 / RN-CIE-09: Aprobó materias pero adeuda servicio social queda APROBADO_PENDIENTE_TITULACION")
    void testUCPROV24_A1_AdeudaServicioSocial_QuedaPendienteTitulacion() {
        TitulacionMediaRequest req = new TitulacionMediaRequest();
        req.setMatriculaId(100L);
        req.setHorasServicioSocial(40); // Menor al mínimo legal de 80 horas
        req.setCodigoRegistroIcfes("AC202611112222");
        req.setDocumentoIdentidadVerificado(true);

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(titulacionRepository.findByMatriculaId(100L)).thenReturn(Optional.empty());
        when(titulacionRepository.save(any(TitulacionGraduandoMedia.class))).thenAnswer(i -> {
            TitulacionGraduandoMedia t = i.getArgument(0);
            t.setId(1L);
            return t;
        });

        TitulacionGraduandoResponse resp = graduacionMediaService.registrarRequisitosTitulacion(req, 1L);

        assertNotNull(resp);
        assertEquals(EstadoTitulacionGraduando.APROBADO_PENDIENTE_TITULACION, resp.getEstadoTitulacion());
    }

    @Test
    @DisplayName("UC-PROV-24: Titulación masiva de bachilleres asignando folio y diploma oficial")
    void testUCPROV24_AprobarGraduacionMedia_Exitoso() {
        TitulacionGraduandoMedia g = new TitulacionGraduandoMedia();
        g.setId(10L);
        g.setMatricula(matricula);
        g.setEstadoTitulacion(EstadoTitulacionGraduando.APTO_GRADUACION);

        PromocionEstudianteAnual prom = new PromocionEstudianteAnual();
        prom.setEstadoPromocion(EstadoPromocionEstudiante.PROMOVIDO_DIRECTO);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(titulacionRepository.findByEstadoTitulacion(EstadoTitulacionGraduando.APTO_GRADUACION)).thenReturn(List.of(g));
        when(promocionEstudianteRepository.findByMatriculaId(100L)).thenReturn(Optional.of(prom));
        when(titulacionRepository.save(any(TitulacionGraduandoMedia.class))).thenAnswer(i -> i.getArgument(0));

        AprobarGraduacionRequest req = new AprobarGraduacionRequest();
        req.setAnioLectivoId(1L);
        req.setNumeroActaGeneral("ACT-GRAD-2026-GEN");

        List<TitulacionGraduandoResponse> resps = graduacionMediaService.aprobarGraduacionMedia(req, 1L);

        assertNotNull(resps);
        assertEquals(1, resps.size());
        assertEquals(EstadoTitulacionGraduando.GRADUADO_EGRESADO, resps.get(0).getEstadoTitulacion());
        assertEquals("IND-2026-001", resps.get(0).getNumeroActaIndividual());
        assertEquals(EstadoMatricula.FINALIZADA, matricula.getEstado());
    }

    @Test
    @DisplayName("UC-PROV-24 E3: Cierre definitivo del año escolar bloqueado si existen periodos abiertos")
    void testUCPROV24_E3_PeriodosAbiertos_BloqueaCierreAnual() {
        periodo.setEstado(EstadoPeriodo.ABIERTO); // Periodo aún abierto

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo));

        assertThrows(ConflictoNegocioException.class, () ->
                graduacionMediaService.cerrarAnioLectivoEscolar(1L, 1L));
    }

    @Test
    @DisplayName("UC-PROV-24: Cierre definitivo del año escolar a CERRADO_HISTORICO con todo en firme")
    void testUCPROV24_CierreAnualDefinitivo_Exitoso() {
        periodo.setEstado(EstadoPeriodo.CERRADO);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(periodoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(periodo));
        when(promocionEstudianteRepository.countByActaPromocionAnioLectivoIdAndEstadoPromocion(1L, EstadoPromocionEstudiante.PENDIENTE_RECUPERACION_FINAL))
                .thenReturn(0L); // Sin habilitaciones pendientes

        graduacionMediaService.cerrarAnioLectivoEscolar(1L, 1L);

        assertEquals(EstadoAnioLectivo.CERRADO, anio.getEstado());
    }
}
