package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Reportes Institucionales (UC-27) y Alertas de Auditoría (UC-PROV-29)")
class ReportesYAlertasServiceTest {

    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private GradoRepository gradoRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private GradoAsignaturaRepository gradoAsignaturaRepository;
    @Mock private CalificacionPeriodoRepository calificacionPeriodoRepository;
    @Mock private CasoConvivenciaRepository casoConvivenciaRepository;
    @Mock private ConfiguracionEscalaSieeRepository escalaSieeRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;
    @Mock private AlertaSeguridadAuditoriaRepository alertaRepository;
    @Mock private AuditoriaEventoRepository auditoriaEventoRepository;
    @Mock private UsuarioRepository usuarioRepository;

    private ReporteInstitucionalService reporteService;
    private AuditoriaGlobalService auditoriaServiceConAlertas;

    private AnioLectivo anioLectivo;
    private Grado grado;

    @BeforeEach
    void setUp() {
        reporteService = new ReporteInstitucionalService(
                anioLectivoRepository,
                gradoRepository,
                grupoRepository,
                matriculaRepository,
                gradoAsignaturaRepository,
                calificacionPeriodoRepository,
                casoConvivenciaRepository,
                escalaSieeRepository,
                auditoriaGlobalService
        );

        auditoriaServiceConAlertas = new AuditoriaGlobalService(auditoriaEventoRepository, usuarioRepository);
        org.springframework.test.util.ReflectionTestUtils.setField(auditoriaServiceConAlertas, "alertaRepository", alertaRepository);

        anioLectivo = new AnioLectivo();
        anioLectivo.setId(1L);
        anioLectivo.setAnio(2026);

        grado = new Grado();
        grado.setId(10L);
        grado.setNombre("Sexto");
    }

    @Test
    @DisplayName("UC-27: Generación de Reporte de Cobertura SIMAT/DANE con cupos y desglose de género")
    void generarReporteCoberturaSimat_Exitoso() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(gradoRepository.findAll()).thenReturn(List.of(grado));

        Estudiante est = new Estudiante();
        est.setSexo(SexoDocumental.MASCULINO);
        Grupo grp = new Grupo();
        grp.setGrado(grado);
        grp.setCupoMaximo(35);

        Matricula mat = new Matricula();
        mat.setEstudiante(est);
        mat.setGrupo(grp);
        mat.setEstado(EstadoMatricula.MATRICULADA);

        when(matriculaRepository.findByAnioLectivoIdAndEstado(1L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(mat));
        when(grupoRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(grp));

        ReporteCoberturaSimatResponse response = reporteService.generarReporteCoberturaSimat(1L, 1L);

        assertNotNull(response);
        assertEquals(2026, response.anio());
        assertEquals(1, response.totalMatriculados());
        assertEquals(1, response.grados().size());
        assertEquals("Sexto", response.grados().get(0).nombreGrado());
        assertEquals(1, response.grados().get(0).masculino());
    }

    @Test
    @DisplayName("UC-27: Generación de Reporte de Convivencia SIUCE con cifras estadísticas")
    void generarReporteConvivenciaSiuce_Exitoso() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));

        CasoConvivencia c1 = new CasoConvivencia();
        c1.setTipoSituacion(TipoSituacionConvivencia.TIPO_I);
        c1.setEstadoCaso(EstadoCasoConvivencia.CERRADO_SUPERADO);

        CasoConvivencia c2 = new CasoConvivencia();
        c2.setTipoSituacion(TipoSituacionConvivencia.TIPO_III);
        c2.setRemitidoAutoridadExterna(true);
        c2.setEstadoCaso(EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION);

        when(casoConvivenciaRepository.findByAnioLectivoId(1L)).thenReturn(List.of(c1, c2));

        ReporteConvivenciaSiuceResponse response = reporteService.generarReporteConvivenciaSiuce(1L, 1L);

        assertNotNull(response);
        assertEquals(2, response.totalCasos());
        assertEquals(1, response.casosTipoI());
        assertEquals(1, response.casosTipoIII());
        assertEquals(1, response.remitidosAutoridadExterna());
        assertEquals(1, response.casosCerradosSuperados());
    }

    @Test
    @DisplayName("UC-27 Paso 6: Exportación de reporte a CSV delimitado oficial")
    void exportarReporte_CSV_Exitoso() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(casoConvivenciaRepository.findByAnioLectivoId(1L)).thenReturn(Collections.emptyList());

        byte[] csv = reporteService.exportarReporte("CONVIVENCIA_SIUCE", "CSV", 1L, 1L);

        assertNotNull(csv);
        String contenido = new String(csv, StandardCharsets.UTF_8);
        assertTrue(contenido.contains("TOTAL_CASOS"));
        assertTrue(contenido.contains("TIPO_I"));
    }

    @Test
    @DisplayName("UC-PROV-29 A2: Registro y consulta de alertas de seguridad automáticas")
    void registrarAlertaSeguridad_Y_ListarPendientes() {
        AlertaSeguridadAuditoria alerta = new AlertaSeguridadAuditoria();
        alerta.setId(5L);
        alerta.setTipoAlerta(TipoAlertaSeguridad.ACCESO_ACUDIENTE_BLOQUEADO_LEGAL);
        alerta.setSeveridad(SeveridadAlertaSeguridad.CRITICA);
        alerta.setDescripcionEvento("Intento de acceso bloqueado");

        when(alertaRepository.findByLeidaPorRectorFalseOrderByFechaAlertaDesc()).thenReturn(List.of(alerta));

        auditoriaServiceConAlertas.registrarAlertaSeguridad(
                TipoAlertaSeguridad.ACCESO_ACUDIENTE_BLOQUEADO_LEGAL,
                SeveridadAlertaSeguridad.CRITICA,
                "Intento de acceso bloqueado",
                null, "127.0.0.1", "CORR-001"
        );

        List<AlertaSeguridadResponse> pendientes = auditoriaServiceConAlertas.listarAlertasPendientesRector();

        assertNotNull(pendientes);
        assertEquals(1, pendientes.size());
        assertEquals(TipoAlertaSeguridad.ACCESO_ACUDIENTE_BLOQUEADO_LEGAL, pendientes.get(0).tipoAlerta());
    }
}
