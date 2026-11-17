package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.DocumentoMapper;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - DocumentoAcademicoService (Consecutivos, Boletines, Constancias y Certificados)")
class DocumentoAcademicoServiceTest {

    @Mock
    private ConsecutivoDocumentoRepository consecutivoRepository;

    @Mock
    private RegistroEmisionDocumentoRepository emisionRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private PeriodoAcademicoRepository periodoRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private GradoAsignaturaRepository gradoAsignaturaRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private CalificacionPeriodoRepository calificacionPeriodoRepository;

    @Mock
    private RegistroAsistenciaRepository registroAsistenciaRepository;

    @Mock
    private ConfiguracionEscalaSieeRepository escalaSieeRepository;

    @Spy
    private DocumentoMapper documentoMapper = new DocumentoMapper();

    @InjectMocks
    private DocumentoAcademicoService documentoService;

    private AnioLectivo anioLectivo;
    private PeriodoAcademico periodo;
    private Grado grado;
    private Grupo grupo;
    private Estudiante estudiante;
    private Matricula matricula;
    private Area area;
    private Asignatura asignatura;
    private GradoAsignatura gradoAsignatura;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        anioLectivo = new AnioLectivo();
        anioLectivo.setId(1L);
        anioLectivo.setAnio(2026);

        periodo = new PeriodoAcademico();
        periodo.setId(10L);
        periodo.setNombre("Primer Periodo");
        periodo.setNumero(1);
        periodo.setAnioLectivo(anioLectivo);

        grado = new Grado();
        grado.setId(20L);
        grado.setNombre("Sexto");

        grupo = new Grupo();
        grupo.setId(30L);
        grupo.setNombre("601");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anioLectivo);
        grupo.setJornada(Jornada.MANANA);

        estudiante = new Estudiante();
        estudiante.setId(40L);
        estudiante.setNombres("Juan Carlos");
        estudiante.setApellidos("Pérez López");
        estudiante.setDocumento("1020304050");

        matricula = new Matricula();
        matricula.setId(50L);
        matricula.setNumeroMatricula("MAT-2026-001");
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(anioLectivo);
        matricula.setEstado(EstadoMatricula.MATRICULADA);

        area = new Area();
        area.setId(60L);
        area.setNombre("Ciencias Naturales");

        asignatura = new Asignatura();
        asignatura.setId(70L);
        asignatura.setNombre("Biología");
        asignatura.setArea(area);

        gradoAsignatura = new GradoAsignatura();
        gradoAsignatura.setGrado(grado);
        gradoAsignatura.setAsignatura(asignatura);
        gradoAsignatura.setIntensidadHoraria(4);
        gradoAsignatura.setActivo(true);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("rector@colegio.edu.co");
    }

    @Test
    @DisplayName("guardarOActualizarConsecutivo debe guardar configuración correctamente")
    void guardarOActualizarConsecutivo_Exitoso() {
        ConfiguracionConsecutivoRequest req = new ConfiguracionConsecutivoRequest();
        req.setAnioLectivoId(1L);
        req.setTipoDocumento(TipoDocumentoAcademico.CERTIFICADO_ESTUDIO);
        req.setPrefijo("CERT");
        req.setUltimoNumero(10L);
        req.setLongitudRelleno(5);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anioLectivo));
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumento(1L, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO))
                .thenReturn(Optional.empty());
        when(consecutivoRepository.save(any(ConsecutivoDocumento.class))).thenAnswer(inv -> {
            ConsecutivoDocumento c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        ConfiguracionConsecutivoResponse res = documentoService.guardarOActualizarConsecutivo(req);

        assertNotNull(res);
        assertEquals(100L, res.getId());
        assertEquals("CERT", res.getPrefijo());
        assertEquals(10L, res.getUltimoNumero());
    }

    @Test
    @DisplayName("generarBoletinPeriodo debe consolidar notas SIEE, asistencias y metadatos oficiales")
    void generarBoletinPeriodo_Exitoso() {
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));
        when(periodoRepository.findById(10L)).thenReturn(Optional.of(periodo));

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anioLectivo, TipoDocumentoAcademico.BOLETIN_PERIODO, "BOL", 0L, 5, null
        );
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(1L, TipoDocumentoAcademico.BOLETIN_PERIODO))
                .thenReturn(Optional.of(consecutivo));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));

        when(escalaSieeRepository.findByAnioLectivoId(1L)).thenReturn(Optional.empty()); // Usa defaults
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(20L)).thenReturn(List.of(gradoAsignatura));

        CalificacionPeriodo cp = new CalificacionPeriodo();
        cp.setMatricula(matricula);
        cp.setAsignatura(asignatura);
        cp.setPeriodoAcademico(periodo);
        cp.setNotaDefinitiva(new BigDecimal("4.20"));
        cp.setDesempeno(DesempenoNacional.ALTO);

        when(calificacionPeriodoRepository.findByMatriculaIdAndPeriodoAcademicoId(50L, 10L)).thenReturn(List.of(cp));
        when(registroAsistenciaRepository.findByMatriculaIdAndSesionAsistenciaPeriodoAcademicoId(50L, 10L))
                .thenReturn(Collections.emptyList());
        when(asignacionRepository.findByGrupoIdAndActivoTrue(30L)).thenReturn(Collections.emptyList());
        when(asignacionRepository.findByGrupoIdAndEsDirectorGrupoTrueAndActivoTrue(30L)).thenReturn(Optional.empty());

        EmisionDocumentoRequest req = new EmisionDocumentoRequest("A quien interese", "Boletín oficial primer periodo");
        BoletinPeriodoResponse res = documentoService.generarBoletinPeriodo(50L, 10L, req, usuario);

        assertNotNull(res);
        assertEquals("BOL-2026-00001", res.getNumeroConsecutivo());
        assertNotNull(res.getCodigoVerificacion());
        assertEquals("Juan Carlos Pérez López", res.getNombreEstudiante());
        assertEquals("1020304050", res.getNumeroDocumentoEstudiante());
        assertEquals(1, res.getAreas().size());
        assertEquals("Ciencias Naturales", res.getAreas().get(0).getNombreArea());
        assertEquals(new BigDecimal("4.2"), res.getAreas().get(0).getPromedioArea());
        assertEquals(1, res.getResumen().getAsignaturasAprobadas());
        assertEquals(0, res.getResumen().getAsignaturasReprobadas());
    }

    @Test
    @DisplayName("generarConstanciaMatricula debe emitir certificación formal con consecutivo")
    void generarConstanciaMatricula_Exitoso() {
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anioLectivo, TipoDocumentoAcademico.CONSTANCIA_MATRICULA, "CONST", 9L, 5, null
        );
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(1L, TipoDocumentoAcademico.CONSTANCIA_MATRICULA))
                .thenReturn(Optional.of(consecutivo));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));

        EmisionDocumentoRequest req = new EmisionDocumentoRequest("Fondo de Pensiones", "Solicitud de auxilio familiar");
        ConstanciaMatriculaResponse res = documentoService.generarConstanciaMatricula(50L, req, usuario);

        assertNotNull(res);
        assertEquals("CONST-2026-00010", res.getNumeroConsecutivo());
        assertNotNull(res.getCodigoVerificacion());
        assertEquals("MAT-2026-001", res.getCodigoMatricula());
        assertTrue(res.getTextoCertificacion().contains("JUAN CARLOS PÉREZ LÓPEZ"));
        assertTrue(res.getTextoCertificacion().contains("SEXTO"));
    }

    @Test
    @DisplayName("generarCertificadoEstudio debe calcular promedios anuales y estado de promoción")
    void generarCertificadoEstudio_Exitoso() {
        when(matriculaRepository.findById(50L)).thenReturn(Optional.of(matricula));

        ConsecutivoDocumento consecutivo = new ConsecutivoDocumento(
                anioLectivo, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO, "CERT", 0L, 5, null
        );
        when(consecutivoRepository.findByAnioLectivoIdAndTipoDocumentoConBloqueo(1L, TipoDocumentoAcademico.CERTIFICADO_ESTUDIO))
                .thenReturn(Optional.of(consecutivo));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));

        when(escalaSieeRepository.findByAnioLectivoId(1L)).thenReturn(Optional.empty());
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(20L)).thenReturn(List.of(gradoAsignatura));

        CalificacionPeriodo cp1 = new CalificacionPeriodo();
        cp1.setAsignatura(asignatura);
        cp1.setNotaDefinitiva(new BigDecimal("4.00"));

        CalificacionPeriodo cp2 = new CalificacionPeriodo();
        cp2.setAsignatura(asignatura);
        cp2.setNotaDefinitiva(new BigDecimal("4.40"));

        when(calificacionPeriodoRepository.findByMatriculaId(50L)).thenReturn(List.of(cp1, cp2));

        CertificadoEstudioResponse res = documentoService.generarCertificadoEstudio(50L, null, usuario);

        assertNotNull(res);
        assertEquals("CERT-2026-00001", res.getNumeroConsecutivo());
        assertEquals(1, res.getAsignaturas().size());
        assertEquals("APROBADA", res.getAsignaturas().get(0).getEstado());
        assertEquals(new BigDecimal("4.2"), res.getPromedioFinal());
        assertEquals("PROMOVIDO(A)", res.getResultadoPromocion());
    }

    @Test
    @DisplayName("anularEmision debe cambiar estado a anulado y lanzar excepción si ya estaba anulada")
    void anularEmision_ExitosoYConflicto() {
        RegistroEmisionDocumento emision = new RegistroEmisionDocumento();
        emision.setId(200L);
        emision.setAnulado(false);

        when(emisionRepository.findById(200L)).thenReturn(Optional.of(emision));
        when(emisionRepository.save(any(RegistroEmisionDocumento.class))).thenAnswer(inv -> inv.getArgument(0));

        AnulacionEmisionRequest req = new AnulacionEmisionRequest("Error en los datos del acudiente");
        RegistroEmisionResponse res = documentoService.anularEmision(200L, req, usuario);

        assertTrue(res.getAnulado());
        assertEquals("Error en los datos del acudiente", res.getMotivoAnulacion());

        // Intentar anular de nuevo -> Debe lanzar ConflictoNegocioException
        assertThrows(ConflictoNegocioException.class, () -> documentoService.anularEmision(200L, req, usuario));
    }
}
