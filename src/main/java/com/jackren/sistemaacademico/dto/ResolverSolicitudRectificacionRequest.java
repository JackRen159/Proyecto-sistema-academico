package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoSolicitudRectificacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ResolverSolicitudRectificacionRequest {

    @NotNull(message = "El estado resolutivo es obligatorio (APROBADA o RECHAZADA_MOTIVADA)")
    private EstadoSolicitudRectificacion estado;

    @NotBlank(message = "La respuesta motivada es obligatoria")
    private String respuestaMotivada;

    public ResolverSolicitudRectificacionRequest() {
    }

    public ResolverSolicitudRectificacionRequest(EstadoSolicitudRectificacion estado, String respuestaMotivada) {
        this.estado = estado;
        this.respuestaMotivada = respuestaMotivada;
    }

    public EstadoSolicitudRectificacion getEstado() { return estado; }
    public void setEstado(EstadoSolicitudRectificacion estado) { this.estado = estado; }
    public String getRespuestaMotivada() { return respuestaMotivada; }
    public void setRespuestaMotivada(String respuestaMotivada) { this.respuestaMotivada = respuestaMotivada; }
}
