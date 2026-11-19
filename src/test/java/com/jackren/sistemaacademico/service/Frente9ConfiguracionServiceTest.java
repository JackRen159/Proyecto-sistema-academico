package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
@DisplayName("Pruebas Unitarias - Configuración Institucional, Política de Datos y SIEE V2")
class Frente9ConfiguracionServiceTest {

    @Mock
    private InstitucionDatosLegalesRepository institucionRepository;

    @Mock
    private ParametroInstitucionalRepository parametroRepository;

    @Mock
    private PoliticaTratamientoDatosRepository politicaRepository;

    @Mock
    private ConsentimientoPoliticaDatosRepository consentimientoRepository;

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

    @InjectMocks
    private ConfiguracionInstitucionalService configuracionService;

    @InjectMocks
    private PoliticaDatosService politicaService;

    @InjectMocks
    private SieeConfiguracionService sieeService;

    @Test
    @DisplayName("actualizarDatosLegales exige código DANE de exactamente 12 dígitos")
    void testDane12Digitos() {
        ActualizarInstitucionRequest reqInvalido = new ActualizarInstitucionRequest();
        reqInvalido.setCodigoDane("12345"); // longitud inválida
        reqInvalido.setNombreOficial("Colegio");

        assertThrows(ValidacionException.class, () -> configuracionService.actualizarDatosLegales(reqInvalido, "admin"));
    }

    @Test
    @DisplayName("actualizarParametro PLAZO_ARCO_DIAS_HABILES no puede superar 15 días (E1)")
    void testPlazoArcoMaximo15Dias() {
        ParametroInstitucional p = new ParametroInstitucional("PLAZO_ARCO_DIAS_HABILES", "15", TipoDatoParametro.INTEGER, "Plazo ARCO", "SEGURIDAD");
        when(parametroRepository.findByClave("PLAZO_ARCO_DIAS_HABILES")).thenReturn(Optional.of(p));

        ActualizarParametroRequest reqExcesivo = new ActualizarParametroRequest("20", "Ampliación de plazo");
        assertThrows(ValidacionException.class, () -> configuracionService.actualizarParametro("PLAZO_ARCO_DIAS_HABILES", reqExcesivo, "admin"));
    }

    @Test
    @DisplayName("crearNuevaVersion de Política de Datos crea versión inmutable incremental y actualiza vigencia (A1)")
    void testPoliticaDatosInmutable() {
        PoliticaTratamientoDatos p1 = new PoliticaTratamientoDatos(1, "V1", "Texto", "Aviso", true);
        when(politicaRepository.findFirstByEsVigenteTrueOrderByNumeroVersionDesc()).thenReturn(Optional.of(p1));
        when(politicaRepository.findAll()).thenReturn(List.of(p1));
        when(politicaRepository.save(any(PoliticaTratamientoDatos.class))).thenAnswer(i -> {
            PoliticaTratamientoDatos pol = i.getArgument(0);
            pol.setId(2L);
            return pol;
        });

        CrearPoliticaDatosRequest req = new CrearPoliticaDatosRequest();
        req.setTitulo("Política V2");
        req.setContenidoPolitica("Texto V2");
        req.setAvisoPrivacidad("Aviso V2");
        req.setFechaVigencia(LocalDate.now());
        req.setEsVigente(true);

        PoliticaDatosResponse resp = politicaService.crearNuevaVersion(req, "rector");

        assertNotNull(resp);
        assertEquals(2, resp.getNumeroVersion());
        assertTrue(resp.getEsVigente());
        assertFalse(p1.getEsVigente()); // La anterior dejó de ser vigente pero no se eliminó físicamente
    }

    @Test
    @DisplayName("aprobarYPublicarSiee exige actas de Consejo Académico y Consejo Directivo (E2)")
    void testSieeExigeActasColegiadas() {
        ConfiguracionSieeVersion siee = new ConfiguracionSieeVersion();
        siee.setId(1L);
        siee.setEstado(EstadoVersionConfiguracion.BORRADOR);
        siee.setEscalaMinima(new BigDecimal("1.00"));
        siee.setEscalaAprobatoria(new BigDecimal("3.00"));
        siee.setEscalaMaxima(new BigDecimal("5.00"));
        siee.setRangosEquivalenciaJson("[{}]");

        when(sieeRepository.findById(1L)).thenReturn(Optional.of(siee));

        AprobarSieeRequest sinActaDirectivo = new AprobarSieeRequest("Acta Académico 01", "", LocalDate.now());
        assertThrows(ConflictoException.class, () -> sieeService.aprobarYPublicarSiee(1L, sinActaDirectivo, "rector"));
    }

    @Test
    @DisplayName("aprobarYPublicarSiee rechaza modificar versión ya VIGENTE o HISTORICA (E5)")
    void testSieeInmutabilidadVigente() {
        ConfiguracionSieeVersion sieeVigente = new ConfiguracionSieeVersion();
        sieeVigente.setId(2L);
        sieeVigente.setEstado(EstadoVersionConfiguracion.VIGENTE);

        when(sieeRepository.findById(2L)).thenReturn(Optional.of(sieeVigente));

        AprobarSieeRequest req = new AprobarSieeRequest("Acta 1", "Acta 2", LocalDate.now());
        assertThrows(ConflictoException.class, () -> sieeService.aprobarYPublicarSiee(2L, req, "rector"));
    }

    @Test
    @DisplayName("crearBorradorSiee valida consistencia matemática de la escala (E1)")
    void testSieeInconsistenciaEscala() {
        CrearSieeBorradorRequest reqInconsistente = new CrearSieeBorradorRequest();
        reqInconsistente.setNombreVersion("Invalida");
        reqInconsistente.setEscalaMinima(new BigDecimal("5.00"));
        reqInconsistente.setEscalaAprobatoria(new BigDecimal("3.00")); // Inconsistente min > aprob
        reqInconsistente.setEscalaMaxima(new BigDecimal("1.00"));
        reqInconsistente.setRangosEquivalenciaJson("[{}]");

        assertThrows(ValidacionException.class, () -> sieeService.crearBorradorSiee(reqInconsistente, "coord"));
    }
}
