package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoCalificacionReclamacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReclamacionCalificacionRequest {

    @NotNull(message = "El tipo de calificación es obligatorio")
    private TipoCalificacionReclamacion tipoCalificacion;

    @NotNull(message = "El ID de referencia de la calificación es obligatorio")
    private Long referenciaId;

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotBlank(message = "El motivo de la reclamación es obligatorio")
    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    private String motivoReclamacion;

    @Size(max = 255, message = "La URL del soporte no puede superar 255 caracteres")
    private String soporteUrl;

    public ReclamacionCalificacionRequest() {
    }

    public TipoCalificacionReclamacion getTipoCalificacion() {
        return tipoCalificacion;
    }

    public void setTipoCalificacion(TipoCalificacionReclamacion tipoCalificacion) {
        this.tipoCalificacion = tipoCalificacion;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getMotivoReclamacion() {
        return motivoReclamacion;
    }

    public void setMotivoReclamacion(String motivoReclamacion) {
        this.motivoReclamacion = motivoReclamacion;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }
}
