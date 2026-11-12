package com.jackren.sistemaacademico.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfesorEntityTest {

    @Test
    @DisplayName("Debe instanciar profesor con valores por defecto consistentes")
    void debeInstanciarProfesorConValoresPorDefecto() {
        Profesor profesor = new Profesor();
        profesor.setNombres("Carlos");
        profesor.setApellidos("Gómez");
        profesor.setDocumento("1020304050");
        profesor.setCorreo("carlos.gomez@colegio.edu.co");
        profesor.setTelefono("3001234567");
        profesor.setTituloAcademico("Licenciado en Matemáticas");
        profesor.setEspecialidad("Álgebra y Trigonometría");

        assertThat(profesor.getTipoDocumento()).isEqualTo(TipoDocumento.CC);
        assertThat(profesor.getActivo()).isTrue();
        assertThat(profesor.getTituloAcademico()).isEqualTo("Licenciado en Matemáticas");
        assertThat(profesor.getEspecialidad()).isEqualTo("Álgebra y Trigonometría");
    }

    @Test
    @DisplayName("Debe vincular asignación académica a profesor, grupo y asignatura")
    void debeVincularAsignacionAcademica() {
        Profesor profesor = new Profesor();
        profesor.setId(1L);

        Grupo grupo = new Grupo();
        grupo.setId(10L);

        Asignatura asignatura = new Asignatura();
        asignatura.setId(100L);

        AsignacionAcademica asignacion = new AsignacionAcademica(profesor, grupo, asignatura, 4);
        asignacion.setEsDirectorGrupo(true);

        assertThat(asignacion.getProfesor().getId()).isEqualTo(1L);
        assertThat(asignacion.getGrupo().getId()).isEqualTo(10L);
        assertThat(asignacion.getAsignatura().getId()).isEqualTo(100L);
        assertThat(asignacion.getIntensidadHorariaSemanal()).isEqualTo(4);
        assertThat(asignacion.getEsDirectorGrupo()).isTrue();
        assertThat(asignacion.getActivo()).isTrue();
    }
}
