package com.jackren.sistemaacademico.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class PlanillaAsistenciaLoteRequest {

    @NotNull(message = "La sesión de asistencia es obligatoria")
    private Long sesionId;

    @NotEmpty(message = "Debe proporcionar al menos un registro de asistencia")
    private List<@Valid RegistroAsistenciaItemRequest> registros;

    public PlanillaAsistenciaLoteRequest() {
    }

    public PlanillaAsistenciaLoteRequest(Long sesionId, List<RegistroAsistenciaItemRequest> registros) {
        this.sesionId = sesionId;
        this.registros = registros;
    }

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public List<RegistroAsistenciaItemRequest> getRegistros() {
        return registros;
    }

    public void setRegistros(List<RegistroAsistenciaItemRequest> registros) {
        this.registros = registros;
    }
}
