package com.jackren.sistemaacademico.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 06: Configuración Institucional V2 al 100%")
class FrenteBrechasModulo06ServiceTest {

    @Mock
    private ConfiguracionSieeVersionRepository sieeRepository;

    @Mock
    private ConfiguracionManualConvivenciaVersionRepository manualRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Mock
    private InstitucionDatosLegalesRepository institucionRepository;

    @Mock
    private ParametroInstitucionalRepository parametroRepository;

    private SieeConfiguracionService sieeService;
    private ConfiguracionInstitucionalService institucionService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String RANGOS_VALIDOS_JSON = "[" +
            "{\"desempeno\":\"BAJO\",\"min\":1.00,\"max\":2.99}," +
            "{\"desempeno\":\"BASICO\",\"min\":3.00,\"max\":3.99}," +
            "{\"desempeno\":\"ALTO\",\"min\":4.00,\"max\":4.59}," +
            "{\"desempeno\":\"SUPERIOR\",\"min\":4.60,\"max\":5.00}" +
            "]";

    private final String PONDERACIONES_VALIDAS_JSON = "[" +
            "{\"periodo\":1,\"porcentaje\":25.00}," +
            "{\"periodo\":2,\"porcentaje\":25.00}," +
            "{\"periodo\":3,\"porcentaje\":25.00}," +
            "{\"periodo\":4,\"porcentaje\":25.00}" +
            "]";

    @BeforeEach
    void setUp() {
        sieeService = new SieeConfiguracionService(
                sieeRepository,
                manualRepository,
                anioLectivoRepository,
                usuarioRepository,
                auditoriaService,
                objectMapper
        );

        institucionService = new ConfiguracionInstitucionalService(
                institucionRepository,
                parametroRepository,
                auditoriaService
        );
    }

    // ── VALIDADOR MATEMÁTICO DE ESCALA CONTINUA (E1, RN-02, RN-CFG-03) ─────────

    @Test
    @DisplayName("validarConsistenciaMatematicaSiee detecta hueco numérico en la escala (E1)")
    void testValidacionEscalaContinuaHuecoRechazado() {
        // Hueco entre 2.90 y 3.00 (faltan 2.91 a 2.99)
        String rangosConHueco = "[" +
                "{\"desempeno\":\"BAJO\",\"min\":1.00,\"max\":2.90}," +
                "{\"desempeno\":\"BASICO\",\"min\":3.00,\"max\":3.99}," +
                "{\"desempeno\":\"ALTO\",\"min\":4.00,\"max\":4.59}," +
                "{\"desempeno\":\"SUPERIOR\",\"min\":4.60,\"max\":5.00}" +
                "]";

        CrearSieeBorradorRequest req = new CrearSieeBorradorRequest();
        req.setNombreVersion("SIEE Hueco");
        req.setEscalaMinima(new BigDecimal("1.00"));
        req.setEscalaAprobatoria(new BigDecimal("3.00"));
        req.setEscalaMaxima(new BigDecimal("5.00"));
        req.setRangosEquivalenciaJson(rangosConHueco);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> sieeService.crearBorradorSiee(req, "coord"));
        assertTrue(ex.getMessage().contains("Hueco numérico"));
    }

    @Test
    @DisplayName("validarConsistenciaMatematicaSiee detecta solapamiento entre rangos (E1)")
    void testValidacionEscalaContinuaSolapamientoRechazado() {
        // Solapamiento: BAJO llega hasta 3.20 y BASICO inicia en 3.00
        String rangosConSolapamiento = "[" +
                "{\"desempeno\":\"BAJO\",\"min\":1.00,\"max\":3.20}," +
                "{\"desempeno\":\"BASICO\",\"min\":3.00,\"max\":3.99}," +
                "{\"desempeno\":\"ALTO\",\"min\":4.00,\"max\":4.59}," +
                "{\"desempeno\":\"SUPERIOR\",\"min\":4.60,\"max\":5.00}" +
                "]";

        CrearSieeBorradorRequest req = new CrearSieeBorradorRequest();
        req.setNombreVersion("SIEE Solapado");
        req.setEscalaMinima(new BigDecimal("1.00"));
        req.setEscalaAprobatoria(new BigDecimal("3.00"));
        req.setEscalaMaxima(new BigDecimal("5.00"));
        req.setRangosEquivalenciaJson(rangosConSolapamiento);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> sieeService.crearBorradorSiee(req, "coord"));
        assertTrue(ex.getMessage().contains("Solapamiento"));
    }

    @Test
    @DisplayName("validarConsistenciaMatematicaSiee rechaza ponderaciones que no suman 100.00% (E1)")
    void testPonderacionesIncompletasRechazadas() {
        // Suma 90.00% en vez de 100.00%
        String ponderacion90 = "[" +
                "{\"periodo\":1,\"porcentaje\":25.00}," +
                "{\"periodo\":2,\"porcentaje\":25.00}," +
                "{\"periodo\":3,\"porcentaje\":20.00}," +
                "{\"periodo\":4,\"porcentaje\":20.00}" +
                "]";

        CrearSieeBorradorRequest req = new CrearSieeBorradorRequest();
        req.setNombreVersion("SIEE 90%");
        req.setEscalaMinima(new BigDecimal("1.00"));
        req.setEscalaAprobatoria(new BigDecimal("3.00"));
        req.setEscalaMaxima(new BigDecimal("5.00"));
        req.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);
        req.setPonderacionPeriodosJson(ponderacion90);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> sieeService.crearBorradorSiee(req, "coord"));
        assertTrue(ex.getMessage().contains("100.00%"));
    }

    @Test
    @DisplayName("crearBorradorSiee persiste exitosamente con escala y ponderaciones válidas")
    void testCrearBorradorSieeValido() {
        when(sieeRepository.findTopByOrderByNumeroVersionDesc()).thenReturn(Optional.empty());
        when(sieeRepository.save(any(ConfiguracionSieeVersion.class))).thenAnswer(i -> {
            ConfiguracionSieeVersion s = i.getArgument(0);
            s.setId(10L);
            return s;
        });

        CrearSieeBorradorRequest req = new CrearSieeBorradorRequest();
        req.setNombreVersion("SIEE V1");
        req.setEscalaMinima(new BigDecimal("1.00"));
        req.setEscalaAprobatoria(new BigDecimal("3.00"));
        req.setEscalaMaxima(new BigDecimal("5.00"));
        req.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);
        req.setPonderacionPeriodosJson(PONDERACIONES_VALIDAS_JSON);

        ConfiguracionSieeResponse resp = sieeService.crearBorradorSiee(req, "coord");
        assertNotNull(resp);
        assertEquals(1, resp.getNumeroVersion());
        assertEquals(EstadoVersionConfiguracion.BORRADOR, resp.getEstado());
    }

    // ── CLONACIÓN Y SEGREGACIÓN TÉCNICA (UC-26 Paso 2, A1) ──────────────────────

    @Test
    @DisplayName("clonarSiee genera nuevo borrador con versión incremental (Paso 2)")
    void testClonarSieeHaciaNuevoBorrador() {
        ConfiguracionSieeVersion fuente = new ConfiguracionSieeVersion();
        fuente.setId(1L);
        fuente.setNumeroVersion(1);
        fuente.setNombreVersion("SIEE 2026");
        fuente.setEscalaMinima(new BigDecimal("1.00"));
        fuente.setEscalaAprobatoria(new BigDecimal("3.00"));
        fuente.setEscalaMaxima(new BigDecimal("5.00"));
        fuente.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);
        fuente.setEstado(EstadoVersionConfiguracion.VIGENTE);

        when(sieeRepository.findById(1L)).thenReturn(Optional.of(fuente));
        when(sieeRepository.findTopByOrderByNumeroVersionDesc()).thenReturn(Optional.of(fuente));
        when(sieeRepository.save(any(ConfiguracionSieeVersion.class))).thenAnswer(i -> {
            ConfiguracionSieeVersion s = i.getArgument(0);
            s.setId(2L);
            return s;
        });

        ClonarVersionRequest req = new ClonarVersionRequest("SIEE 2027 Propuesta", null);
        ConfiguracionSieeResponse resp = sieeService.clonarSiee(1L, req, "coord");

        assertNotNull(resp);
        assertEquals(2, resp.getNumeroVersion());
        assertEquals("SIEE 2027 Propuesta", resp.getNombreVersion());
        assertEquals(EstadoVersionConfiguracion.BORRADOR, resp.getEstado());
    }

    @Test
    @DisplayName("marcarSieeListoParaAprobacion transiciona estado de BORRADOR a LISTA_PARA_APROBACION (A1)")
    void testMarcarSieeListoParaAprobacion() {
        ConfiguracionSieeVersion borrador = new ConfiguracionSieeVersion();
        borrador.setId(5L);
        borrador.setNumeroVersion(2);
        borrador.setEstado(EstadoVersionConfiguracion.BORRADOR);
        borrador.setEscalaMinima(new BigDecimal("1.00"));
        borrador.setEscalaAprobatoria(new BigDecimal("3.00"));
        borrador.setEscalaMaxima(new BigDecimal("5.00"));
        borrador.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);

        when(sieeRepository.findById(5L)).thenReturn(Optional.of(borrador));
        when(sieeRepository.save(any(ConfiguracionSieeVersion.class))).thenAnswer(i -> i.getArgument(0));

        ConfiguracionSieeResponse resp = sieeService.marcarSieeListoParaAprobacion(5L, "coord");
        assertEquals(EstadoVersionConfiguracion.LISTA_PARA_APROBACION, resp.getEstado());
    }

    // ── APROBACIÓN, VIGENCIA FUTURA E IRRETROACTIVIDAD (A2, E2, E3, E5) ─────────

    @Test
    @DisplayName("aprobarYPublicarSiee exige constancia de socialización ante comunidad educativa (E2)")
    void testAprobarSieeExigeSocializacion() {
        ConfiguracionSieeVersion borrador = new ConfiguracionSieeVersion();
        borrador.setId(1L);
        borrador.setEstado(EstadoVersionConfiguracion.LISTA_PARA_APROBACION);
        borrador.setEscalaMinima(new BigDecimal("1.00"));
        borrador.setEscalaAprobatoria(new BigDecimal("3.00"));
        borrador.setEscalaMaxima(new BigDecimal("5.00"));
        borrador.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);

        when(sieeRepository.findById(1L)).thenReturn(Optional.of(borrador));

        AprobarSieeRequest reqSinSocializacion = new AprobarSieeRequest("Acta 1", "Acta 2", LocalDate.now());
        // constanciaSocializacion is null

        ConflictoException ex = assertThrows(ConflictoException.class, () -> sieeService.aprobarYPublicarSiee(1L, reqSinSocializacion, "rector"));
        assertTrue(ex.getMessage().contains("socialización"));
    }

    @Test
    @DisplayName("aprobarYPublicarSiee transiciona a APROBADA_VIGENCIA_FUTURA si año lectivo está en planeación (A2)")
    void testAprobarSieeVigenciaFuturaEnPlaneacion() {
        AnioLectivo anioPlaneacion = new AnioLectivo();
        anioPlaneacion.setId(2027L);
        anioPlaneacion.setEstado(EstadoAnioLectivo.PLANIFICACION);

        ConfiguracionSieeVersion borrador = new ConfiguracionSieeVersion();
        borrador.setId(3L);
        borrador.setEstado(EstadoVersionConfiguracion.LISTA_PARA_APROBACION);
        borrador.setAnioLectivoVigencia(anioPlaneacion);
        borrador.setEscalaMinima(new BigDecimal("1.00"));
        borrador.setEscalaAprobatoria(new BigDecimal("3.00"));
        borrador.setEscalaMaxima(new BigDecimal("5.00"));
        borrador.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);

        when(sieeRepository.findById(3L)).thenReturn(Optional.of(borrador));
        when(sieeRepository.save(any(ConfiguracionSieeVersion.class))).thenAnswer(i -> i.getArgument(0));

        AprobarSieeRequest req = new AprobarSieeRequest("Acta Académico 05", "Acta Directivo 05", LocalDate.now());
        req.setConstanciaSocializacion("Socialización virtual en circular informativa");

        ConfiguracionSieeResponse resp = sieeService.aprobarYPublicarSiee(3L, req, "rector");
        assertEquals(EstadoVersionConfiguracion.APROBADA_VIGENCIA_FUTURA, resp.getEstado());
    }

    @Test
    @DisplayName("aprobarYPublicarSiee bloquea aplicación retroactiva a año lectivo cerrado (E3)")
    void testAprobarSieeBloqueaAnioCerrado() {
        AnioLectivo anioCerrado = new AnioLectivo();
        anioCerrado.setId(2025L);
        anioCerrado.setEstado(EstadoAnioLectivo.CERRADO);

        ConfiguracionSieeVersion borrador = new ConfiguracionSieeVersion();
        borrador.setId(4L);
        borrador.setEstado(EstadoVersionConfiguracion.BORRADOR);
        borrador.setAnioLectivoVigencia(anioCerrado);
        borrador.setEscalaMinima(new BigDecimal("1.00"));
        borrador.setEscalaAprobatoria(new BigDecimal("3.00"));
        borrador.setEscalaMaxima(new BigDecimal("5.00"));
        borrador.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);

        when(sieeRepository.findById(4L)).thenReturn(Optional.of(borrador));

        AprobarSieeRequest req = new AprobarSieeRequest("Acta 1", "Acta 2", LocalDate.now());
        req.setConstanciaSocializacion("Constancia");

        assertThrows(ConflictoException.class, () -> sieeService.aprobarYPublicarSiee(4L, req, "rector"));
    }

    @Test
    @DisplayName("rechazarEliminacionVersionSiee lanza HTTP 409 Conflicto protegiendo inmutabilidad (E5)")
    void testInmutabilidadRechazoEliminacionSiee() {
        assertThrows(ConflictoException.class, () -> sieeService.rechazarEliminacionVersionSiee(1L));
    }

    // ── SIMULADOR DE NOTAS Y EQUIVALENCIAS (UC-26 A4) ───────────────────────────

    @Test
    @DisplayName("simularSiee calcula promedio, redondeo y desempeño nacional con nivelación (A4)")
    void testSimulacionNotasSiee() {
        ConfiguracionSieeVersion siee = new ConfiguracionSieeVersion();
        siee.setId(1L);
        siee.setEscalaMinima(new BigDecimal("1.00"));
        siee.setEscalaAprobatoria(new BigDecimal("3.00"));
        siee.setEscalaMaxima(new BigDecimal("5.00"));
        siee.setDecimales(2);
        siee.setModoRedondeo("HALF_UP");
        siee.setRangosEquivalenciaJson(RANGOS_VALIDOS_JSON);

        when(sieeRepository.findById(1L)).thenReturn(Optional.of(siee));

        // 3 notas: 2.0, 2.5, 3.0 -> promedio = 2.50 (BAJO). Nivelación = 3.50 -> notaFinal = 3.50 (BASICO, APROBADO)
        SimulacionSieeRequest req = new SimulacionSieeRequest(
                List.of(new BigDecimal("2.00"), new BigDecimal("2.50"), new BigDecimal("3.00")),
                null,
                new BigDecimal("3.50")
        );

        SimulacionSieeResponse resp = sieeService.simularSiee(1L, req);
        assertNotNull(resp);
        assertEquals(new BigDecimal("2.50"), resp.getNotaPromedio());
        assertEquals(new BigDecimal("3.50"), resp.getNotaFinal());
        assertEquals("BASICO", resp.getDesempenoNacional());
        assertTrue(resp.getAprobado());
    }

    // ── COMPARADOR DE DIFERENCIAS (UC-PROV-14 A1) ───────────────────────────────

    @Test
    @DisplayName("compararVersionesSiee resalta diferencias estructuradas entre versiones (A1)")
    void testCompararVersionesSieeDiff() {
        ConfiguracionSieeVersion v1 = new ConfiguracionSieeVersion();
        v1.setId(1L);
        v1.setNumeroVersion(1);
        v1.setNombreVersion("SIEE V1");
        v1.setEscalaAprobatoria(new BigDecimal("3.00"));

        ConfiguracionSieeVersion v2 = new ConfiguracionSieeVersion();
        v2.setId(2L);
        v2.setNumeroVersion(2);
        v2.setNombreVersion("SIEE V2");
        v2.setEscalaAprobatoria(new BigDecimal("3.50"));

        when(sieeRepository.findById(1L)).thenReturn(Optional.of(v1));
        when(sieeRepository.findById(2L)).thenReturn(Optional.of(v2));

        DiferenciaVersionResponse resp = sieeService.compararVersionesSiee(1L, 2L);
        assertNotNull(resp);
        assertEquals(1, resp.getVersionA());
        assertEquals(2, resp.getVersionB());
        assertFalse(resp.getDiferencias().isEmpty());
    }

    // ── MANUAL DE CONVIVENCIA ESCOLAR (UC-26, UC-PROV-14) ───────────────────────

    @Test
    @DisplayName("crearBorradorManual persiste nuevo borrador con número incremental")
    void testCrearBorradorManual() {
        when(manualRepository.findTopByOrderByNumeroVersionDesc()).thenReturn(Optional.empty());
        when(manualRepository.save(any(ConfiguracionManualConvivenciaVersion.class))).thenAnswer(i -> {
            ConfiguracionManualConvivenciaVersion m = i.getArgument(0);
            m.setId(100L);
            return m;
        });

        CrearManualBorradorRequest req = new CrearManualBorradorRequest();
        req.setNombreVersion("Manual 2026");
        req.setSituacionesTipo1Json("{\"conflictos\": true}");
        req.setPlazosDebidoProcesoJson("{\"descargos\": 3}");

        ConfiguracionManualConvivenciaResponse resp = sieeService.crearBorradorManual(req, "coord_conv");
        assertNotNull(resp);
        assertEquals(1, resp.getNumeroVersion());
        assertEquals(EstadoVersionConfiguracion.BORRADOR, resp.getEstado());
    }

    @Test
    @DisplayName("clonarManual genera borrador a partir de versión previa")
    void testClonarManual() {
        ConfiguracionManualConvivenciaVersion fuente = new ConfiguracionManualConvivenciaVersion();
        fuente.setId(1L);
        fuente.setNumeroVersion(1);
        fuente.setNombreVersion("Manual Vigente");
        fuente.setSituacionesTipo1Json("{\"t1\": true}");

        when(manualRepository.findById(1L)).thenReturn(Optional.of(fuente));
        when(manualRepository.findTopByOrderByNumeroVersionDesc()).thenReturn(Optional.of(fuente));
        when(manualRepository.save(any(ConfiguracionManualConvivenciaVersion.class))).thenAnswer(i -> {
            ConfiguracionManualConvivenciaVersion m = i.getArgument(0);
            m.setId(2L);
            return m;
        });

        ClonarVersionRequest req = new ClonarVersionRequest("Manual 2027", null);
        ConfiguracionManualConvivenciaResponse resp = sieeService.clonarManual(1L, req, "coord_conv");

        assertNotNull(resp);
        assertEquals(2, resp.getNumeroVersion());
        assertEquals(EstadoVersionConfiguracion.BORRADOR, resp.getEstado());
    }

    @Test
    @DisplayName("rechazarEliminacionVersionManual lanza ConflictoException protegiendo inmutabilidad (E5)")
    void testInmutabilidadRechazoEliminacionManual() {
        assertThrows(ConflictoException.class, () -> sieeService.rechazarEliminacionVersionManual(1L));
    }

    // ── IDENTIFICACIÓN LEGAL INSTITUCIONAL (UC-PROV-13, RN-CFG-08) ──────────────

    @Test
    @DisplayName("actualizarDatosLegales persiste URLs de escudo, membrete y firmas autorizadas")
    void testActualizarDatosLegalesConUrls() {
        InstitucionDatosLegales inst = new InstitucionDatosLegales();
        inst.setId(1L);
        inst.setNombreOficial("Colegio Departamental");
        inst.setCodigoDane("123456789012");
        when(institucionRepository.findFirstByActivoTrue()).thenReturn(Optional.of(inst));
        when(institucionRepository.save(any(InstitucionDatosLegales.class))).thenAnswer(i -> i.getArgument(0));

        ActualizarInstitucionRequest req = new ActualizarInstitucionRequest();
        req.setNombreOficial("Colegio Departamental");
        req.setCodigoDane("123456789012");
        req.setNit("900123456-7");
        req.setResolucionAprobacion("Resolución 100 de 2020");
        req.setCaracter(CaracterInstitucion.OFICIAL);
        req.setNombreRectorFirmante("Rector Test");
        req.setNombreSecretarioFirmante("Secretaria Test");
        req.setEscudoUrl("https://storage.institucion.edu.co/escudo.png");
        req.setMembreteUrl("https://storage.institucion.edu.co/membrete.png");
        req.setFirmaRectorUrl("https://storage.institucion.edu.co/firma-rector.png");
        req.setFirmaSecretarioUrl("https://storage.institucion.edu.co/firma-secretario.png");

        InstitucionDatosLegalesResponse resp = institucionService.actualizarDatosLegales(req, "rector");
        assertNotNull(resp);
        assertEquals("https://storage.institucion.edu.co/escudo.png", resp.getEscudoUrl());
        assertEquals("https://storage.institucion.edu.co/firma-rector.png", resp.getFirmaRectorUrl());
    }
}
