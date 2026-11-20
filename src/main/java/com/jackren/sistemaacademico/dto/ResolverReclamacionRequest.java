package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DecisionReclamacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ResolverReclamacionRequest {

    @NotNull(message = "La decisión de la reclamación es obligatoria")
    private DecisionReclamacion decision;

    @NotBlank(message = "El motivo de la decisión es obligatorio")
    @Size(max = 2000, message = "El motivo de la decisión no puede superar 2000 caracteres")
    private String motivo;

    private BigDecimal nuevaNotaAprobada;

    public ResolverReclamacionRequest() {
    }

    public DecisionReclamacion getDecision() {
        return decision;
    }

    public void setDecision(DecisionReclamacion decision) {
        this.decision = decision;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public BigDecimal getNuevaNotaAprobada() {
        return nuevaNotaAprobada;
    }

    public void setNuevaNotaAprobada(BigDecimal nuevaNotaAprobada) {
        this.nuevaNotaAprobada = nuevaNotaAprobada;
    }
}
