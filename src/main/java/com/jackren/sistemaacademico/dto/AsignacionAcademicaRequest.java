package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AsignacionAcademicaRequest {

    @NotNull(message = "El profesor es obligatorio")
    private Long profesorId;

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    @NotNull(message = "La asignatura es obligatoria")
    private Long asignaturaId;

    @NotNull(message = "La intensidad horaria semanal es obligatoria")
    @Min(value = 1, message = "La intensidad horaria semanal debe ser al menos de 1 hora")
    private Integer intensidadHorariaSemanal = 1;

    private Boolean esDirectorGrupo = false;

    public AsignacionAcademicaRequest() {
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Integer getIntensidadHorariaSemanal() {
        return intensidadHorariaSemanal;
    }

    public void setIntensidadHorariaSemanal(Integer intensidadHorariaSemanal) {
        this.intensidadHorariaSemanal = intensidadHorariaSemanal;
    }

    public Boolean getEsDirectorGrupo() {
        return esDirectorGrupo;
    }

    public void setEsDirectorGrupo(Boolean esDirectorGrupo) {
        this.esDirectorGrupo = esDirectorGrupo;
    }
}
