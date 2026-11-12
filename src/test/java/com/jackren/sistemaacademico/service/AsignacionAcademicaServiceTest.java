package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AsignacionAcademicaRequest;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.AsignacionAcademicaMapper;
import com.jackren.sistemaacademico.repository.AsignacionAcademicaRepository;
import com.jackren.sistemaacademico.repository.AsignaturaRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import com.jackren.sistemaacademico.repository.ProfesorRepository;
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
class AsignacionAcademicaServiceTest {

    @Mock
    private AsignacionAcademicaRepository asignacionRepository;

    @Mock
    private ProfesorRepository profesorRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @Spy
    private AsignacionAcademicaMapper asignacionMapper;

    @InjectMocks
    private AsignacionAcademicaService asignacionService;

    private Profesor profesor;
    private Grupo grupo;
    private AnioLectivo anioLectivo;
    private Asignatura asignatura;
    private AsignacionAcademicaRequest request;

    @BeforeEach
    void setUp() {
        profesor = new Profesor();
        profesor.setId(1L);
        profesor.setNombres("Diana");
        profesor.setApellidos("Pérez");
        profesor.setDocumento("98765432");
        profesor.setActivo(true);

        anioLectivo = new AnioLectivo(2026, "Año 2026", null, null);
        anioLectivo.setId(10L);
        anioLectivo.setEstado(EstadoAnioLectivo.ACTIVO);

        grupo = new Grupo();
        grupo.setId(20L);
        grupo.setNombre("10-A");
        grupo.setAnioLectivo(anioLectivo);
        grupo.setActivo(true);

        asignatura = new Asignatura();
        asignatura.setId(30L);
        asignatura.setNombre("Física");
        asignatura.setCodigo("FIS10");
        asignatura.setActivo(true);

        request = new AsignacionAcademicaRequest();
        request.setProfesorId(1L);
        request.setGrupoId(20L);
        request.setAsignaturaId(30L);
        request.setIntensidadHorariaSemanal(4);
        request.setEsDirectorGrupo(false);
    }

    @Test
    @DisplayName("Debe crear asignación académica cuando todas las invariantes se cumplen")
    void debeCrearAsignacionExitosamente() {
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(asignaturaRepository.findById(30L)).thenReturn(Optional.of(asignatura));
        when(asignacionRepository.existsByGrupoIdAndAsignaturaId(20L, 30L)).thenReturn(false);
        when(asignacionRepository.save(any(AsignacionAcademica.class))).thenAnswer(invocation -> {
            AsignacionAcademica a = invocation.getArgument(0);
            a.setId(100L);
            return a;
        });

        AsignacionAcademicaResponse response = asignacionService.crear(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getProfesorId()).isEqualTo(1L);
        assertThat(response.getProfesorNombreCompleto()).isEqualTo("Diana Pérez");
        assertThat(response.getGrupoId()).isEqualTo(20L);
        assertThat(response.getAsignaturaId()).isEqualTo(30L);
        assertThat(response.getIntensidadHorariaSemanal()).isEqualTo(4);
    }

    @Test
    @DisplayName("Debe rechazar asignación si el profesor está inactivo")
    void debeRechazarProfesorInactivo() {
        profesor.setActivo(false);
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));

        assertThatThrownBy(() -> asignacionService.crear(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("No se puede asignar un profesor en estado inactivo");

        verify(asignacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar asignación si el año lectivo del grupo está CERRADO")
    void debeRechazarAnioLectivoCerrado() {
        anioLectivo.setEstado(EstadoAnioLectivo.CERRADO);
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));

        assertThatThrownBy(() -> asignacionService.crear(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("No se pueden realizar asignaciones académicas en un año lectivo cerrado");

        verify(asignacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe rechazar asignación si la asignatura ya tiene un docente asignado en ese grupo")
    void debeRechazarAsignaturaYaAsignadaEnGrupo() {
        when(profesorRepository.findById(1L)).thenReturn(Optional.of(profesor));
        when(grupoRepository.findById(20L)).thenReturn(Optional.of(grupo));
        when(asignaturaRepository.findById(30L)).thenReturn(Optional.of(asignatura));
        when(asignacionRepository.existsByGrupoIdAndAsignaturaId(20L, 30L)).thenReturn(true);

        assertThatThrownBy(() -> asignacionService.crear(request))
                .isInstanceOf(ConflictoNegocioException.class)
                .hasMessageContaining("Ya existe una asignación académica para la asignatura");

        verify(asignacionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe validar correctamente la autorización contextual docente (Spec S2)")
    void debeValidarAutorizacionContextualDocente() {
        when(asignacionRepository.existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue(50L, 20L, 30L))
                .thenReturn(true);
        when(asignacionRepository.existsByProfesorUsuarioIdAndGrupoIdAndAsignaturaIdAndActivoTrue(50L, 20L, 999L))
                .thenReturn(false);

        assertThat(asignacionService.tieneAutorizacionDocente(50L, 20L, 30L)).isTrue();
        assertThat(asignacionService.tieneAutorizacionDocente(50L, 20L, 999L)).isFalse();
        assertThat(asignacionService.tieneAutorizacionDocente(null, 20L, 30L)).isFalse();
    }
}
