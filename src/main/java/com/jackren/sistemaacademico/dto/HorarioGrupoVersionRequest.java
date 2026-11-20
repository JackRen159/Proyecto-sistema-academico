package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class HorarioGrupoVersionRequest {

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    private LocalDate fechaInicioVigencia;

    private List<HorarioBloqueRequest> bloques;

    public HorarioGrupoVersionRequest() {
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public List<HorarioBloqueRequest> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioBloqueRequest> bloques) {
        this.bloques = bloques;
    }
}
