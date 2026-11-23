package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AnioLectivoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - AnioLectivoService (Invariantes de Año Escolar)")
class AnioLectivoServiceTest {

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Mock
    private com.jackren.sistemaacademico.repository.PeriodoAcademicoRepository periodoAcademicoRepository;

    @Spy
    private AnioLectivoMapper anioLectivoMapper = new AnioLectivoMapper();

    @InjectMocks
    private AnioLectivoService anioLectivoService;

    private AnioLectivo anio2026;

    @BeforeEach
    void setUp() {
        anio2026 = new AnioLectivo(2026, "Año Escolar 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio2026.setId(1L);
    }

    @Test
    @DisplayName("Debe guardar un año lectivo si los datos son válidos y no está duplicado")
    void debeGuardarAnioLectivoExitosamente() {
        AnioLectivoRequest request = new AnioLectivoRequest(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));

        when(anioLectivoRepository.existsByAnio(2026)).thenReturn(false);
        when(anioLectivoRepository.save(any(AnioLectivo.class))).thenReturn(anio2026);

        AnioLectivoResponse response = anioLectivoService.guardar(request);

        assertNotNull(response);
        assertEquals(2026, response.getAnio());
        assertEquals(EstadoAnioLectivo.PLANIFICACION, response.getEstado());
        verify(anioLectivoRepository).save(any(AnioLectivo.class));
    }

    @Test
    @DisplayName("Debe lanzar ConflictoNegocioException si la fecha fin es anterior a la fecha inicio")
    void debeFallarSiFechasSonInconsistentes() {
        AnioLectivoRequest request = new AnioLectivoRequest(
                2026,
                "Año 2026",
                LocalDate.of(2026, 11, 30),
                LocalDate.of(2026, 1, 15) // Fecha fin anterior
        );

        assertThrows(ConflictoNegocioException.class, () -> anioLectivoService.guardar(request));
        verify(anioLectivoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ConflictoNegocioException si el año ya existe")
    void debeFallarSiAnioYaExiste() {
        AnioLectivoRequest request = new AnioLectivoRequest(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));

        when(anioLectivoRepository.existsByAnio(2026)).thenReturn(true);

        assertThrows(ConflictoNegocioException.class, () -> anioLectivoService.guardar(request));
    }

    @Test
    @DisplayName("Invariante Año Activo: Debe activar año si no existe otro activo")
    void debeActivarAnioExitosamente() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)).thenReturn(Optional.empty());
        when(anioLectivoRepository.save(any(AnioLectivo.class))).thenReturn(anio2026);

        AnioLectivoResponse response = anioLectivoService.activar(1L);

        assertEquals(EstadoAnioLectivo.ACTIVO, response.getEstado());
    }

    @Test
    @DisplayName("Invariante Año Activo: Debe impedir activar un año si ya existe otro en estado ACTIVO")
    void debeImpedirActivarSiYaHayOtroAnioActivo() {
        AnioLectivo anio2025 = new AnioLectivo(2025, "Año 2025", LocalDate.of(2025, 1, 15), LocalDate.of(2025, 11, 30));
        anio2025.setId(2L);
        anio2025.setEstado(EstadoAnioLectivo.ACTIVO);

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)).thenReturn(Optional.of(anio2025));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class, () -> anioLectivoService.activar(1L));
        assertTrue(ex.getMessage().contains("ya se encuentra ACTIVO"));
    }

    @Test
    @DisplayName("Debe impedir activar un año lectivo que ya fue formalmente CERRADO")
    void debeImpedirActivarAnioCerrado() {
        anio2026.setEstado(EstadoAnioLectivo.CERRADO);
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));

        assertThrows(ConflictoNegocioException.class, () -> anioLectivoService.activar(1L));
    }

    @Test
    @DisplayName("Debe cerrar un año lectivo correctamente")
    void debeCerrarAnioLectivo() {
        anio2026.setEstado(EstadoAnioLectivo.ACTIVO);
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));
        when(anioLectivoRepository.save(any(AnioLectivo.class))).thenReturn(anio2026);

        AnioLectivoResponse response = anioLectivoService.cerrar(1L);

        assertEquals(EstadoAnioLectivo.CERRADO, response.getEstado());
    }

    @Test
    @DisplayName("Debe rechazar actualización de año lectivo si las nuevas fechas dejan fuera de rango un periodo (UC-14 E1)")
    void debeRechazarActualizarAnioSiNuevasFechasDejanPeriodoFueraDeRango() {
        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio2026));

        com.jackren.sistemaacademico.entity.PeriodoAcademico p1 = new com.jackren.sistemaacademico.entity.PeriodoAcademico();
        p1.setNombre("Periodo 1");
        p1.setFechaInicio(LocalDate.of(2026, 1, 20));
        p1.setFechaFin(LocalDate.of(2026, 4, 10));

        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(java.util.List.of(p1));

        // Intento de mover la fecha de inicio del año al 1 de febrero (posterior al inicio del Periodo 1)
        AnioLectivoRequest request = new AnioLectivoRequest(
                2026, "Año 2026 Modificado", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 11, 30)
        );

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> anioLectivoService.actualizar(1L, request));
        assertTrue(ex.getMessage().contains("deja al periodo 'Periodo 1'"));
        assertTrue(ex.getMessage().contains("fuera del año lectivo (UC-14 E1)"));
    }
}
