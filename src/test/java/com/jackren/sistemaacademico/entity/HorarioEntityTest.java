package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas Unitarias de Entidades y Dominio - Horarios Escolares")
class HorarioEntityTest {

    @Test
    @DisplayName("Aula debe instanciarse con capacidad y tipo de espacio físico")
    void aula_InstanciacionCorrecta() {
        Aula aula = new Aula("SEDE_PRINCIPAL", "101", "Salón 101", TipoEspacioFisico.AULA_ESTANDAR, 40);

        assertEquals("SEDE_PRINCIPAL", aula.getSede());
        assertEquals("101", aula.getCodigo());
        assertEquals("Salón 101", aula.getNombre());
        assertEquals(TipoEspacioFisico.AULA_ESTANDAR, aula.getTipoEspacio());
        assertEquals(40, aula.getCapacidadMaxima());
        assertEquals(EstadoAula.DISPONIBLE, aula.getEstado());
        assertFalse(aula.getEsMultigrupo());
    }

    @Test
    @DisplayName("PlantillaFranjaHoraria debe validar horas de inicio y fin")
    void plantillaFranja_InstanciacionCorrecta() {
        AnioLectivo anio = new AnioLectivo();
        anio.setId(1L);

        PlantillaFranjaHoraria franja = new PlantillaFranjaHoraria(
                anio,
                Jornada.MANANA,
                1,
                LocalTime.of(7, 0),
                LocalTime.of(7, 55),
                TipoFranjaHoraria.BLOQUE_LECTIVO,
                "Primer Bloque de Clase"
        );

        assertEquals(Jornada.MANANA, franja.getJornada());
        assertEquals(1, franja.getNumeroBloque());
        assertEquals(LocalTime.of(7, 0), franja.getHoraInicio());
        assertEquals(LocalTime.of(7, 55), franja.getHoraFin());
        assertEquals(TipoFranjaHoraria.BLOQUE_LECTIVO, franja.getTipoFranja());
        assertTrue(franja.getHoraInicio().isBefore(franja.getHoraFin()));
    }

    @Test
    @DisplayName("HorarioGrupoVersion debe instanciarse en estado BORRADOR")
    void horarioGrupoVersion_InstanciacionCorrecta() {
        Grupo grupo = new Grupo();
        grupo.setId(10L);

        HorarioGrupoVersion version = new HorarioGrupoVersion(grupo, 1);

        assertEquals(grupo, version.getGrupo());
        assertEquals(1, version.getNumeroVersion());
        assertEquals(EstadoHorarioVersion.BORRADOR, version.getEstado());
        assertNotNull(version.getBloques());
    }

    @Test
    @DisplayName("HorarioBloque debe vincular día de semana, franja y aula")
    void horarioBloque_InstanciacionCorrecta() {
        HorarioGrupoVersion version = new HorarioGrupoVersion();
        AsignacionAcademica asig = new AsignacionAcademica();
        Aula aula = new Aula();
        PlantillaFranjaHoraria franja = new PlantillaFranjaHoraria();

        HorarioBloque bloque = new HorarioBloque(version, asig, aula, DiaSemana.LUNES, franja);

        assertEquals(DiaSemana.LUNES, bloque.getDiaSemana());
        assertEquals(version, bloque.getHorarioVersion());
        assertEquals(asig, bloque.getAsignacionAcademica());
        assertEquals(aula, bloque.getAula());
        assertEquals(franja, bloque.getFranjaHoraria());
    }

    @Test
    @DisplayName("NovedadHorario debe registrar permutas y cambios temporales")
    void novedadHorario_InstanciacionCorrecta() {
        HorarioBloque bloque = new HorarioBloque();
        NovedadHorario novedad = new NovedadHorario();
        novedad.setHorarioBloque(bloque);
        novedad.setTipoNovedad(TipoNovedadHorario.CAMBIO_AULA_TEMPORAL);
        novedad.setFechaInicio(LocalDate.of(2026, 3, 10));
        novedad.setFechaFin(LocalDate.of(2026, 3, 15));
        novedad.setMotivo("Salón en pintura y mantenimiento eléctrico");

        assertEquals(TipoNovedadHorario.CAMBIO_AULA_TEMPORAL, novedad.getTipoNovedad());
        assertEquals(LocalDate.of(2026, 3, 10), novedad.getFechaInicio());
        assertEquals(LocalDate.of(2026, 3, 15), novedad.getFechaFin());
        assertEquals("Salón en pintura y mantenimiento eléctrico", novedad.getMotivo());
    }
}
