package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.MatriculaRequest;
import com.jackren.sistemaacademico.dto.MatriculaResponse;
import com.jackren.sistemaacademico.dto.TrasladoGrupoRequest;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private HistorialTrasladoMatriculaRepository historialTrasladoRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private MatriculaMapper matriculaMapper;

    @InjectMocks
    private MatriculaService matriculaService;

    private Estudiante estudiante;
    private Grupo grupo;
    private Grado grado;
    private AnioLectivo anioLectivo;
    private MatriculaRequest request;

    @BeforeEach
    void setUp() {
        estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setNombres("Sofía");
        estudiante.setApellidos("Herrera");
        estudiante.setDocumento("1030405060");
        estudiante.setActivo(true);

        grado = new Grado();
        grado.setId(5L);
        grado.setCodigo("10");
        grado.setNombre("Décimo");

        anioLectivo = new AnioLectivo(2026, "Año 2026", null, null);
        anioLectivo.setId(10L);
        anioLectivo.setEstado(EstadoAnioLectivo.ACTIVO);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("10-A");
        grupo.setGrado(grado);
        grupo.setAnioLectivo(anioLectivo);
        grupo.setCupoMaximo(35);
        grupo.setActivo(true);

        request = new MatriculaRequest();
        request.setEstudianteId(1L);
        request.setGrupoId(20L);
        request.setTipoMatricula(TipoMatricula.REGULAR);
        request.setFolio("FOLIO-01");
    }

    @Test
    @DisplayName("Debe matricular estudiante exitosamente cuando cumple todas las invariantes")
    void debeMatricularEstudianteExitosamente() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(1L, 10L, EstadoMatricula.MATRICULADA)).thenReturn(false);
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(10L);
        when(matriculaRepository.countByAnioLectivoId(10L)).thenReturn(0L);
        when(matriculaRepository.existsByNumeroMatricula("MAT-2026-0001")).thenReturn(false);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> {
            Matricula m = inv.getArgument(0);
            m.setId(100L);
            return m;
        });

        MatriculaResponse response = matriculaService.matricular(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getNumeroMatricula()).isEqualTo("MAT-2026-0001");
        assertThat(response.getEstudianteId()).isEqualTo(1L);
        assertThat(response.getGrupoId()).isEqualTo(20L);
        assertThat(response.getEstado()).isEqualTo(EstadoMatricula.MATRICULADA);
        assertThat(response.getTipoMatricula()).isEqualTo(TipoMatricula.REGULAR);
    }

    @Test
    @DisplayName("Debe rechazar matrícula si el grupo ha alcanzado el cupo máximo")
    void debeRechazarPorCupoMaximoAlcanzado() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(1L, 10L, EstadoMatricula.MATRICULADA)).thenReturn(false);
        when(matriculaRepository.countByGrupoIdAndEstado(20L, EstadoMatricula.MATRICULADA)).thenReturn(35L); // Cupo lleno

        assertThatThrownBy(() -> matriculaService.matricular(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("ha alcanzado su cupo máximo de 35 estudiantes");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar matrícula si el estudiante ya tiene una matrícula activa en el año lectivo")
    void debeRechazarPorDobleMatriculaEnMismoAnio() {
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(1L, 10L, EstadoMatricula.MATRICULADA)).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.matricular(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("ya cuenta con una matrícula activa en el año lectivo 2026");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar matrícula si el año lectivo está CERRADO")
    void debeRechazarPorAnioLectivoCerrado() {
        anioLectivo.setEstado(EstadoAnioLectivo.CERRADO);
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudiante));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));

        assertThatThrownBy(() -> matriculaService.matricular(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("No se pueden realizar matrículas en un año lectivo cerrado");

        verify(matriculaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe trasladar de grupo exitosamente registrando auditoría en historial")
    void debeTrasladarDeGrupoExitosamente() {
        Matricula matricula = new Matricula("MAT-2026-0001", estudiante, grupo, anioLectivo);
        matricula.setId(100L);

        Grupo grupoDestino = new Grupo();
        grupoDestino.setId(21L);
        grupoDestino.setNombre("10-B");
        grupoDestino.setGrado(grado); // Mismo grado
        grupoDestino.setAnioLectivo(anioLectivo); // Mismo año
        grupoDestino.setCupoMaximo(35);
        grupoDestino.setActivo(true);

        TrasladoGrupoRequest trasladoReq = new TrasladoGrupoRequest();
        trasladoReq.setNuevoGrupoId(21L);
        trasladoReq.setMotivo("Cambio por solicitud de acudiente");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(grupoRepository.findById(21L)).thenReturn(Optional.of(grupoDestino));
        when(matriculaRepository.countByGrupoIdAndEstado(21L, EstadoMatricula.MATRICULADA)).thenReturn(20L);
        when(matriculaRepository.save(any(Matricula.class))).thenAnswer(inv -> inv.getArgument(0));

        MatriculaResponse response = matriculaService.trasladarDeGrupo(100L, trasladoReq, 5L);

        assertThat(response.getGrupoId()).isEqualTo(21L);
        assertThat(response.getGrupoNombre()).isEqualTo("10-B");
        verify(historialTrasladoRepository).save(any(HistorialTrasladoMatricula.class));
    }

    @Test
    @DisplayName("Debe rechazar traslado si el grupo destino es de un grado diferente")
    void debeRechazarTrasladoADiferenteGrado() {
        Matricula matricula = new Matricula("MAT-2026-0001", estudiante, grupo, anioLectivo);
        matricula.setId(100L);

        Grado otroGrado = new Grado();
        otroGrado.setId(99L);
        otroGrado.setNombre("Undécimo");

        Grupo grupoDestino = new Grupo();
        grupoDestino.setId(22L);
        grupoDestino.setNombre("11-A");
        grupoDestino.setGrado(otroGrado); // Grado distinto
        grupoDestino.setAnioLectivo(anioLectivo);
        grupoDestino.setActivo(true);

        TrasladoGrupoRequest trasladoReq = new TrasladoGrupoRequest();
        trasladoReq.setNuevoGrupoId(22L);
        trasladoReq.setMotivo("Error en asignación");

        when(matriculaRepository.findById(100L)).thenReturn(Optional.of(matricula));
        when(grupoRepository.findById(22L)).thenReturn(Optional.of(grupoDestino));

        assertThatThrownBy(() -> matriculaService.trasladarDeGrupo(100L, trasladoReq, 5L))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("El traslado interno de grupo solo está permitido entre cursos del mismo grado");

        verify(historialTrasladoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe validar correctamente la existencia de matrícula activa para calificaciones (Spec S2)")
    void debeValidarMatriculaActivaParaCalificaciones() {
        when(matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(1L, 20L, EstadoMatricula.MATRICULADA))
                .thenReturn(true);
        when(matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(1L, 99L, EstadoMatricula.MATRICULADA))
                .thenReturn(false);

        assertThat(matriculaService.tieneMatriculaActivaEnGrupo(1L, 20L)).isTrue();
        assertThat(matriculaService.tieneMatriculaActivaEnGrupo(1L, 99L)).isFalse();
        assertThat(matriculaService.tieneMatriculaActivaEnGrupo(null, 20L)).isFalse();
    }
}
