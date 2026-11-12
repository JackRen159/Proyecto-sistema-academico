package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.PeriodoAcademicoRequest;
import com.jackren.sistemaacademico.dto.PeriodoAcademicoResponse;
import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoPeriodo;
import com.jackren.sistemaacademico.entity.PeriodoAcademico;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.mapper.PeriodoAcademicoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.PeriodoAcademicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas Unitarias - PeriodoAcademicoService (Invariantes de Periodos y Porcentajes)")
class PeriodoAcademicoServiceTest {

    @Mock
    private PeriodoAcademicoRepository periodoAcademicoRepository;

    @Mock
    private AnioLectivoRepository anioLectivoRepository;

    @Spy
    private PeriodoAcademicoMapper periodoAcademicoMapper = new PeriodoAcademicoMapper();

    @InjectMocks
    private PeriodoAcademicoService periodoAcademicoService;

    private AnioLectivo anio;
    private PeriodoAcademico periodo1;

    @BeforeEach
    void setUp() {
        anio = new AnioLectivo(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        anio.setId(1L);

        periodo1 = new PeriodoAcademico(
                anio,
                1,
                "Primer Periodo",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2026, 3, 31),
                new BigDecimal("25.00")
        );
        periodo1.setId(10L);
    }

    @Test
    @DisplayName("Debe guardar un periodo académico si fechas y porcentajes son válidos")
    void debeGuardarPeriodoExitosamente() {
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(
                1, "Primer Periodo", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 3, 31), new BigDecimal("25.00")
        );

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(1L, 1)).thenReturn(false);
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of());
        when(periodoAcademicoRepository.save(any(PeriodoAcademico.class))).thenReturn(periodo1);

        PeriodoAcademicoResponse response = periodoAcademicoService.guardar(1L, request);

        assertNotNull(response);
        assertEquals(1, response.getNumero());
        assertEquals(new BigDecimal("25.00"), response.getPorcentaje());
        assertEquals(EstadoPeriodo.CERRADO, response.getEstado());
    }

    @Test
    @DisplayName("Debe lanzar excepción si las fechas del periodo desbordan el año lectivo")
    void debeFallarSiFechasDesbordanAnioLectivo() {
        // Periodo inicia antes del año lectivo
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(
                1, "Primer Periodo", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 3, 31), new BigDecimal("25.00")
        );

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.guardar(1L, request));
        assertTrue(ex.getMessage().contains("no puede ser anterior al inicio del año lectivo"));
    }

    @Test
    @DisplayName("Invariante SIEE: Debe impedir que la suma de porcentajes supere 100%")
    void debeFallarSiSumaPorcentajesSuperaCien() {
        PeriodoAcademico p1 = new PeriodoAcademico(anio, 1, "P1", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 3, 31), new BigDecimal("40.00"));
        PeriodoAcademico p2 = new PeriodoAcademico(anio, 2, "P2", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 6, 15), new BigDecimal("40.00"));

        // Se intenta agregar un periodo de 30% (40 + 40 + 30 = 110%)
        PeriodoAcademicoRequest request = new PeriodoAcademicoRequest(
                3, "Tercer Periodo", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 9, 15), new BigDecimal("30.00")
        );

        when(anioLectivoRepository.findById(1L)).thenReturn(Optional.of(anio));
        when(periodoAcademicoRepository.existsByAnioLectivoIdAndNumero(1L, 3)).thenReturn(false);
        when(periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(1L)).thenReturn(List.of(p1, p2));

        ConflictoNegocioException ex = assertThrows(ConflictoNegocioException.class,
                () -> periodoAcademicoService.guardar(1L, request));
        assertTrue(ex.getMessage().contains("no puede superar el 100%"));
    }

    @Test
    @DisplayName("Debe permitir abrir y cerrar un periodo académico")
    void debeCambiarEstadoDePeriodo() {
        when(periodoAcademicoRepository.findById(10L)).thenReturn(Optional.of(periodo1));
        when(periodoAcademicoRepository.save(any(PeriodoAcademico.class))).thenReturn(periodo1);

        PeriodoAcademicoResponse abierto = periodoAcademicoService.abrir(10L);
        assertEquals(EstadoPeriodo.ABIERTO, abierto.getEstado());

        PeriodoAcademicoResponse cerrado = periodoAcademicoService.cerrar(10L);
        assertEquals(EstadoPeriodo.CERRADO, cerrado.getEstado());
    }
}
