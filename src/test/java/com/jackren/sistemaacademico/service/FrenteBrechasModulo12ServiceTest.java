package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.DocumentoMapper;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 12: Documentos, Reportes y Auditoría V2 al 100%")
class FrenteBrechasModulo12ServiceTest {

    @Mock private ConsecutivoDocumentoRepository consecutivoRepository;
    @Mock private RegistroEmisionDocumentoRepository emisionRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private PeriodoAcademicoRepository periodoRepository;
    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private GradoAsignaturaRepository gradoAsignaturaRepository;
    @Mock private AsignacionAcademicaRepository asignacionRepository;
    @Mock private CalificacionPeriodoRepository calificacionPeriodoRepository;
    @Mock private RegistroAsistenciaRepository registroAsistenciaRepository;
    @Mock private ConfiguracionEscalaSieeRepository escalaSieeRepository;
    @Mock private InstitucionDatosLegalesRepository institucionDatosLegalesRepository;
    @Mock private PromocionEstudianteAnualRepository promocionEstudianteAnualRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;
    @Mock private EstudianteAcudienteRepository estudianteAcudienteRepository;
    @Mock private AcudienteRepository acudienteRepository;
    @Mock private CasoConvivenciaRepository casoConvivenciaRepository;

    @Mock private RepositorioDocumentoRepository documentoRepository;
    @Mock private RepositorioDocumentoVersionRepository versionRepository;
    @Mock private TablaRetencionDocumentalRepository trdRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private GradoRepository gradoRepository;
    @Mock private GrupoRepository grupoRepository;
    @Mock private AlertaSeguridadAuditoriaRepository alertaRepository;

    @Spy private DocumentoMapper documentoMapper = new DocumentoMapper();

    private DocumentoAcademicoService documentoService;
    private RepositorioDocumentalService repositorioService;
    private ReporteInstitucionalService reporteService;

    private AnioLectivo anioLectivo;
    private Estudiante estudiante;
    private Matricula matricula;
    private Grupo grupo;
    private Grado grado;
    private Asignatura asignatura;
    private GradoAsignatura gradoAsignatura;
    private Usuario usuarioRector;
    private Usuario usuarioEstudiante;
    private Usuario usuarioAcudiente;
    private Acudiente acudiente;

    @BeforeEach
    void setUp() {
        documentoService = new DocumentoAcademicoService(
                consecutivoRepository, emisionRepository, matriculaRepository,
                periodoRepository, anioLectivoRepository, gradoAsignaturaRepository,
                asignacionRepository, calificacionPeriodoRepository,
                registroAsistenciaRepository, escalaSieeRepository, documentoMapper,
                institucionDatosLegalesRepository, promocionEstudianteAnualRepository,
                auditoriaGlobalService, estudianteAcudienteRepository,
                acudienteRepository, casoConvivenciaRepository
        );

        repositorioService = new RepositorioDocumentalService(
                documentoRepository, versionRepository, trdRepository,
                estudianteRepository, matriculaRepository, anioLectivoRepository,
                usuarioRepository, documentoService, auditoriaGlobalService
        );

        reporteService = new ReporteInstitucionalService(
                anioLectivoRepository, gradoRepository, grupoRepository,
                matriculaRepository, gradoAsignaturaRepository,
                calificacionPeriodoRepository, casoConvivenciaRepository,
                escalaSieeRepository, auditoriaGlobalService
        );

        anioLectivo = new AnioLectivo();
        anioLectivo.setId(1L);
        anioLectivo.setAnio(2026);
        anioLectivo.setEstado(EstadoAnioLectivo.CERRADO);

        estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Juan Carlos");
        estudiante.setApellidos("Pérez");
        estudiante.setDocumento("10203040");

        grado = new Grado();
        grado.setId(20L);
        grado.setNombre("Sexto");

        grupo = new Grupo();
        grupo.setId(30L);
        grupo.setNombre("6-A");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anioLectivo);
        grupo.setCupoMaximo(35);

        matricula = new Matricula();
        matricula.setId(50L);
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anioLectivo);
        matricula.setNumeroMatricula("MAT-2026-050");
        matricula.setEstado(EstadoMatricula.MATRICULADA);

        asignatura = new Asignatura();
        asignatura.setId(70L);
        asignatura.setNombre("Matemáticas");

        gradoAsignatura = new GradoAsignatura();
        gradoAsignatura.setGrado(grado);
        gradoAsignatura.setAsignatura(asignatura);
        gradoAsignatura.setIntensidadHoraria(4);
        gradoAsignatura.setActivo(true);

        usuarioRector = new Usuario("rector1", "hash", Rol.RECTOR);
        usuarioRector.setId(1L);

        usuarioEstudiante = new Usuario("est10", "hash", Rol.ESTUDIANTE);
        usuarioEstudiante.setId(2L);
        usuarioEstudiante.setPersonaId(10L);

        acudiente = new Acudiente();
        acudiente.setId(20L);
        acudiente.setDocumento("52001002");

        usuarioAcudiente = new Usuario("52001002", "hash", Rol.ACUDIENTE);
        usuarioAcudiente.setId(3L);
        usuarioAcudiente.setPersonaId(20L);
    }

    // =========================================================================
    // UC-05 Y UC-11: CONTROL CONTEXTUAL Y ERRADICACIÓN DE IDOR (RN-DOC-04)
    // =========================================================================

    @Test
    @DisplayName("UC-05 E2: Estudiante intenta acceder a documentos de otro alumno y es rechazado con 403 Forbidden")
    void uc05_E2_IdorEstudianteRechazado() {
        // Estudiante con personaId=10 intenta acceder al estudianteId=99
        assertThrows(AccessDeniedException.class, () -> documentoService.validarAccesoContextual(99L, usuarioEstudiante));
    }

    @Test
    @DisplayName("UC-11 E1: Acudiente con BLOQUEADO_RESTRICCION_LEGAL es rechazado con 403 Forbidden")
    void uc11_E1_AcudienteConRestriccionLegalRechazado() {
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(List.of(matricula));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(any(), any())).thenReturn(Optional.of(vinculo));

        assertThrows(AccessDeniedException.class, () -> documentoService.validarAccesoContextual(10L, usuarioAcudiente));
    }

    // =========================================================================
    // UC-PROV-27: EXPEDICIÓN DE CERTIFICADOS Y CONSTANCIAS
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-27 E2: Bloqueo de emisión de certificado definitivo si el año sigue abierto")
    void ucProv27_E2_BloqueoCertificadoAnioAbierto() {
        anioLectivo.setEstado(EstadoAnioLectivo.ACTIVO); // Año aún en curso
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));

        assertThrows(ConflictoNegocioException.class, () ->
                documentoService.generarCertificadoEstudio(50L, null, usuarioRector)
        );
    }

    @Test
    @DisplayName("UC-PROV-27 A1 / RN-DOC-02: Emisión de Certificado de Notas Parciales por Retiro")
    void ucProv27_A1_CertificadoNotasParcialesPorRetiro() {
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anioLectivo, TipoDocumentoAcademico.CERTIFICADO_NOTAS_PARCIALES_RETIRO, "CPAR", 0L, 5, null
        );
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(eq(1L), eq(TipoDocumentoAcademico.CERTIFICADO_NOTAS_PARCIALES_RETIRO)))
                .thenReturn(Optional.of(consecutivo));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(i -> i.getArgument(0));
        when(escalaSieeRepository.findByAnioLectivoId(1L)).thenReturn(Optional.empty());
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(20L)).thenReturn(List.of(gradoAsignatura));
        when(calificacionPeriodoRepository.findByMatriculaId(50L)).thenReturn(Collections.emptyList());

        CertificadoEstudioResponse resp = documentoService.generarCertificadoNotasParciales(50L, null, usuarioRector);

        assertNotNull(resp);
        assertEquals("PERIODO_PARCIAL_POR_RETIRO", resp.getResultadoPromocion());
        assertTrue(resp.getNumeroConsecutivo().startsWith("CPAR-"));
    }

    @Test
    @DisplayName("UC-PROV-27 A3 / RN-DOC-03: Versionamiento V+1 al regenerar un certificado por rectificación")
    void ucProv27_A3_VersionamientoV2AlRegenerar() {
        anioLectivo.setEstado(EstadoAnioLectivo.CERRADO);
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anioLectivo, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO, "CERT", 1L, 5, null
        );
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(eq(1L), eq(TipoDocumentoAcademico.CERTIFICADO_ESTUDIO)))
                .thenReturn(Optional.of(consecutivo));

        RegistroEmisionDocumento emisionV1 = new RegistroEmisionDocumento();
        emisionV1.setId(10L);
        emisionV1.setNumeroVersion(1);
        emisionV1.setEstadoEmision(EstadoEmisionDocumento.PUBLICADA_VIGENTE);

        when(emisionRepository.findByMatriculaIdAndTipoDocumentoAndAnuladoFalse(50L, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO))
                .thenReturn(Optional.of(emisionV1));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(i -> i.getArgument(0));
        when(escalaSieeRepository.findByAnioLectivoId(1L)).thenReturn(Optional.empty());
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(20L)).thenReturn(List.of(gradoAsignatura));
        when(calificacionPeriodoRepository.findByMatriculaId(50L)).thenReturn(Collections.emptyList());

        CertificadoEstudioResponse resp = documentoService.generarCertificadoEstudio(50L, null, usuarioRector);

        assertNotNull(resp);
        assertEquals(EstadoEmisionDocumento.HISTORICA_REEMPLAZADA_POR_RECTIFICACION, emisionV1.getEstadoEmision());
    }

    // =========================================================================
    // UC-PROV-28: REPOSITORIO DOCUMENTAL Y TRD (LEY 594 DE 2000)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-28 Flujo Principal: Carga y clasificación en repositorio seguro")
    void ucProv28_CargaDocumentoSeguro() {
        CargarDocumentoRequest req = new CargarDocumentoRequest(
                "Ficha Escolar 2026", "Expediente del estudiante",
                "FICHA_ESCOLAR", NivelConfidencialidadDocumental.ADMINISTRATIVO_EXPEDIENTE,
                10L, 50L, 1L, null, "ficha.pdf", "application/pdf",
                1024L, "/storage/ficha.pdf", "ZGF0YQ=="
        );

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioRector));
        when(documentoRepository.save(any(RepositorioDocumento.class))).thenAnswer(i -> {
            RepositorioDocumento d = i.getArgument(0);
            d.setId(101L);
            return d;
        });

        RepositorioDocumentoResponse resp = repositorioService.cargarDocumento(req, 1L);

        assertNotNull(resp);
        assertEquals(1, resp.versionActual());
        assertNotNull(resp.hashSha256Actual());
        assertEquals(NivelConfidencialidadDocumental.ADMINISTRATIVO_EXPEDIENTE, resp.nivelConfidencialidad());
    }

    @Test
    @DisplayName("UC-PROV-28 E3: Prohibición absoluta de borrado físico conforme a Ley 594 de 2000")
    void ucProv28_E3_ProhibicionBorradoFisico() {
        assertThrows(ConflictoNegocioException.class, () -> repositorioService.eliminarDocumentoFisico(101L));
    }

    // =========================================================================
    // UC-27: REPORTES INSTITUCIONALES (SIMAT, DANE, SIUCE)
    // =========================================================================

    @Test
    @DisplayName("UC-27: Reporte oficial de Cobertura SIMAT/DANE con cálculo de cupos")
    void uc27_ReporteCoberturaSimat() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(gradoRepository.findAll()).thenReturn(List.of(grado));
        when(matriculaRepository.findByAnioLectivoIdAndEstado(1L, EstadoMatricula.MATRICULADA)).thenReturn(List.of(matricula));
        when(grupoRepository.findByAnioLectivoIdAndActivoTrue(1L)).thenReturn(List.of(grupo));

        ReporteCoberturaSimatResponse resp = reporteService.generarReporteCoberturaSimat(1L, 1L);

        assertNotNull(resp);
        assertEquals(1, resp.totalMatriculados());
        assertEquals(35, resp.totalCupos());
        assertEquals(34, resp.grados().get(0).cuposDisponibles());
    }

    @Test
    @DisplayName("UC-27: Reporte de Convivencia SIUCE con cifras y agregación")
    void uc27_ReporteConvivenciaSiuce() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(casoConvivenciaRepository.findByAnioLectivoId(1L)).thenReturn(Collections.emptyList());

        ReporteConvivenciaSiuceResponse resp = reporteService.generarReporteConvivenciaSiuce(1L, 1L);

        assertNotNull(resp);
        assertEquals(0, resp.totalCasos());
    }
}
