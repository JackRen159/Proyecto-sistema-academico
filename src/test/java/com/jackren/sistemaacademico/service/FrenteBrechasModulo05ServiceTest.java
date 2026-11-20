package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.AsignacionAcademicaMapper;
import com.jackren.sistemaacademico.mapper.ProfesorMapper;
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
@DisplayName("Pruebas Unitarias - Módulo 05 V2 (Profesores y Asignaciones Docentes: A# y E#)")
class FrenteBrechasModulo05ServiceTest {

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Mock
    private GradoAsignaturaRepository gradoAsignaturaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuditoriaGlobalService auditoriaService;

    @Spy
    private ProfesorMapper profesorMapper = new ProfesorMapper();

    @Spy
    private AsignacionAcademicaMapper asignacionMapper = new AsignacionAcademicaMapper();

    @InjectMocks
    private ProfesorService profesorService;

    @InjectMocks
    private AsignacionAcademicaService asignacionService;

    private Profesor profesorActivo;
    private Profesor profesorSuplente;
    private Grado gradoQuinto;
    private Grupo grupo5A;
    private Asignatura asignaturaMatematicas;
    private Asignatura asignaturaFilosofia;
    private AnioLectivo anio2026;

    @BeforeEach
    void setUp() {
        profesorActivo = new Profesor();
        profesorActivo.setId(10L);
        profesorActivo.setNombres("Alberto");
        profesorActivo.setApellidos("Lleras");
        profesorActivo.setDocumento("79123456");
        profesorActivo.setCorreo("alberto.lleras@colegio.edu.co");
        profesorActivo.setActivo(true);
        profesorActivo.setEstadoAdministrativo(EstadoAdministrativoDocente.ACTIVO);
        profesorActivo.setHorasSemanalesOrdinarias(22);
        profesorActivo.setHorasExtrasPermitidas(4);
        profesorActivo.setVerificadoInhabilidades(true);

        profesorSuplente = new Profesor();
        profesorSuplente.setId(20L);
        profesorSuplente.setNombres("Camilo");
        profesorSuplente.setApellidos("Torres");
        profesorSuplente.setDocumento("80987654");
        profesorSuplente.setCorreo("camilo.torres@colegio.edu.co");
        profesorSuplente.setActivo(true);
        profesorSuplente.setEstadoAdministrativo(EstadoAdministrativoDocente.ACTIVO);
        profesorSuplente.setVerificadoInhabilidades(true);

        gradoQuinto = new Grado("05", "Quinto", NivelEducativo.BASICA_PRIMARIA, 5);
        gradoQuinto.setId(50L);

        anio2026 = new AnioLectivo(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio2026.setId(1L);
        anio2026.setEstado(EstadoAnioLectivo.ACTIVO);

        grupo5A = new Grupo(anio2026, gradoQuinto, "5-A", Jornada.MANANA, 35);
        grupo5A.setId(105L);

        asignaturaMatematicas = new Asignatura(null, "MAT5", "Matemáticas 5°", "Asignatura Primaria");
        asignaturaMatematicas.setId(301L);

        asignaturaFilosofia = new Asignatura(null, "FILO", "Filosofía Media", "Asignatura Media");
        asignaturaFilosofia.setId(302L);
    }

    // =========================================================================
    // UC-PROV-11: Expediente maestro e inhabilidades (E1, E2, A2, A3)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-11 E1: Debe rechazar alta de profesor si no cuenta con verificación de inhabilidades Ley 1918")
    void ucProv11_E1_debeRechazarDocenteSinVerificacionInhabilidades() {
        ProfesorRequest request = new ProfesorRequest();
        request.setDocumento("10203040");
        request.setNombres("Pedro");
        request.setApellidos("Navaja");
        request.setCorreo("pedro.navaja@colegio.edu.co");
        request.setVerificadoInhabilidades(false); // Inhabilidades no verificadas

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> profesorService.crear(request));

        assertTrue(ex.getMessage().contains("Ley 1918 de 2018"));
    }

    @Test
    @DisplayName("UC-PROV-11 E2: Debe bloquear inactivación de docente con cursos activos sin relevo previo")
    void ucProv11_E2_debeBloquearInactivacionConCursosActivos() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(asignacionRepository.existsByProfesorIdAndActivoTrue(10L)).thenReturn(true);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> profesorService.desactivar(10L));

        assertTrue(ex.getMessage().contains("cuenta con cursos o asignaciones académicas activas"));
    }

    @Test
    @DisplayName("UC-PROV-11 A2 y A3: Registro de situación de licencia/incapacidad y posterior reintegro")
    void ucProv11_A2_A3_debeRegistrarLicenciaYReintegro() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(profesorRepository.save(any(Profesor.class))).thenAnswer(i -> i.getArgument(0));

        // Registro de licencia médica
        ProfesorResponse respLicencia = profesorService.registrarSituacionAdministrativa(
                10L, EstadoAdministrativoDocente.EN_LICENCIA_O_INCAPACIDAD,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 4, 15), "Incapacidad por cirugía"
        );
        assertEquals(EstadoAdministrativoDocente.EN_LICENCIA_O_INCAPACIDAD, respLicencia.getEstadoAdministrativo());

        // Reintegro
        ProfesorResponse respReintegro = profesorService.registrarSituacionAdministrativa(
                10L, EstadoAdministrativoDocente.ACTIVO, null, null, "Alta médica y reintegro formal"
        );
        assertEquals(EstadoAdministrativoDocente.ACTIVO, respReintegro.getEstadoAdministrativo());
        assertTrue(respReintegro.getActivo());
    }

    // =========================================================================
    // UC-15: Asignaciones docentes y carga académica (E1, E2, E3, A1, A2, A3, A4)
    // =========================================================================

    @Test
    @DisplayName("UC-15 E1: Debe rechazar asignación a un docente en situación de licencia o incapacidad")
    void uc15_E1_debeRechazarAsignacionADocenteEnLicencia() {
        profesorActivo.setEstadoAdministrativo(EstadoAdministrativoDocente.EN_LICENCIA_O_INCAPACIDAD);
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));

        AsignacionAcademicaRequest req = new AsignacionAcademicaRequest();
        req.setProfesorId(10L);
        req.setGrupoId(105L);
        req.setAsignaturaId(301L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> asignacionService.crear(req));

        assertTrue(ex.getMessage().contains("EN_LICENCIA_O_INCAPACIDAD"));
    }

    @Test
    @DisplayName("UC-15 E3: Debe rechazar asignación si la materia no pertenece al Plan de Estudios del grado (RN-PRO-04)")
    void uc15_E3_debeRechazarAsignaturaAjenaAlPlanDeEstudios() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));
        when(asignaturaRepository.findById(302L)).thenReturn(Optional.of(asignaturaFilosofia));
        // Filosofía NO está en 5° de Primaria
        when(gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(50L, 302L)).thenReturn(false);

        AsignacionAcademicaRequest req = new AsignacionAcademicaRequest();
        req.setProfesorId(10L);
        req.setGrupoId(105L);
        req.setAsignaturaId(302L);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> asignacionService.crear(req));

        assertTrue(ex.getMessage().contains("no pertenece al Plan de Estudios del grado Quinto"));
    }

    @Test
    @DisplayName("UC-15 E2: Debe rechazar asignación si supera el tope máximo de horas sin acta de horas extras (RN-PRO-03)")
    void uc15_E2_debeRechazarSuperacionDeTopeHorario() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));
        when(asignaturaRepository.findById(301L)).thenReturn(Optional.of(asignaturaMatematicas));
        when(gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(50L, 301L)).thenReturn(true);
        // El profesor ya tiene 24 horas asignadas (tope ordinario 22 + extras 4 = 26)
        when(asignacionRepository.sumHorasAsignadasPorProfesor(10L)).thenReturn(24);

        // Intento asignarle 4 horas más (24 + 4 = 28 > 26)
        AsignacionAcademicaRequest req = new AsignacionAcademicaRequest();
        req.setProfesorId(10L);
        req.setGrupoId(105L);
        req.setAsignaturaId(301L);
        req.setIntensidadHorariaSemanal(4);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> asignacionService.crear(req));

        assertTrue(ex.getMessage().contains("supera el tope máximo de carga horaria"));
    }

    @Test
    @DisplayName("UC-15 A4: Debe permitir asignación que supera tope ordinario cuando cuenta con acta rectoral de horas extras")
    void uc15_A4_debePermitirHorasExtrasConActaRectoral() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));
        when(asignaturaRepository.findById(301L)).thenReturn(Optional.of(asignaturaMatematicas));
        when(gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(50L, 301L)).thenReturn(true);
        when(asignacionRepository.existsByGrupoIdAndAsignaturaId(105L, 301L)).thenReturn(false);
        // Horas actuales: 20. Nuevas: 5 -> Total: 25 (> 22 ordinarias pero <= 26 con extras)
        when(asignacionRepository.sumHorasAsignadasPorProfesor(10L)).thenReturn(20);
        when(asignacionRepository.save(any(AsignacionAcademica.class))).thenAnswer(i -> {
            AsignacionAcademica a = i.getArgument(0);
            a.setId(901L);
            return a;
        });

        AsignacionAcademicaRequest req = new AsignacionAcademicaRequest();
        req.setProfesorId(10L);
        req.setGrupoId(105L);
        req.setAsignaturaId(301L);
        req.setIntensidadHorariaSemanal(5);
        req.setActaRectoralHorasExtras("RESOLUCION-EXTRAS-004-2026");

        AsignacionAcademicaResponse response = asignacionService.crear(req);

        assertNotNull(response);
        assertEquals(901L, response.getId());
        assertEquals("RESOLUCION-EXTRAS-004-2026", response.getActaRectoralHorasExtras());
    }

    @Test
    @DisplayName("UC-15 A3: Relevo docente temporal por licencia médica transfiriendo titularidad operativa (RN-PRO-06)")
    void uc15_A3_debeEjecutarRelevoDocenteTemporalExitosamente() {
        AsignacionAcademica asigOriginal = new AsignacionAcademica(profesorActivo, grupo5A, asignaturaMatematicas, 5);
        asigOriginal.setId(700L);
        asigOriginal.setEstadoAsignacion(EstadoAsignacionAcademica.TITULAR);

        when(asignacionRepository.findById(700L)).thenReturn(Optional.of(asigOriginal));
        when(profesorRepository.findById(20L)).thenReturn(Optional.of(profesorSuplente));
        when(asignacionRepository.save(any(AsignacionAcademica.class))).thenAnswer(i -> {
            AsignacionAcademica a = i.getArgument(0);
            if (a.getId() == null) a.setId(701L);
            return a;
        });

        RelevoDocenteRequest relevoReq = new RelevoDocenteRequest();
        relevoReq.setDocenteReemplazoId(20L);
        relevoReq.setTipoRelevo(EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL);
        relevoReq.setFechaInicioVigencia(LocalDate.of(2026, 3, 1));
        relevoReq.setFechaFinVigencia(LocalDate.of(2026, 4, 15));
        relevoReq.setMotivo("Incapacidad médica del titular");

        AsignacionAcademicaResponse response = asignacionService.relevarDocente(700L, relevoReq, 99L);

        assertNotNull(response);
        assertEquals(701L, response.getId());
        assertEquals(EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL, response.getEstadoAsignacion());
        assertTrue(response.getEsReemplazoTemporal());
        assertEquals(10L, response.getDocenteTitularRelevadoId());
        assertEquals(EstadoAsignacionAcademica.SUSPENDIDA_POR_LICENCIA, asigOriginal.getEstadoAsignacion());
        verify(auditoriaService).registrarEvento(eq("RELEVO_DOCENTE"), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("UC-15 A2: Asignación masiva por bloque curricular para modelo unidocente en Primaria")
    void uc15_A2_debeAsignarBloqueCurricularExitosamente() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));

        GradoAsignatura ga1 = new GradoAsignatura(gradoQuinto, asignaturaMatematicas, 5, false);
        ga1.setId(801L);
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(50L)).thenReturn(List.of(ga1));
        when(asignacionRepository.existsByGrupoIdAndAsignaturaId(105L, 301L)).thenReturn(false);
        when(asignacionRepository.save(any(AsignacionAcademica.class))).thenAnswer(i -> {
            AsignacionAcademica a = i.getArgument(0);
            a.setId(888L);
            return a;
        });

        BloqueCurricularAsignacionRequest request = new BloqueCurricularAsignacionRequest();
        request.setProfesorId(10L);
        request.setGrupoId(105L);
        request.setEsDirectorGrupo(true);

        List<AsignacionAcademicaResponse> asignadas = asignacionService.asignarBloqueCurricular(request);

        assertNotNull(asignadas);
        assertEquals(1, asignadas.size());
        assertEquals(888L, asignadas.getFirst().getId());
        assertTrue(asignadas.getFirst().getEsDirectorGrupo());
        verify(auditoriaService).registrarEvento(eq("ASIGNACION_BLOQUE_CURRICULAR"), any(), any(), any(), any(), any(), any());
    }

    // =========================================================================
    // UC-PROV-12: Consultas contextuales y cobertura (A1, A2)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-12 A1: Matriz directiva de cobertura identifica asignaturas vacantes en el curso")
    void ucProv12_A1_debeIdentificarMateriasVacantes() {
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));

        GradoAsignatura ga1 = new GradoAsignatura(gradoQuinto, asignaturaMatematicas, 5, false);
        ga1.setId(801L);
        when(gradoAsignaturaRepository.findByGradoIdAndActivoTrue(50L)).thenReturn(List.of(ga1));

        // El grupo NO tiene docente asignado aún
        when(asignacionRepository.findByGrupoIdAndActivoTrue(105L)).thenReturn(List.of());

        List<MateriaVacanteResponse> vacantes = asignacionService.obtenerMatrizVacantes(1L, 105L);

        assertNotNull(vacantes);
        assertEquals(1, vacantes.size());
        assertFalse(vacantes.getFirst().getTieneDocenteAsignado());
        assertEquals("VACANTE", vacantes.getFirst().getEstadoAsignacion());
    }

    @Test
    @DisplayName("UC-PROV-12 A2: Cuadro docente del curso expone profesores minimizando datos privados")
    void ucProv12_A2_debeExponerEquipoDocenteConDatosMinimizados() {
        AsignacionAcademica asig = new AsignacionAcademica(profesorActivo, grupo5A, asignaturaMatematicas, 5);
        asig.setId(750L);
        when(asignacionRepository.findByGrupoIdAndActivoTrue(105L)).thenReturn(List.of(asig));

        List<AsignacionAcademicaResponse> equipo = asignacionService.obtenerEquipoDocenteGrupo(105L);

        assertNotNull(equipo);
        assertEquals(1, equipo.size());
        assertEquals("Alberto Lleras", equipo.getFirst().getProfesorNombreCompleto());
        // Se minimiza documento de identidad para proteger privacidad frente a familias (RN-09)
        assertNull(equipo.getFirst().getProfesorDocumento());
    }

    // =========================================================================
    // Sondas Independientes de Regresión (S2, S3)
    // =========================================================================

    @Test
    @DisplayName("Sonda S2 (Límite): Carga horaria exactamente igual al tope máximo permitido debe ser aprobada sin acta")
    void uc15_sonda_limiteExactoTopeMaximoDebeAprobarse() {
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo)); // Ordinarias: 22, Extras: 4 -> Tope: 26
        when(grupoRepository.findById(105L)).thenReturn(Optional.of(grupo5A));
        when(asignaturaRepository.findById(301L)).thenReturn(Optional.of(asignaturaMatematicas));
        when(gradoAsignaturaRepository.existsByGradoIdAndAsignaturaId(50L, 301L)).thenReturn(true);
        when(asignacionRepository.existsByGrupoIdAndAsignaturaId(105L, 301L)).thenReturn(false);
        when(asignacionRepository.sumHorasAsignadasPorProfesor(10L)).thenReturn(22);
        when(asignacionRepository.save(any(AsignacionAcademica.class))).thenAnswer(i -> {
            AsignacionAcademica a = i.getArgument(0);
            a.setId(905L);
            return a;
        });

        // Intento asignar 4 horas (22 + 4 = 26 == topeMaximo 26)
        AsignacionAcademicaRequest req = new AsignacionAcademicaRequest();
        req.setProfesorId(10L);
        req.setGrupoId(105L);
        req.setAsignaturaId(301L);
        req.setIntensidadHorariaSemanal(4);

        assertDoesNotThrow(() -> asignacionService.crear(req));
    }

    @Test
    @DisplayName("Sonda S2 (Relevo): Debe rechazar relevo docente si el docente suplente es el mismo titular")
    void uc15_sonda_relevoMismoDocenteDebeFallar() {
        AsignacionAcademica asig = new AsignacionAcademica(profesorActivo, grupo5A, asignaturaMatematicas, 4);
        asig.setId(710L);
        when(asignacionRepository.findById(710L)).thenReturn(Optional.of(asig));
        when(profesorRepository.findById(10L)).thenReturn(Optional.of(profesorActivo));

        RelevoDocenteRequest req = new RelevoDocenteRequest();
        req.setDocenteReemplazoId(10L); // Mismo profesor
        req.setTipoRelevo(EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL);
        req.setMotivo("Prueba relevo inválido");

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> asignacionService.relevarDocente(710L, req, 1L));
        assertTrue(ex.getMessage().contains("no puede ser el mismo docente titular actual"));
    }

    @Test
    @DisplayName("Sonda S3 (Inhabilidades): Debe rechazar alta si verificadoInhabilidades es null")
    void ucProv11_sonda_altaDocenteInhabilidadesNullDebeFallar() {
        ProfesorRequest request = new ProfesorRequest();
        request.setDocumento("99887766");
        request.setNombres("Carlos");
        request.setApellidos("Pérez");
        request.setCorreo("carlos.perez@colegio.edu.co");
        request.setVerificadoInhabilidades(null);

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> profesorService.crear(request));
        assertTrue(ex.getMessage().contains("Ley 1918 de 2018"));
    }
}
