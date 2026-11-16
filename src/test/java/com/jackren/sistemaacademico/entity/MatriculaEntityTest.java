package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class MatriculaEntityTest {

    @Test
    @DisplayName("Debe instanciar matrícula con valores y relaciones coherentes")
    void debeInstanciarMatricula() {
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);
        estudiante.setNombres("Juan");
        estudiante.setApellidos("Pérez");

        Grupo grupo = new Grupo();
        grupo.setId(10L);
        grupo.setNombre("10-A");

        AnioLectivo anioLectivo = new AnioLectivo(2026, "Año 2026", null, null);
        anioLectivo.setId(100L);

        Matricula matricula = new Matricula("MAT-2026-0001", estudiante, grupo, anioLectivo);
        matricula.setFolio("F-123");
        matricula.setTipoMatricula(TipoMatricula.NUEVO);

        assertThat(matricula.getNumeroMatricula()).isEqualTo("MAT-2026-0001");
        assertThat(matricula.getEstudiante().getId()).isEqualTo(1L);
        assertThat(matricula.getGrupo().getId()).isEqualTo(10L);
        assertThat(matricula.getAnioLectivo().getId()).isEqualTo(100L);
        assertThat(matricula.getEstado()).isEqualTo(EstadoMatricula.MATRICULADA);
        assertThat(matricula.getTipoMatricula()).isEqualTo(TipoMatricula.NUEVO);
        assertThat(matricula.getFolio()).isEqualTo("F-123");
        assertThat(matricula.getFechaMatricula()).isEqualTo(LocalDate.now());
        assertThat(matricula.getActivo()).isTrue();
    }

    @Test
    @DisplayName("Debe instanciar registro de historial de traslado de grupo")
    void debeInstanciarHistorialTraslado() {
        Matricula matricula = new Matricula();
        matricula.setId(1L);

        Grupo origen = new Grupo();
        origen.setId(10L);

        Grupo destino = new Grupo();
        destino.setId(11L);

        Usuario admin = new Usuario();
        admin.setId(5L);

        HistorialTrasladoMatricula historial = new HistorialTrasladoMatricula(
                matricula, origen, destino, "Cambio a jornada de la tarde", admin
        );

        assertThat(historial.getMatricula().getId()).isEqualTo(1L);
        assertThat(historial.getGrupoOrigen().getId()).isEqualTo(10L);
        assertThat(historial.getGrupoDestino().getId()).isEqualTo(11L);
        assertThat(historial.getMotivo()).isEqualTo("Cambio a jornada de la tarde");
        assertThat(historial.getUsuario().getId()).isEqualTo(5L);
    }
}
