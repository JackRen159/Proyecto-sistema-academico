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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - Consulta Proyectada de Convivencia con Permisos (UC-25)")
class ConvivenciaConsultaServiceTest {

    @Mock private EstudianteRepository estudianteRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AcudienteRepository acudienteRepository;
    @Mock private EstudianteAcudienteRepository estudianteAcudienteRepository;
    @Mock private AsignacionAcademicaRepository asignacionAcademicaRepository;
    @Mock private MatriculaRepository matriculaRepository;
    @Mock private ObservadorService observadorService;
    @Mock private RutaAtencionService rutaAtencionService;
    @Mock private DebidoProcesoService debidoProcesoService;
    @Mock private MedidaSancionService medidaSancionService;
    @Mock private PlanContinuidadAcademicaRepository planContinuidadRepository;
    @Mock private CasoConvivenciaRepository casoRepository;
    @Mock private ExpedienteDebidoProcesoRepository expedienteRepository;
    @Mock private MedidaSancionConvivenciaRepository medidaRepository;
    @Mock private AuditoriaGlobalService auditoriaGlobalService;

    private ConvivenciaConsultaService consultaService;

    private Estudiante estudiante;
    private Usuario usuarioAcudiente;
    private Acudiente acudiente;
    private Usuario usuarioEstudiante;

    @BeforeEach
    void setUp() {
        consultaService = new ConvivenciaConsultaService(
                estudianteRepository,
                usuarioRepository,
                acudienteRepository,
                estudianteAcudienteRepository,
                asignacionAcademicaRepository,
                matriculaRepository,
                observadorService,
                rutaAtencionService,
                debidoProcesoService,
                medidaSancionService,
                planContinuidadRepository,
                casoRepository,
                expedienteRepository,
                medidaRepository,
                auditoriaGlobalService
        );

        estudiante = new Estudiante();
        estudiante.setId(10L);
        estudiante.setNombres("Juan");
        estudiante.setApellidos("Pérez");

        acudiente = new Acudiente();
        acudiente.setId(20L);
        acudiente.setDocumento("52001002");

        usuarioAcudiente = new Usuario("52001002", "hash", Rol.ACUDIENTE);
        usuarioAcudiente.setId(200L);
        usuarioAcudiente.setPersonaId(20L);

        usuarioEstudiante = new Usuario("est10", "hash", Rol.ESTUDIANTE);
        usuarioEstudiante.setId(100L);
        usuarioEstudiante.setPersonaId(10L);
    }

    @Test
    @DisplayName("UC-25 E1: Acudiente con BLOQUEADO_RESTRICCION_LEGAL es rechazado con AccessDeniedException")
    void consultarSeguimiento_AcudienteConRestriccionLegal_LanzaAccessDenied() {
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEstadoVinculo(EstadoVinculo.BLOQUEADO_RESTRICCION_LEGAL);

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioAcudiente));
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(vinculo));

        assertThrows(AccessDeniedException.class, () -> consultaService.consultarSeguimientoEstudiante(10L, 200L));
    }

    @Test
    @DisplayName("UC-25 A1: Proyección minimizada y anonimizada para acudiente verificado")
    void consultarSeguimiento_AcudienteVerificado_ProyeccionAnonimizada() {
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        CasoConvivencia caso = new CasoConvivencia();
        caso.setId(1L);
        caso.setCodigoExpediente("EXP-CONV-2026-0001");
        caso.setAnioLectivo(new AnioLectivo());
        caso.setEstudianteImplicado(estudiante);
        caso.setTipoSituacion(TipoSituacionConvivencia.TIPO_I);
        caso.setEstadoCaso(EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION);
        caso.setFechaHechos(LocalDate.now());
        caso.setLugarHechos("Patio");
        caso.setDescripcionSituacion("Conflicto menor entre pares");
        caso.setAtencionMedicaUrgente(false);
        caso.setEsReporteConfidencial(false);
        caso.setReservaProteccionContraAcudiente(false);
        caso.setRemitidoAutoridadExterna(false);

        CasoConvivenciaResponse resp = new CasoConvivenciaResponse(
                1L, "EXP-CONV-2026-0001", 1L, 10L, "Juan Pérez",
                TipoSituacionConvivencia.TIPO_I, EstadoCasoConvivencia.EN_PROTOCOLO_ATENCION,
                LocalDate.now(), "Patio", "Conflicto menor entre pares", 1L, "Testigo confidencial",
                false, false, "Datos medicos íntimos", false, null, null, false, null, null, null,
                LocalDateTime.now(), null, null
        );

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioAcudiente));
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(vinculo));
        when(observadorService.listarPorEstudiante(10L)).thenReturn(Collections.emptyList());
        when(casoRepository.findByEstudianteImplicadoId(10L)).thenReturn(List.of(caso));
        when(rutaAtencionService.toResponse(caso)).thenReturn(resp);
        when(expedienteRepository.findByEstudianteId(10L)).thenReturn(Collections.emptyList());
        when(planContinuidadRepository.findByEstudianteId(10L)).thenReturn(Collections.emptyList());

        SeguimientoConvivenciaEstudianteResponse resultado = consultaService.consultarSeguimientoEstudiante(10L, 200L);

        assertNotNull(resultado);
        assertEquals(1, resultado.casos().size());
        CasoConvivenciaResponse casoProyectado = resultado.casos().get(0);
        // Debe anonimizar el reportante y ocultar detalles médicos íntimos
        assertEquals("Personal Institucional Autorizado", casoProyectado.nombreReportante());
        assertNull(casoProyectado.detalleAtencionMedica());
    }

    @Test
    @DisplayName("UC-25 E1 / UC-23 A4: Reserva contra acudiente agresor omite el caso para dicho adulto")
    void consultarSeguimiento_ReservaContraAcudiente_OmiteCasoParaAgresor() {
        EstudianteAcudiente vinculo = new EstudianteAcudiente();
        vinculo.setEstudiante(estudiante);
        vinculo.setAcudiente(acudiente);
        vinculo.setEstadoVinculo(EstadoVinculo.VERIFICADO);

        CasoConvivencia casoReservado = new CasoConvivencia();
        casoReservado.setId(2L);
        casoReservado.setReservaProteccionContraAcudiente(true);
        casoReservado.setAcudienteAgresor(acudiente); // Este acudiente es el agresor
        casoReservado.setEstudianteImplicado(estudiante);

        when(estudianteRepository.findById(10L)).thenReturn(Optional.of(estudiante));
        when(usuarioRepository.findById(200L)).thenReturn(Optional.of(usuarioAcudiente));
        when(acudienteRepository.findById(20L)).thenReturn(Optional.of(acudiente));
        when(estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)).thenReturn(Optional.of(vinculo));
        when(observadorService.listarPorEstudiante(10L)).thenReturn(Collections.emptyList());
        when(casoRepository.findByEstudianteImplicadoId(10L)).thenReturn(List.of(casoReservado));
        when(expedienteRepository.findByEstudianteId(10L)).thenReturn(Collections.emptyList());
        when(planContinuidadRepository.findByEstudianteId(10L)).thenReturn(Collections.emptyList());

        SeguimientoConvivenciaEstudianteResponse resultado = consultaService.consultarSeguimientoEstudiante(10L, 200L);

        assertNotNull(resultado);
        // El caso con reserva de protección no debe ser expuesto al acudiente agresor
        assertTrue(resultado.casos().isEmpty());
    }
}
