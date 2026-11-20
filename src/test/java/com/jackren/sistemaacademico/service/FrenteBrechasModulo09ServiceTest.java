package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.mapper.HorarioMapper;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias Exhaustivas - Módulo 09: Horarios Escolares V2 al 100%")
class FrenteBrechasModulo09ServiceTest {

    @Mock
    private AulaRepository aulaRepository;

    @Mock
    private PlantillaFranjaHorariaRepository franjaRepository;

    @Mock
    private HorarioGrupoVersionRepository versionRepository;

    @Mock
    private HorarioBloqueRepository bloqueRepository;

    @Mock
    private NovedadHorarioRepository novedadRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private EstudianteAcudienteRepository estudianteAcudienteRepository;

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private DiaNoLectivoRepository diaNoLectivoRepository;

    @Mock
    private GradoAsignaturaRepository gradoAsignaturaRepository;

    private HorarioMapper horarioMapper;
    private ValidadorColisionesHorarioService validadorColisionesService;
    private HorarioService horarioService;
    private NovedadHorarioService novedadHorarioService;

    private AnioLectivo anio;
    private Grado grado;
    private Grupo grupo;
    private Profesor profesor1;
    private Profesor profesor2;
    private Asignatura asignaturaMat;
    private Asignatura asignaturaFis;
    private AsignacionAcademica asignacion1;
    private AsignacionAcademica asignacion2;
    private Aula aula101;
    private Aula aulaLab;
    private PlantillaFranjaHoraria franja1;
    private HorarioGrupoVersion versionBorrador;

    @BeforeEach
    void setUp() {
        horarioMapper = new HorarioMapper();

        validadorColisionesService = new ValidadorColisionesHorarioService(
                bloqueRepository, asignacionRepository, aulaRepository,
                franjaRepository, gradoAsignaturaRepository, matriculaRepository
        );

        horarioService = new HorarioService(
                aulaRepository, franjaRepository, versionRepository, bloqueRepository,
                novedadRepository, grupoRepository, asignacionRepository, anioLectivoRepository,
                matriculaRepository, estudianteRepository, estudianteAcudienteRepository,
                profesorRepository, usuarioRepository, diaNoLectivoRepository,
                validadorColisionesService, horarioMapper
        );

        novedadHorarioService = new NovedadHorarioService(
                novedadRepository, bloqueRepository, aulaRepository,
                profesorRepository, usuarioRepository, horarioMapper
        );

        anio = new AnioLectivo();
        anio.setId(1L);
        anio.setAnio(2026);
        anio.setEstado(EstadoAnioLectivo.ACTIVO);

        grado = new Grado();
        grado.setId(5L);
        grado.setNombre("Noveno");
        grado.setNivel(NivelEducativo.BASICA_SECUNDARIA);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("9-A");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anio);

        profesor1 = new Profesor();
        profesor1.setId(30L);
        profesor1.setNombres("Carl");
        profesor1.setApellidos("Gauss");
        profesor1.setActivo(true);

        profesor2 = new Profesor();
        profesor2.setId(31L);
        profesor2.setNombres("Max");
        profesor2.setApellidos("Planck");
        profesor2.setActivo(true);

        asignaturaMat = new Asignatura();
        asignaturaMat.setId(50L);
        asignaturaMat.setNombre("Matemáticas");

        asignaturaFis = new Asignatura();
        asignaturaFis.setId(51L);
        asignaturaFis.setNombre("Física");

        asignacion1 = new AsignacionAcademica();
        asignacion1.setId(60L);
        asignacion1.setGrupo(grupo);
        asignacion1.setProfesor(profesor1);
        asignacion1.setAsignatura(asignaturaMat);
        asignacion1.setActivo(true);

        asignacion2 = new AsignacionAcademica();
        asignacion2.setId(61L);
        asignacion2.setGrupo(grupo);
        asignacion2.setProfesor(profesor2);
        asignacion2.setAsignatura(asignaturaFis);
        asignacion2.setActivo(true);

        aula101 = new Aula("SEDE_PRINCIPAL", "101", "Salón 101", TipoEspacioFisico.AULA_ESTANDAR, 40);
        aula101.setId(1L);
        aula101.setEstado(EstadoAula.DISPONIBLE);

        aulaLab = new Aula("SEDE_PRINCIPAL", "LAB", "Laboratorio de Ciencias", TipoEspacioFisico.LABORATORIO_CIENCIAS, 35);
        aulaLab.setId(2L);
        aulaLab.setEstado(EstadoAula.DISPONIBLE);

        franja1 = new PlantillaFranjaHoraria(anio, Jornada.MANANA, 1, LocalTime.of(7, 0), LocalTime.of(7, 55), TipoFranjaHoraria.BLOQUE_LECTIVO, "Bloque 1");
        franja1.setId(10L);

        versionBorrador = new HorarioGrupoVersion(grupo, 1);
        versionBorrador.setId(100L);
        versionBorrador.setEstado(EstadoHorarioVersion.BORRADOR);
    }

    // =========================================================================
    // UC-PROV-20: REGLA DE TRIPLE NO-COLISIÓN (RN-HOR-01)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-20 E1 / RN-HOR-01: Colisión de Docente con otro grupo en la misma franja debe ser bloqueada")
    void testUCPROV20_ColisionDocente_DebeLanzarConflicto() {
        HorarioBloqueRequest req = new HorarioBloqueRequest(60L, 1L, DiaSemana.LUNES, 10L);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion1));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula101));
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(30L);
        when(franjaRepository.findById(10L)).thenReturn(Optional.of(franja1));

        HorarioBloque colision = new HorarioBloque();
        Grupo otroGrupo = new Grupo();
        otroGrupo.setNombre("10-B");
        AsignacionAcademica asigOtro = new AsignacionAcademica();
        asigOtro.setGrupo(otroGrupo);
        asigOtro.setProfesor(profesor1);
        colision.setAsignacionAcademica(asigOtro);

        when(bloqueRepository.findColisionesDocente(30L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, -1L))
                .thenReturn(List.of(colision));

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.agregarBloqueHorario(100L, req));
    }

    @Test
    @DisplayName("UC-PROV-20 E1 / RN-HOR-01: Colisión de Aula ocupada por otro grupo debe ser bloqueada")
    void testUCPROV20_ColisionAula_DebeLanzarConflicto() {
        HorarioBloqueRequest req = new HorarioBloqueRequest(60L, 1L, DiaSemana.LUNES, 10L);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion1));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula101));
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(30L);
        when(franjaRepository.findById(10L)).thenReturn(Optional.of(franja1));
        when(bloqueRepository.findColisionesDocente(any(), any(), any(), any(), any())).thenReturn(List.of());

        HorarioBloque colisionAula = new HorarioBloque();
        Grupo ocupante = new Grupo();
        ocupante.setNombre("11-A");
        AsignacionAcademica asigOcup = new AsignacionAcademica();
        asigOcup.setGrupo(ocupante);
        colisionAula.setAsignacionAcademica(asigOcup);

        when(bloqueRepository.findColisionesAula(1L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, -1L))
                .thenReturn(List.of(colisionAula));

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.agregarBloqueHorario(100L, req));
    }

    @Test
    @DisplayName("UC-PROV-20 E1 / RN-HOR-01: Colisión de Grupo con dos clases simultáneas debe ser bloqueada")
    void testUCPROV20_ColisionGrupo_DebeLanzarConflicto() {
        HorarioBloqueRequest req = new HorarioBloqueRequest(61L, 1L, DiaSemana.LUNES, 10L);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(asignacionRepository.findById(61L)).thenReturn(Optional.of(asignacion2));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula101));
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(30L);
        when(franjaRepository.findById(10L)).thenReturn(Optional.of(franja1));

        HorarioBloque clasePrevia = new HorarioBloque();
        clasePrevia.setId(555L);
        clasePrevia.setAsignacionAcademica(asignacion1);

        when(bloqueRepository.findByHorarioVersionIdAndDiaSemanaAndFranjaHorariaId(100L, DiaSemana.LUNES, 10L))
                .thenReturn(Optional.of(clasePrevia));

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.agregarBloqueHorario(100L, req));
    }

    // =========================================================================
    // UC-PROV-20: CAPACIDAD Y ESTADO DE AULA (RN-HOR-05)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-20 E3 / RN-HOR-05: Aula con capacidad menor al número de estudiantes debe ser bloqueada")
    void testUCPROV20_E3_CapacidadInsuficiente_DebeLanzarConflicto() {
        HorarioBloqueRequest req = new HorarioBloqueRequest(60L, 2L, DiaSemana.LUNES, 10L);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion1));
        when(aulaRepository.findById(2L)).thenReturn(Optional.of(aulaLab)); // Capacidad 35
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(40L); // 40 alumnos

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.agregarBloqueHorario(100L, req));
    }

    @Test
    @DisplayName("UC-PROV-20 E3: Aula en estado EN_MANTENIMIENTO no puede ser programada")
    void testUCPROV20_E3_AulaEnMantenimiento_DebeLanzarConflicto() {
        HorarioBloqueRequest req = new HorarioBloqueRequest(60L, 1L, DiaSemana.LUNES, 10L);
        aula101.setEstado(EstadoAula.EN_MANTENIMIENTO);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(asignacionRepository.findById(60L)).thenReturn(Optional.of(asignacion1));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula101));

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.agregarBloqueHorario(100L, req));
    }

    // =========================================================================
    // UC-PROV-20: CUMPLIMIENTO EXACTO DE INTENSIDAD HORARIA SEMANAL (RN-HOR-02)
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-20 E2 / RN-HOR-02: Publicar horario sin cumplir la IHS del Plan de Estudios debe fallar")
    void testUCPROV20_E2_PublicarSinCumplirIhs_DebeLanzarConflicto() {
        GradoAsignatura gaMat = new GradoAsignatura();
        gaMat.setGrado(grado);
        gaMat.setAsignatura(asignaturaMat);
        gaMat.setIntensidadHoraria(4); // Exige 4 horas semanales

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(5L, 1L)).thenReturn(List.of(gaMat));
        when(bloqueRepository.countBloquesAsignadosAsignatura(100L, 50L)).thenReturn(2L); // Solo tiene 2 programadas

        assertThrows(ConflictoNegocioException.class, () ->
                horarioService.publicarHorario(100L, LocalDate.of(2026, 2, 1), 1L));
    }

    @Test
    @DisplayName("UC-PROV-20: Publicación exitosa de horario al cumplir 100% de la IHS semanal")
    void testUCPROV20_PublicacionExitosa_CumpleIhs() {
        GradoAsignatura gaMat = new GradoAsignatura();
        gaMat.setGrado(grado);
        gaMat.setAsignatura(asignaturaMat);
        gaMat.setIntensidadHoraria(4);

        when(versionRepository.findById(100L)).thenReturn(Optional.of(versionBorrador));
        when(gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(5L, 1L)).thenReturn(List.of(gaMat));
        when(bloqueRepository.countBloquesAsignadosAsignatura(100L, 50L)).thenReturn(4L); // 4 programadas exactas
        when(versionRepository.findFirstByGrupoIdAndEstadoOrderByNumeroVersionDesc(20L, EstadoHorarioVersion.PUBLICADO_VIGENTE))
                .thenReturn(Optional.empty());
        when(versionRepository.save(any(HorarioGrupoVersion.class))).thenAnswer(i -> i.getArgument(0));

        HorarioGrupoVersionResponse resp = horarioService.publicarHorario(100L, LocalDate.of(2026, 2, 1), 1L);

        assertNotNull(resp);
        assertEquals(EstadoHorarioVersion.PUBLICADO_VIGENTE, resp.getEstado());
        assertEquals(LocalDate.of(2026, 2, 1), resp.getFechaInicioVigencia());
    }

    // =========================================================================
    // UC-PROV-21: GESTIÓN DE NOVEDADES Y PERMUTAS
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-21: Registrar novedad temporal de cambio de aula exitosamente")
    void testUCPROV21_NovedadCambioAula_Exitoso() {
        HorarioBloque bloque = new HorarioBloque(versionBorrador, asignacion1, aula101, DiaSemana.LUNES, franja1);
        bloque.setId(10L);

        NovedadHorarioRequest req = new NovedadHorarioRequest();
        req.setHorarioBloqueId(10L);
        req.setTipoNovedad(TipoNovedadHorario.CAMBIO_AULA_TEMPORAL);
        req.setAulaReemplazoId(2L);
        req.setFechaInicio(LocalDate.of(2026, 3, 10));
        req.setFechaFin(LocalDate.of(2026, 3, 12));
        req.setMotivo("Salón 101 en mantenimiento");

        when(bloqueRepository.findById(10L)).thenReturn(Optional.of(bloque));
        when(aulaRepository.findById(2L)).thenReturn(Optional.of(aulaLab));
        when(bloqueRepository.findColisionesAula(2L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, 10L))
                .thenReturn(List.of());
        when(novedadRepository.save(any(NovedadHorario.class))).thenAnswer(i -> {
            NovedadHorario n = i.getArgument(0);
            n.setId(1L);
            return n;
        });

        NovedadHorarioResponse resp = novedadHorarioService.registrarNovedad(req, 1L);

        assertNotNull(resp);
        assertEquals(TipoNovedadHorario.CAMBIO_AULA_TEMPORAL, resp.getTipoNovedad());
        assertEquals("LAB", resp.getAulaReemplazoCodigo());
    }

    @Test
    @DisplayName("UC-PROV-21 E1: Novedad con docente de reemplazo en colisión debe ser rechazada")
    void testUCPROV21_E1_ColisionDocenteReemplazo_DebeLanzarConflicto() {
        HorarioBloque bloque = new HorarioBloque(versionBorrador, asignacion1, aula101, DiaSemana.LUNES, franja1);
        bloque.setId(10L);

        NovedadHorarioRequest req = new NovedadHorarioRequest();
        req.setHorarioBloqueId(10L);
        req.setTipoNovedad(TipoNovedadHorario.PERMUTA_DOCENTE);
        req.setDocenteReemplazoId(31L);
        req.setFechaInicio(LocalDate.of(2026, 3, 10));
        req.setFechaFin(LocalDate.of(2026, 3, 10));
        req.setMotivo("Permuta de bloque");

        when(bloqueRepository.findById(10L)).thenReturn(Optional.of(bloque));
        when(profesorRepository.findById(31L)).thenReturn(Optional.of(profesor2));

        HorarioBloque colision = new HorarioBloque();
        when(bloqueRepository.findColisionesDocente(31L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, 10L))
                .thenReturn(List.of(colision));

        assertThrows(ConflictoNegocioException.class, () ->
                novedadHorarioService.registrarNovedad(req, 1L));
    }

    // =========================================================================
    // UC-04 y UC-10: CONSULTAS CONTEXTUALES SEGURAS ANTI-IDOR
    // =========================================================================

    @Test
    @DisplayName("UC-04: Estudiante consulta su horario vigente según su matrícula activa")
    void testUC04_ConsultaHorarioEstudiante_Exitoso() {
        Usuario userEst = new Usuario();
        userEst.setId(500L);
        userEst.setPersonaTipo(TipoPersonaUsuario.ESTUDIANTE);
        userEst.setPersonaId(70L);

        Estudiante est = new Estudiante();
        est.setId(70L);
        est.setNombres("Niels");
        est.setApellidos("Bohr");

        Matricula mat = new Matricula();
        mat.setId(100L);
        mat.setNumeroMatricula("MAT-2026-0001");
        mat.setGrupo(grupo);
        mat.setEstado(EstadoMatricula.MATRICULADA);

        versionBorrador.setEstado(EstadoHorarioVersion.PUBLICADO_VIGENTE);
        versionBorrador.setFechaInicioVigencia(LocalDate.of(2026, 2, 1));

        when(usuarioRepository.findById(500L)).thenReturn(Optional.of(userEst));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(est));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(70L)).thenReturn(List.of(mat));
        when(versionRepository.findFirstByGrupoIdAndEstadoOrderByNumeroVersionDesc(20L, EstadoHorarioVersion.PUBLICADO_VIGENTE))
                .thenReturn(Optional.of(versionBorrador));

        ConsultaHorarioEstudianteResponse resp = horarioService.consultarHorarioEstudiante(500L, LocalDate.of(2026, 3, 1));

        assertNotNull(resp);
        assertEquals("Niels Bohr", resp.getEstudianteNombreCompleto());
        assertEquals("9-A", resp.getGrupoNombre());
    }

    @Test
    @DisplayName("UC-10 E1 / RN-HOR-07: Acudiente con BLOQUEADO_RESTRICCION_LEGAL no puede consultar horarios")
    void testUC10_E1_AcudienteRestriccionLegal_DebeDenegar() {
        Usuario userAcud = new Usuario();
        userAcud.setId(600L);
        userAcud.setPersonaTipo(TipoPersonaUsuario.ACUDIENTE);
        userAcud.setPersonaId(80L);

        Estudiante est = new Estudiante();
        est.setId(70L);

        Acudiente acud = new Acudiente();
        acud.setId(80L);

        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(est);
        vinculo.setAcudiente(acud);
        vinculo.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(usuarioRepository.findById(600L)).thenReturn(Optional.of(userAcud));
        when(estudianteRepository.findById(70L)).thenReturn(Optional.of(est));
        when(estudianteAcudienteRepository.findByEstudiante(est)).thenReturn(List.of(vinculo));

        assertThrows(AccessDeniedException.class, () ->
                horarioService.consultarHorarioAcudiente(600L, 70L, LocalDate.now()));
    }

    // =========================================================================
    // UC-PROV-22: AGENDA DOCENTE Y DISPONIBILIDAD DE AULAS
    // =========================================================================

    @Test
    @DisplayName("UC-PROV-22: Docente consulta su agenda individual consolidada")
    void testUCPROV22_AgendaDocente_Exitoso() {
        Usuario userDoc = new Usuario();
        userDoc.setId(300L);

        when(profesorRepository.findByUsuarioId(300L)).thenReturn(Optional.of(profesor1));

        HorarioBloque b1 = new HorarioBloque(versionBorrador, asignacion1, aula101, DiaSemana.LUNES, franja1);
        b1.setId(10L);

        when(bloqueRepository.findAgendaDocenteVigente(30L)).thenReturn(List.of(b1));

        AgendaDocenteResponse resp = horarioService.consultarAgendaDocente(300L);

        assertNotNull(resp);
        assertEquals(1L, resp.getTotalBloquesSemanales());
        assertEquals("Carl Gauss", resp.getProfesorNombreCompleto());
    }

    @Test
    @DisplayName("UC-PROV-22 A2: Mapa de disponibilidad de aulas por franja horaria")
    void testUCPROV22_DisponibilidadAulas_Exitoso() {
        when(aulaRepository.findBySedeAndEstado("SEDE_PRINCIPAL", EstadoAula.DISPONIBLE)).thenReturn(List.of(aula101, aulaLab));
        when(bloqueRepository.findColisionesAula(1L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, -1L))
                .thenReturn(List.of()); // Aula 101 libre

        HorarioBloque bloqueOcup = new HorarioBloque();
        bloqueOcup.setAsignacionAcademica(asignacion2);
        when(bloqueRepository.findColisionesAula(2L, DiaSemana.LUNES, 10L, EstadoHorarioVersion.PUBLICADO_VIGENTE, -1L))
                .thenReturn(List.of(bloqueOcup)); // Aula Lab ocupada

        List<DisponibilidadAulaResponse> resps = horarioService.consultarDisponibilidadAulas("SEDE_PRINCIPAL", DiaSemana.LUNES, 10L);

        assertNotNull(resps);
        assertEquals(2, resps.size());
        assertTrue(resps.get(0).getDisponible());
        assertFalse(resps.get(1).getDisponible());
        assertEquals("9-A", resps.get(1).getGrupoOcupante());
    }
}
