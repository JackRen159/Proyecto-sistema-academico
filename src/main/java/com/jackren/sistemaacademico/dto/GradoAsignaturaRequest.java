package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class GradoAsignaturaRequest {

    @NotNull(message = "El ID del grado es obligatorio")
    private Long gradoId;

    @NotNull(message = "El ID de la asignatura es obligatorio")
    private Long asignaturaId;

    @NotNull(message = "La intensidad horaria es obligatoria")
    @Min(value = 1, message = "La intensidad horaria debe ser mayor a 0")
    private Integer intensidadHoraria = 1;

    private Boolean esOptativa = false;

    public GradoAsignaturaRequest() {
    }

    public GradoAsignaturaRequest(Long gradoId, Long asignaturaId, Integer intensidadHoraria, Boolean esOptativa) {
        this.gradoId = gradoId;
        this.asignaturaId = asignaturaId;
        this.intensidadHoraria = intensidadHoraria;
        this.esOptativa = esOptativa != null ? esOptativa : false;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

    public Boolean getEsOptativa() {
        return esOptativa;
    }

    public void setEsOptativa(Boolean esOptativa) {
        this.esOptativa = esOptativa;
    }
}
