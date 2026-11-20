package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BloqueCurricularAsignacionRequest {

    @NotNull(message = "El profesor es obligatorio")
    private Long profesorId;

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    private List<Long> asignaturasExcluidasIds = new ArrayList<>();

    private Boolean esDirectorGrupo = true;

    public BloqueCurricularAsignacionRequest() {
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

    public List<Long> getAsignaturasExcluidasIds() {
        return asignaturasExcluidasIds;
    }

    public void setAsignaturasExcluidasIds(List<Long> asignaturasExcluidasIds) {
        this.asignaturasExcluidasIds = asignaturasExcluidasIds != null ? asignaturasExcluidasIds : new ArrayList<>();
    }

    public Boolean getEsDirectorGrupo() {
        return esDirectorGrupo;
    }

    public void setEsDirectorGrupo(Boolean esDirectorGrupo) {
        this.esDirectorGrupo = esDirectorGrupo;
    }
}
