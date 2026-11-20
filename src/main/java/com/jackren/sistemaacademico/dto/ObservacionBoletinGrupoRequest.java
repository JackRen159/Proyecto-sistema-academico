package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ObservacionBoletinGrupoRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    @NotBlank(message = "La observación formativa es obligatoria")
    @Size(max = 2000, message = "La observación no puede superar 2000 caracteres")
    private String observacionFormativa;

    @Size(max = 2000, message = "Las recomendaciones pedagógicas no pueden superar 2000 caracteres")
    private String recomendacionesPedagogicas;

    public ObservacionBoletinGrupoRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getObservacionFormativa() {
        return observacionFormativa;
    }

    public void setObservacionFormativa(String observacionFormativa) {
        this.observacionFormativa = observacionFormativa;
    }

    public String getRecomendacionesPedagogicas() {
        return recomendacionesPedagogicas;
    }

    public void setRecomendacionesPedagogicas(String recomendacionesPedagogicas) {
        this.recomendacionesPedagogicas = recomendacionesPedagogicas;
    }
}
