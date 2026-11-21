package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.ValidacionException;
import com.jackren.sistemaacademico.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Observador (UC-22) y Ruta de Atención (UC-23)")
class ObservadorYRutaAtencionServiceTest {

    @Mock private AnotacionObservadorRepository anotacionRepository;
    @Mock private AnotacionObservadorAdendaRepository adendaRepository;
    @Mock private CasoConvivenciaRepository casoRepository;
    @Mock private ActuacionRutaAtencionRepository actuacionRepository;
    @Mock private EstudianteRepository estudianteRepository;
    @Mock private PeriodoAcademicoRepository periodoAcademicoRepository;
    @Mock private AnioLectivoRepository anioLectivoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private AsignacionAcademicaRepository asignacionAcademicaRepository;
    @Mock private EstudianteAcudienteRepository estudianteAcudienteRepository;
    @Mock private AcudienteRepository acudienteRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;

    private RutaAtencionService rutaAtencionService;
    private ObservadorService observadorService;

    private Estudiante estudiante;
    private Usuario docente;
    private Usuario directivo;
    private AnioLectivo anioLectivo;

    @BeforeEach
    void setUp() {
        rutaAtencionService = new RutaAtencionService(
                casoRepository,
                actuacionRepository,
                anioLectivoRepository,
                estudianteRepository,
                usuarioRepository,
                acudienteRepository,
                auditoriaGlobalService
        );

        observadorService = new ObservadorService(
                anotacionRepository,
                adendaRepository,
                estudianteRepository,
                periodoAcademicoRepository,
                usuarioRepository,
                matriculaRepository,
                asignacionAcademicaRepository,
                estudianteAcudienteRepository,
                acudienteRepository,
                rutaAtencionService,
                auditoriaGlobalService
        );

        estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Carlos");
        estudiante.setApellidos("Pérez");

        docente = new Usuario("docente1", "hash", Rol.PROFESOR);
        docente.setId(1L);

        directivo = new Usuario("coord1", "hash", Rol.COORDINADOR_CONVIVENCIA);
        directivo.setId(2L);

        anioLectivo = new AnioLectivo();
        anioLectivo.setId(100L);
        anioLectivo.setAnio(2026);
    }

    @Test
    @DisplayName("UC-22: Docente con asignación registra anotación formativa exitosamente")
    void registrarAnotacion_DocenteConAsignacion_Exitoso() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, null, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Aula 204",
                "El estudiante demuestra actitud receptiva en el diálogo de seguimiento",
                "Acuerdo de puntualidad", false, false, null
        );

        Grupo grupo = new Grupo();
        grupo.setId(5L);
        Matricula matricula = new Matricula();
        matricula.setEstado(EstadoMatricula.MATRICULADA);
        matricula.setGrupo(grupo);

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(List.of(matricula));
        when(asignacionAcademicaRepository.existsByProfesorUsuarioIdAndGrupoIdAndActivoTrue(1L, 5L)).thenReturn(true);
        when(anotacionRepository.save(any(AnotacionObservador.class))).thenAnswer(i -> {
            AnotacionObservador a = i.getArgument(0);
            a.setId(50L);
            return a;
        });
        when(adendaRepository.findByAnotacionIdOrderByFechaAdendaAsc(50L)).thenReturn(Collections.emptyList());

        AnotacionObservadorResponse response = observadorService.registrarAnotacion(request, 1L);

        assertNotNull(response);
        assertEquals(TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO, response.tipoAnotacion());
        assertTrue(response.anotacionFirmada());
    }

    @Test
    @DisplayName("UC-22 E2: Docente sin asignación en el grupo es rechazado con 403 Forbidden")
    void registrarAnotacion_DocenteSinAsignacion_LanzaAccessDenied() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, null, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Aula 204",
                "El estudiante demuestra actitud receptiva en el diálogo de seguimiento",
                "Acuerdo de puntualidad", false, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(10L)).thenReturn(Collections.emptyList());

        assertThrows(AccessDeniedException.class, () -> observadorService.registrarAnotacion(request, 1L));
    }

    @Test
    @DisplayName("UC-22 A4: Docente en modo Zona Común registra incidente sin asignación previa")
    void registrarAnotacion_ModoZonaComun_Exitoso() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, null, TipoAnotacionObservador.REPORTE_SITUACION_CONVIVENCIA,
                LocalDate.now(), LocalTime.now(), "Patio Central",
                "Se evidencia altercado verbal entre pares en hora de descanso escolar",
                "Se realiza llamado formativo", true, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));
        when(anotacionRepository.save(any(AnotacionObservador.class))).thenAnswer(i -> {
            AnotacionObservador a = i.getArgument(0);
            a.setId(51L);
            return a;
        });
        when(adendaRepository.findByAnotacionIdOrderByFechaAdendaAsc(51L)).thenReturn(Collections.emptyList());

        AnotacionObservadorResponse response = observadorService.registrarAnotacion(request, 1L);

        assertNotNull(response);
        assertTrue(response.esZonaComun());
    }

    @Test
    @DisplayName("UC-22 E3: Bloqueo de datos clínicos o sensibles en observador general")
    void registrarAnotacion_DatosClinicosSensibles_LanzaValidacionException() {
        AnotacionObservadorRequest request = new AnotacionObservadorRequest(
                10L, null, TipoAnotacionObservador.SEGUIMIENTO_FORMATIVO,
                LocalDate.now(), LocalTime.now(), "Enfermería",
                "Estudiante presenta diagnóstico psiquiátrico reservado según documento adjunto",
                "Remitir", true, false, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(docente));

        assertThrows(ValidacionException.class, () -> observadorService.registrarAnotacion(request, 1L));
    }

    @Test
    @DisplayName("UC-22 E4: Bloqueo de modificación o borrado in-situ (Inmutabilidad RN-CON-01)")
    void bloquearEdicionOBorradoDirecto_LanzaConflictoNegocioException() {
        assertThrows(ConflictoNegocioException.class, () -> observadorService.bloquearEdicionOBorradoDirecto(50L));
    }

    @Test
    @DisplayName("UC-22 A3: Creación de adenda vinculada para rectificación inmutable")
    void agregarAdenda_Exitoso() {
        AnotacionObservador anotacion = new AnotacionObservador();
        anotacion.setId(50L);
        anotacion.setEstudiante(estudiante);

        AdendaObservadorRequest request = new AdendaObservadorRequest(
                "Aclaración de ubicación",
                "El hecho tuvo lugar en el pasillo adyacente a coordinación y no en cafetería"
        );

        when(anotacionRepository.findById(50L)).thenReturn(Optional.of(anotacion));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(directivo));
        when(adendaRepository.save(any(AnotacionObservadorAdenda.class))).thenAnswer(i -> {
            AnotacionObservadorAdenda ad = i.getArgument(0);
            ad.setId(101L);
            return ad;
        });

        AdendaObservadorResponse response = observadorService.agregarAdenda(50L, request, 2L);

        assertNotNull(response);
        assertEquals(50L, response.anotacionId());
        assertEquals("Aclaración de ubicación", response.motivoAdenda());
    }

    @Test
    @DisplayName("UC-23 A1: Apertura preliminar urgente por atención médica sin trabas burocráticas")
    void abrirCaso_AtencionMedicaUrgente_EstadoPreliminarUrgente() {
        CasoConvivenciaRequest request = new CasoConvivenciaRequest(
                100L, 10L, TipoSituacionConvivencia.TIPO_II,
                LocalDate.now(), "Cancha deportiva",
                "Golpe accidental o agresión física en partido interclases",
                false, true, "Traslado a enfermería y llamado a ambulancia",
                false, null, false, null, null, null
        );

        when(anioLectivoRepository.findById(100L)).thenReturn(Optional.of(anioLectivo));
        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(directivo));
        when(casoRepository.save(any(CasoConvivencia.class))).thenAnswer(i -> {
            CasoConvivencia c = i.getArgument(0);
            c.setId(80L);
            return c;
        });

        CasoConvivenciaResponse response = rutaAtencionService.abrirCaso(request, 2L);

        assertNotNull(response);
        assertEquals(EstadoCasoConvivencia.ABIERTO_PRELIMINAR_URGENTE, response.estadoCaso());
        assertTrue(response.atencionMedicaUrgente());
    }

    @Test
    @DisplayName("UC-23 E3: Prohibición legal absoluta de conciliación o careo en situaciones Tipo III")
    void registrarActuacion_ConciliacionEnTipoIII_LanzaConflictoNegocioException() {
        CasoConvivencia caso = new CasoConvivencia();
        caso.setId(85L);
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_III);

        ActuacionRutaAtencionRequest request = new ActuacionRutaAtencionRequest(
                TipoActuacionRutaAtencion.MEDIACION_CONCILIACION,
                "Se cita a víctima y presunto agresor a mesa de diálogo",
                null, "Acuerdos"
        );

        when(casoRepository.findById(85L)).thenReturn(Optional.of(caso));

        assertThrows(ConflictoNegocioException.class, () -> rutaAtencionService.registrarActuacion(85L, request, 2L));
    }

    @Test
    @DisplayName("UC-23 E1: Bloqueo de cierre de casos Tipo II o III sin código SIUCE")
    void cerrarCaso_TipoII_SinSiuce_LanzaConflictoNegocioException() {
        CasoConvivencia caso = new CasoConvivencia();
        caso.setId(90L);
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_II);

        when(casoRepository.findById(90L)).thenReturn(Optional.of(caso));

        assertThrows(ConflictoNegocioException.class, () -> rutaAtencionService.cerrarCaso(90L, "Motivo", "", 2L));
    }
}
