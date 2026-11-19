package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.OrigenConsentimiento;
import jakarta.validation.constraints.NotNull;

public class RegistrarConsentimientoRequest {

    @NotNull(message = "El ID de la versión de política es obligatorio")
    private Long politicaId;

    private OrigenConsentimiento origen = OrigenConsentimiento.LOGIN_PRIMER_INGRESO;

    public RegistrarConsentimientoRequest() {
    }

    public RegistrarConsentimientoRequest(Long politicaId, OrigenConsentimiento origen) {
        this.politicaId = politicaId;
        this.origen = origen;
    }

    public Long getPoliticaId() {
        return politicaId;
    }

    public void setPoliticaId(Long politicaId) {
        this.politicaId = politicaId;
    }

    public OrigenConsentimiento getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenConsentimiento origen) {
        this.origen = origen;
    }
}
