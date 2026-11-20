package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CierrePeriodoRequest {

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoId;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivoCierre;

    private Boolean permitirEstudiantesAplazadosIncapacidad = true;

    public CierrePeriodoRequest() {
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getMotivoCierre() {
        return motivoCierre;
    }

    public void setMotivoCierre(String motivoCierre) {
        this.motivoCierre = motivoCierre;
    }

    public Boolean getPermitirEstudiantesAplazadosIncapacidad() {
        return permitirEstudiantesAplazadosIncapacidad;
    }

    public void setPermitirEstudiantesAplazadosIncapacidad(Boolean permitirEstudiantesAplazadosIncapacidad) {
        this.permitirEstudiantesAplazadosIncapacidad = permitirEstudiantesAplazadosIncapacidad;
    }
}
