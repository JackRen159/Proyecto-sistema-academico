package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.MatriculaMapper;
import com.jackren.sistemaacademico.repository.*;
import jakarta.persistence.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Independent Spec-Derived Probes - Modulo 04 V2 (S1-S4 Verification)")
public class IndependentVerifierModulo04ProbeTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private HistorialTrasladoMatriculaRepository historialTrasladoRepository;

    @Mock
    private HistorialNovedadMatriculaRepository historialNovedadRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private AcudienteRepository acudienteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionAcademicaRepository;

    @Mock
    private CalificacionActividadRepository calificacionActividadRepository;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Spy
    private MatriculaMapper matriculaMapper = new MatriculaMapper();

    @InjectMocks
    private MatriculaService matriculaService;

    private Estudiante estudiante;
    private Acudiente acudiente;
    private EstudianteAcudiente vinculo;
    private Grado grado6;
    private Grado grado7;
    private Grado grado8;
    private AnioLectivo anio;
    private Grupo grupo6A;
    private Grupo grupo6B;
    private Grupo grupo7A;
    private Grupo grupo8A;
    private Matricula matricula;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante();
        estudiante.setId(100L);
        estudiante.setNombres("Sofia");
        estudiante.setApellidos("Ramirez");
        estudiante.setActivo(true);
        estudiante.setEstadoExpediente(EstadoExpediente.LISTO_PARA_MATRICULA);

        acudiente = new Acudiente();
        acudiente.setId(200L);
        acudiente.setNombres("Carlos");
        acudiente.setApellidos("Ramirez");
        acudiente.setActivo(true);

        vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEsPrincipal(true);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        grado6 = new Grado("06", "Sexto", NivelEducativo.BASICA_SECUNDARIA, 6);
        grado6.setId(60L);

        grado7 = new Grado("07", "Séptimo", NivelEducativo.BASICA_SECUNDARIA, 7);
        grado7.setId(70L);

        grado8 = new Grado("08", "Octavo", NivelEducativo.BASICA_SECUNDARIA, 8);
        grado8.setId(80L);

        grado6.setGradoSiguiente(grado7);
        grado7.setGradoSiguiente(grado8);

        anio = new AnioLectivo(2026, "2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio.setId(1L);
        anio.setEstado(EstadoAnioLectivo.ACTIVO);

        grupo6A = new Grupo(anio, grado6, "6-A", Jornada.MANANA, 30);
        grupo6A.setId(601L);

        grupo6B = new Grupo(anio, grado6, "6-B", Jornada.MANANA, 30);
        grupo6B.setId(602L);

        grupo7A = new Grupo(anio, grado7, "7-A", Jornada.MANANA, 30);
        grupo7A.setId(701L);

        grupo8A = new Grupo(anio, grado8, "8-A", Jornada.MANANA, 30);
        grupo8A.setId(801L);

        matricula = new Matricula("MAT-2026-9999", estudiante, grupo6A, anio);
        matricula.setId(900L);
        matricula.setAcudientePrincipal(acudiente);
        matricula.setEstado(EstadoMatricula.MATRICULADA);
        matricula.setFechaEfectivaInicio(LocalDate.of(2026, 2, 1));
    }

    @Test
    @DisplayName("PROBE-S2-01: Verificación de concurrencia optimista @Version en entidad Matricula (S2)")
    void probeS2_01_matriculaTieneAnotacionVersion() throws NoSuchFieldException {
        Field versionField = Matricula.class.getDeclaredField("version");
        assertNotNull(versionField.getAnnotation(Version.class), "El campo version debe estar anotado con @Version para control de concurrencia optimista (RN-11)");
        assertEquals(0L, matricula.getVersion(), "El valor inicial de version debe ser 0");
    }

    @Test
    @DisplayName("PROBE-S2-02: Rechazo con HTTP 409 si el grupo ha copado exactamente su cupoMaximo (S2)")
    void probeS2_02_rechazoCupoMaximoExacto() {
        when(estudianteRepository.findById(100L)).thenReturn(Optional.of(estudiante));
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)).thenReturn(Optional.of(vinculo));
        when(grupoRepository.findById(601L)).thenReturn(Optional.of(grupo6A));
        when(matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(100L, 1L, EstadoMatricula.MATRICULADA)).thenReturn(false);
        when(matriculaRepository.countByGrupoIdAndEstado(601L, EstadoMatricula.MATRICULADA)).thenReturn(30L);

        MatriculaRequest req = new MatriculaRequest();
        req.setEstudianteId(100L);
        req.setGrupoId(601L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> matriculaService.matricular(req));
        assertTrue(ex.getMessage().contains("cupo máximo de 30"), "Debe indicar el cupo máximo exacto alcanzado");
    }

    @Test
    @DisplayName("PROBE-S2-03: Rechazo si el expediente se encuentra en estado BORRADOR_PENDIENTE (S2)")
    void probeS2_03_rechazoExpedienteNoListo() {
        estudiante.setEstadoExpediente(EstadoExpediente.BORRADOR_PENDIENTE);
        when(estudianteRepository.findById(100L)).thenReturn(Optional.of(estudiante));

        MatriculaRequest req = new MatriculaRequest();
        req.setEstudianteId(100L);
        req.setGrupoId(601L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> matriculaService.matricular(req));
        assertTrue(ex.getMessage().contains("LISTO_PARA_MATRICULA"), "Debe exigir estado LISTO_PARA_MATRICULA");
    }

    @Test
    @DisplayName("PROBE-S3-01: Rechazo de traslado interno si el grupo destino pertenece a otro grado sin promoción (S3)")
    void probeS3_01_rechazoTrasladoEntreGradosDistintos() {
        when(matriculaRepository.findById(900L)).thenReturn(Optional.of(matricula));
        when(grupoRepository.findById(701L)).thenReturn(Optional.of(grupo7A));

        TrasladoGrupoRequest req = new TrasladoGrupoRequest();
        req.setNuevoGrupoId(701L);
        req.setMotivo("Cambio incorrecto de grado por traslado");

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.trasladarDeGrupo(900L, req, 1L));
        assertTrue(ex.getMessage().contains("mismo grado"), "Debe rechazar traslado entre grados distintos (RN-MAT-06)");
    }

    @Test
    @DisplayName("PROBE-S3-02: Rechazo de promoción anticipada si se intenta saltar más de un grado (S3)")
    void probeS3_02_rechazoPromocionAnticipadaSaltandoGrados() {
        when(matriculaRepository.findById(900L)).thenReturn(Optional.of(matricula));
        when(grupoRepository.findById(801L)).thenReturn(Optional.of(grupo8A));

        NovedadMatriculaRequest req = new NovedadMatriculaRequest();
        req.setTipoNovedad(TipoNovedadMatricula.PROMOCION_ANTICIPADA);
        req.setNuevoGrupoId(801L);
        req.setNumeroActaResolucion("ACTA-001");
        req.setConsentimientoAcudiente(true);
        req.setMotivo("Intento de salto de 2 grados");

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.promocionAnticipada(900L, req, 1L));
        assertTrue(ex.getMessage().contains("inmediatamente siguiente"), "Debe exigir grado inmediatamente siguiente");
    }

    @Test
    @DisplayName("PROBE-S3-03: Bloqueo inmutable de anulación administrativa si existen notas parciales registradas (S3)")
    void probeS3_03_bloqueoInmutableAnulacionConNotas() {
        when(matriculaRepository.findById(900L)).thenReturn(Optional.of(matricula));
        when(calificacionActividadRepository.existsByMatriculaId(900L)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.anularMatricula(900L, "Anulación no procedente", 1L));
        assertTrue(ex.getMessage().contains("ya registra calificaciones causadas"));
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("PROBE-S4-01: Listado oficial del grupo mapea activos y retirados con fechaEfectivaRetiro (S4)")
    void probeS4_01_listadoOficialDiferenciaActivosYRetirados() {
        Matricula mRetirada = new Matricula("MAT-2026-9002", estudiante, grupo6A, anio);
        mRetirada.setId(901L);
        mRetirada.setEstado(EstadoMatricula.RETIRADO_VOLUNTARIO);
        mRetirada.setFechaEfectivaRetiro(LocalDate.of(2026, 4, 15));

        when(matriculaRepository.findListadoOficialPorGrupo(601L)).thenReturn(List.of(matricula, mRetirada));

        List<MatriculaResponse> listado = matriculaService.listarListadoOficialGrupo(601L);

        assertEquals(2, listado.size());
        assertEquals(EstadoMatricula.MATRICULADA, listado.get(0).getEstado());
        assertNull(listado.get(0).getFechaEfectivaRetiro());
        assertEquals(EstadoMatricula.RETIRADO_VOLUNTARIO, listado.get(1).getEstado());
        assertEquals(LocalDate.of(2026, 4, 15), listado.get(1).getFechaEfectivaRetiro());
    }

    @Test
    @DisplayName("PROBE-S4-02: Acudiente solo visualiza matrículas de estudiantes con vínculo VERIFICADO (S4)")
    void probeS4_02_acudienteSoloVeEstudiantesVinculadosVerificados() {
        Usuario usuarioAcudiente = new Usuario("acudienteCarlos", "hash", Rol.ACUDIENTE);
        usuarioAcudiente.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);
        usuarioAcudiente.setPersonaId(200L);

        when(usuarioRepository.findByUsername("acudienteCarlos")).thenReturn(Optional.of(usuarioAcudiente));
        when(acudienteRepository.findById(200L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByAcudienteAndEstadoVinculo(acudiente, EstadoVinculo.VERIFICADO))
                .thenReturn(List.of(vinculo));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(100L))
                .thenReturn(List.of(matricula));

        List<MatriculaResponse> respuestas = matriculaService.listarMatriculasMisEstudiantes("acudienteCarlos");

        assertEquals(1, respuestas.size());
        assertEquals("MAT-2026-9999", respuestas.getFirst().getNumeroMatricula());
    }

    @Test
    @DisplayName("PROBE-S4-03: Comprobación contextual de matrícula activa en grupo (S4)")
    void probeS4_03_verificacionContextualActivaEnGrupo() {
        when(matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(100L, 601L, EstadoMatricula.MATRICULADA))
                .thenReturn(true);
        when(matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(100L, 602L, EstadoMatricula.MATRICULADA))
                .thenReturn(false);

        assertTrue(matriculaService.tieneMatriculaActivaEnGrupo(100L, 601L));
        assertFalse(matriculaService.tieneMatriculaActivaEnGrupo(100L, 602L));
        assertFalse(matriculaService.tieneMatriculaActivaEnGrupo(null, 601L));
    }
}
