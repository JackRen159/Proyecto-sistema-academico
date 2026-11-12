package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias - Entidades de Estructura Académica (Frente 2)")
class EstructuraAcademicaEntityTest {

    @Test
    @DisplayName("Debe instanciar Grado con valores y estado activo por defecto")
    void debeInstanciarGradoCorrectamente() {
        Grado grado = new Grado("10", "Décimo", NivelEducativo.MEDIA, 10);

        assertEquals("10", grado.getCodigo());
        assertEquals("Décimo", grado.getNombre());
        assertEquals(NivelEducativo.MEDIA, grado.getNivel());
        assertEquals(10, grado.getOrdenJerarquico());
        assertTrue(grado.getActivo());
    }

    @Test
    @DisplayName("Debe instanciar Area de conocimiento con sus atributos")
    void debeInstanciarAreaCorrectamente() {
        Area area = new Area("HUM", "Humanidades y Lengua Castellana", "Área fundamental");

        assertEquals("HUM", area.getCodigo());
        assertEquals("Humanidades y Lengua Castellana", area.getNombre());
        assertEquals("Área fundamental", area.getDescripcion());
        assertTrue(area.getActivo());
    }

    @Test
    @DisplayName("Debe instanciar Asignatura vinculada a un Area")
    void debeInstanciarAsignaturaConArea() {
        Area area = new Area("MAT", "Matemáticas", "Área numérica");
        Asignatura asignatura = new Asignatura(area, "TRIG", "Trigonometría", "Asignatura de grado 10");

        assertEquals("TRIG", asignatura.getCodigo());
        assertEquals("Trigonometría", asignatura.getNombre());
        assertEquals(area, asignatura.getArea());
        assertTrue(asignatura.getActivo());
    }

    @Test
    @DisplayName("Debe instanciar GradoAsignatura con intensidad horaria y optatividad")
    void debeInstanciarGradoAsignaturaCorrectamente() {
        Grado grado = new Grado("11", "Undécimo", NivelEducativo.MEDIA, 11);
        Asignatura asignatura = new Asignatura(null, "FILO", "Filosofía", "Materia de humanidades");

        GradoAsignatura relacion = new GradoAsignatura(grado, asignatura, 3, false);

        assertEquals(grado, relacion.getGrado());
        assertEquals(asignatura, relacion.getAsignatura());
        assertEquals(3, relacion.getIntensidadHoraria());
        assertFalse(relacion.getEsOptativa());
        assertTrue(relacion.getActivo());
    }

    @Test
    @DisplayName("Debe instanciar AnioLectivo en estado PLANIFICACION por defecto")
    void debeInstanciarAnioLectivoCorrectamente() {
        LocalDate inicio = LocalDate.of(2026, 1, 15);
        LocalDate fin = LocalDate.of(2026, 11, 30);
        AnioLectivo anio = new AnioLectivo(2026, "Año Escolar 2026", inicio, fin);

        assertEquals(2026, anio.getAnio());
        assertEquals("Año Escolar 2026", anio.getNombre());
        assertEquals(inicio, anio.getFechaInicio());
        assertEquals(fin, anio.getFechaFin());
        assertEquals(EstadoAnioLectivo.PLANIFICACION, anio.getEstado());
    }

    @Test
    @DisplayName("Debe instanciar PeriodoAcademico vinculado a AnioLectivo en estado CERRADO por defecto")
    void debeInstanciarPeriodoAcademicoCorrectamente() {
        AnioLectivo anio = new AnioLectivo(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        PeriodoAcademico periodo = new PeriodoAcademico(
                anio,
                1,
                "Primer Periodo",
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2026, 4, 15),
                new BigDecimal("25.00")
        );

        assertEquals(anio, periodo.getAnioLectivo());
        assertEquals(1, periodo.getNumero());
        assertEquals("Primer Periodo", periodo.getNombre());
        assertEquals(new BigDecimal("25.00"), periodo.getPorcentaje());
        assertEquals(EstadoPeriodo.CERRADO, periodo.getEstado());
    }

    @Test
    @DisplayName("Debe instanciar Grupo con jornada y cupo máximo por defecto")
    void debeInstanciarGrupoCorrectamente() {
        AnioLectivo anio = new AnioLectivo(2026, "Año 2026", LocalDate.of(2026, 1, 15), LocalDate.of(2026, 11, 30));
        Grado grado = new Grado("06", "Sexto", NivelEducativo.BASICA_SECUNDARIA, 6);

        Grupo grupo = new Grupo(anio, grado, "A", Jornada.MANANA, 35);

        assertEquals(anio, grupo.getAnioLectivo());
        assertEquals(grado, grupo.getGrado());
        assertEquals("A", grupo.getNombre());
        assertEquals(Jornada.MANANA, grupo.getJornada());
        assertEquals(35, grupo.getCupoMaximo());
        assertTrue(grupo.getActivo());
    }
}
