package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.MatriculaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Módulo 04 V2 (Matrículas y Novedades: A# y E#)")
class FrenteBrechasModulo04ServiceTest {

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

    private Estudiante estudianteMenor;
    private Acudiente acudienteValido;
    private EstudianteAcudiente vinculoPrincipal;
    private Grado gradoSeptimo;
    private Grado gradoOctavo;
    private AnioLectivo anio2026;
    private Grupo grupo7A;
    private Grupo grupo7B;
    private Grupo grupo8A;
    private Matricula matriculaActiva;

    @BeforeEach
    void setUp() {
        estudianteMenor = new Estudiante();
        estudianteMenor.setId(10L);
        estudianteMenor.setNombres("Mateo");
        estudianteMenor.setApellidos("Gómez");
        estudianteMenor.setDocumento("1020304050");
        estudianteMenor.setActivo(true);
        estudianteMenor.setEstadoExpediente(EstadoExpediente.LISTO_PARA_MATRICULA);

        acudienteValido = new Acudiente();
        acudienteValido.setId(20L);
        acudienteValido.setNombres("Claudia");
        acudienteValido.setApellidos("Pérez");
        acudienteValido.setDocumento("52987654");
        acudienteValido.setActivo(true);

        vinculoPrincipal = new EstudianteAcudiente();
        vinculoPrincipal.setEstudiante(estudianteMenor);
        vinculoPrincipal.setAcudiente(acudienteValido);
        vinculoPrincipal.setParentesco(Parentesco.MADRE);
        vinculoPrincipal.setEsPrincipal(true);
        vinculoPrincipal.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        gradoSeptimo = new Grado("07", "Séptimo", NivelEducativo.BASICA_SECUNDARIA, 7);
        gradoSeptimo.setId(70L);

        gradoOctavo = new Grado("08", "Octavo", NivelEducativo.BASICA_SECUNDARIA, 8);
        gradoOctavo.setId(80L);
        gradoSeptimo.setGradoSiguiente(gradoOctavo);

        anio2026 = new AnioLectivo(2026, "Año Escolar 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio2026.setId(1L);
        anio2026.setEstado(EstadoAnioLectivo.ACTIVO);

        grupo7A = new Grupo(anio2026, gradoSeptimo, "7-A", Jornada.MANANA, 35);
        grupo7A.setId(101L);

        grupo7B = new Grupo(anio2026, gradoSeptimo, "7-B", Jornada.MANANA, 35);
        grupo7B.setId(102L);

        grupo8A = new Grupo(anio2026, gradoOctavo, "8-A", Jornada.MANANA, 35);
        grupo8A.setId(103L);

        matriculaActiva = new Matricula("MAT-2026-0001", estudianteMenor, grupo7A, anio2026);
        matriculaActiva.setId(500L);
        matriculaActiva.setAcudientePrincipal(acudienteValido);
        matriculaActiva.setEstado(EstadoMatricula.MATRICULADA);
        matriculaActiva.setFechaEfectivaInicio(LocalDate.of(2026, 1, 20));
    }

    // =========================================================================
    // UC-13: Formalizar matrícula con expediente validado (E2, E3, E4, E1, A1, A5)
    // =========================================================================

    @Test
    @DisplayName("UC-13 E2: Debe rechazar matrícula si el expediente no está en estado LISTO_PARA_MATRICULA")
    void uc13_E2_debeRechazarExpedienteIncompleto() {
        estudianteMenor.setEstadoExpediente(EstadoExpediente.BORRADOR_PENDIENTE);
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudianteMenor));

        MatriculaRequest request = new MatriculaRequest();
        request.setEstudianteId(10L);
        request.setGrupoId(101L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.matricular(request));

        assertTrue(ex.getMessage().contains("LISTO_PARA_MATRICULA"));
    }

    @Test
    @DisplayName("UC-13 E3: Debe rechazar matrícula si el estudiante menor no cuenta con acudiente principal verificado (RN-EST-05)")
    void uc13_E3_debeRechazarMenorSinAcudientePrincipal() {
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudianteMenor));
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudianteMenor)).thenReturn(Optional.empty());

        MatriculaRequest request = new MatriculaRequest();
        request.setEstudianteId(10L);
        request.setGrupoId(101L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.matricular(request));

        assertTrue(ex.getMessage().contains("no cuenta con un acudiente principal activo"));
    }

    @Test
    @DisplayName("UC-13 E4: Debe rechazar matrícula si el acudiente principal registra medida de restricción legal vigente (RN-EST-02)")
    void uc13_E4_debeRechazarAcudienteConRestriccionLegal() {
        vinculoPrincipal.setMotivoBloqueoLegal("Orden judicial de alejamiento Juzgado 4 de Familia");
        vinculoPrincipal.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudianteMenor));
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudianteMenor)).thenReturn(Optional.of(vinculoPrincipal));

        MatriculaRequest request = new MatriculaRequest();
        request.setEstudianteId(10L);
        request.setGrupoId(101L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.matricular(request));

        assertTrue(ex.getMessage().contains("restricción legal vigente"));
    }

    @Test
    @DisplayName("UC-13 A5: Formalización exitosa por traslado entrante con fecha efectiva e institución de procedencia")
    void uc13_A5_debeFormalizarMatriculaTrasladoEntrante() {
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudianteMenor));
        when(estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudianteMenor)).thenReturn(Optional.of(vinculoPrincipal));
        when(grupoRepository.findById(101L)).thenReturn(Optional.of(grupo7A));
        when(matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(10L, 1L, EstadoMatricula.MATRICULADA)).thenReturn(false);
        when(matriculaRepository.countByGrupoIdAndEstado(101L, EstadoMatricula.MATRICULADA)).thenReturn(20L);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(i -> {
            Matricula m = i.getArgument(0);
            m.setId(777L);
            return m;
        });

        MatriculaRequest request = new MatriculaRequest();
        request.setEstudianteId(10L);
        request.setGrupoId(101L);
        request.setTipoMatricula(TipoMatricula.TRASLADO_ENTRANTE);
        request.setInstitucionProcedencia("Colegio Departamental de Facatativá");
        request.setFechaEfectivaInicio(LocalDate.of(2026, 3, 1));

        MatriculaResponse response = matriculaService.matricular(request);

        assertNotNull(response);
        assertEquals(777L, response.getId());
        assertEquals("Colegio Departamental de Facatativá", response.getInstitucionProcedencia());
        assertEquals(LocalDate.of(2026, 3, 1), response.getFechaEfectivaInicio());
        assertEquals(EstadoExpediente.MATRICULADO, estudianteMenor.getEstadoExpediente());
        verify(auditoriaService).registrarEvento(eq("CREACION_MATRICULA"), any(), any(), any(), any(), any(), any());
    }

    // =========================================================================
    // UC-PROV-09: Novedades de matrícula (A1, A2, A3, A4, A5, E1, E2, E4)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-09 A1: Cambio de grupo interno en el grado con registro en historial de novedades (RN-MAT-06)")
    void ucProv09_A1_debeTrasladarGrupoInternoConHistorialNovedad() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));
        when(grupoRepository.findById(102L)).thenReturn(Optional.of(grupo7B));
        when(matriculaRepository.countByGrupoIdAndEstado(102L, EstadoMatricula.MATRICULADA)).thenReturn(10L);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));

        TrasladoGrupoRequest request = new TrasladoGrupoRequest();
        request.setNuevoGrupoId(102L);
        request.setMotivo("Solicitud de acudiente por jornada laboral");

        MatriculaResponse response = matriculaService.trasladarDeGrupo(500L, request, 99L);

        assertNotNull(response);
        assertEquals("7-B", response.getGrupoNombre());
        verify(historialNovedadRepository).save(any(HistorialNovedadMatricula.class));
        verify(auditoriaService).registrarEvento(eq("TRASLADO_GRUPO_MATRICULA"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-PROV-09 A2: Promoción Anticipada de grado con acta de Consejo Directivo y consentimiento (RN-MAT-07)")
    void ucProv09_A2_debeEjecutarPromocionAnticipadaDeGradoExitosamente() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));
        when(grupoRepository.findById(103L)).thenReturn(Optional.of(grupo8A));
        when(matriculaRepository.countByGrupoIdAndEstado(103L, EstadoMatricula.MATRICULADA)).thenReturn(25L);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));

        NovedadMatriculaRequest request = new NovedadMatriculaRequest();
        request.setTipoNovedad(TipoNovedadMatricula.PROMOCION_ANTICIPADA);
        request.setNuevoGrupoId(103L);
        request.setNumeroActaResolucion("ACTA-CD-008-2026");
        request.setFechaActaResolucion(LocalDate.of(2026, 3, 20));
        request.setConsentimientoAcudiente(true);
        request.setMotivo("Rendimiento superior en todas las áreas durante el primer periodo");

        MatriculaResponse response = matriculaService.promocionAnticipada(500L, request, 99L);

        assertNotNull(response);
        assertEquals("8-A", response.getGrupoNombre());
        assertEquals("Octavo", response.getGradoNombre());
        assertEquals("ACTA-CD-008-2026", response.getActaConsejoDirectivo());
        verify(historialNovedadRepository).save(any(HistorialNovedadMatricula.class));
        verify(auditoriaService).registrarEvento(eq("PROMOCION_ANTICIPADA_MATRICULA"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-PROV-09 E2: Debe rechazar Promoción Anticipada si falta el acta del Consejo Directivo o el consentimiento")
    void ucProv09_E2_debeRechazarPromocionAnticipadaSinActaOConsentimiento() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));

        NovedadMatriculaRequest requestSinActa = new NovedadMatriculaRequest();
        requestSinActa.setNuevoGrupoId(103L);
        requestSinActa.setConsentimientoAcudiente(true);
        requestSinActa.setMotivo("Sin acta oficial");

        ConflictoNegocioException ex1 = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.promocionAnticipada(500L, requestSinActa, 99L));
        assertTrue(ex1.getMessage().contains("acta del Consejo Directivo"));

        NovedadMatriculaRequest requestSinConsentimiento = new NovedadMatriculaRequest();
        requestSinConsentimiento.setNuevoGrupoId(103L);
        requestSinConsentimiento.setNumeroActaResolucion("ACTA-01");
        requestSinConsentimiento.setConsentimientoAcudiente(false);
        requestSinConsentimiento.setMotivo("Sin consentimiento del acudiente");

        ConflictoNegocioException ex2 = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.promocionAnticipada(500L, requestSinConsentimiento, 99L));
        assertTrue(ex2.getMessage().contains("consentimiento expreso del acudiente principal"));
    }

    @Test
    @DisplayName("UC-PROV-09 A3: Retiro voluntario formalizando fecha efectiva de corte e historial de novedad (RN-MAT-08)")
    void ucProv09_A3_debeRetirarEstudianteVoluntarioExitosamente() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));

        NovedadMatriculaRequest request = new NovedadMatriculaRequest();
        request.setTipoNovedad(TipoNovedadMatricula.RETIRO_VOLUNTARIO);
        request.setFechaEfectiva(LocalDate.of(2026, 4, 30));
        request.setMotivo("Traslado de residencia familiar a otra ciudad");
        request.setColegioDestino("Colegio Distrital de Barranquilla");

        MatriculaResponse response = matriculaService.retirarEstudiante(500L, request, 99L);

        assertNotNull(response);
        assertEquals(EstadoMatricula.RETIRADO_VOLUNTARIO, response.getEstado());
        assertEquals(LocalDate.of(2026, 4, 30), response.getFechaEfectivaRetiro());
        verify(historialNovedadRepository).save(any(HistorialNovedadMatricula.class));
        verify(auditoriaService).registrarEvento(eq("RETIRO_MATRICULA"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-PROV-09 A5: Anulación administrativa exitosa de matrícula creada por error sin notas causadas")
    void ucProv09_A5_debeAnularMatriculaSinNotas() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));
        when(calificacionActividadRepository.existsByMatriculaId(500L)).thenReturn(false);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(i -> i.getArgument(0));

        MatriculaResponse response = matriculaService.anularMatricula(500L, "Error en digitación de documento", 99L);

        assertNotNull(response);
        assertEquals(EstadoMatricula.ANULADA_ADMINISTRATIVAMENTE, response.getEstado());
        assertFalse(response.getActivo());
        verify(auditoriaService).registrarEvento(eq("ANULACION_ADMINISTRATIVA_MATRICULA"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-PROV-09 E4: Debe impedir anulación administrativa si el estudiante ya cuenta con notas causadas")
    void ucProv09_E4_debeImpedirAnulacionConNotas() {
        when(matriculaRepository.findById(500L)).thenReturn(Optional.of(matriculaActiva));
        when(calificacionActividadRepository.existsByMatriculaId(500L)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> matriculaService.anularMatricula(500L, "Intento de anulación", 99L));

        assertTrue(ex.getMessage().contains("ya registra calificaciones causadas"));
        verify(matriculaRepository, never()).save(any());
    }

    // =========================================================================
    // UC-PROV-10: Consultas contextuales por rol y listado oficial (A1, E1)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-10 A1: Listado oficial del grupo incluye estudiantes activos y retirados con fecha de corte")
    void ucProv10_A1_debeRetornarListadoOficialConActivosYRetirados() {
        Matricula retirado = new Matricula("MAT-2026-0002", estudianteMenor, grupo7A, anio2026);
        retirado.setId(501L);
        retirado.setEstado(EstadoMatricula.RETIRADO_VOLUNTARIO);
        retirado.setFechaEfectivaRetiro(LocalDate.of(2026, 3, 10));

        when(matriculaRepository.findListadoOficialPorGrupo(101L)).thenReturn(List.of(matriculaActiva, retirado));

        List<MatriculaResponse> listado = matriculaService.listarListadoOficialGrupo(101L);

        assertNotNull(listado);
        assertEquals(2, listado.size());
        assertEquals(EstadoMatricula.MATRICULADA, listado.get(0).getEstado());
        assertEquals(EstadoMatricula.RETIRADO_VOLUNTARIO, listado.get(1).getEstado());
        assertEquals(LocalDate.of(2026, 3, 10), listado.get(1).getFechaEfectivaRetiro());
    }

    @Test
    @DisplayName("UC-PROV-10 E1: Estudiante autenticado consulta exclusivamente su propio historial de matrículas")
    void ucProv10_E1_estudianteConsultaSuPropioHistorial() {
        Usuario usuarioEstudiante = new Usuario("estudiante10", "hash", Rol.ESTUDIANTE);
        usuarioEstudiante.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);
        usuarioEstudiante.setPersonaId(10L);

        when(usuarioRepository.findByUsername("estudiante10")).thenReturn(Optional.of(usuarioEstudiante));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(List.of(matriculaActiva));

        List<MatriculaResponse> historial = matriculaService.listarMiHistorial("estudiante10");

        assertNotNull(historial);
        assertEquals(1, historial.size());
        assertEquals("MAT-2026-0001", historial.getFirst().getNumeroMatricula());
    }
}
