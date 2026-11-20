package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoOrigenConvalidacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class ConvalidacionCalificacionRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    @NotNull(message = "El tipo de origen es obligatorio")
    private TipoOrigenConvalidacion tipoOrigen;

    @Size(max = 150, message = "La institución de origen no puede superar 150 caracteres")
    private String institucionOrigen;

    @Size(max = 255, message = "La URL del acta de soporte no puede superar 255 caracteres")
    private String actaSoporteUrl;

    @Size(max = 1000, message = "Las observaciones no pueden superar 1000 caracteres")
    private String observaciones;

    private LocalDate fechaConvalidacion;

    @NotEmpty(message = "Debe incluir al menos una asignatura convalidada")
    @Valid
    private List<ConvalidacionDetalleRequest> detalles;

    public ConvalidacionCalificacionRequest() {
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

    public TipoOrigenConvalidacion getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(TipoOrigenConvalidacion tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public String getInstitucionOrigen() {
        return institucionOrigen;
    }

    public void setInstitucionOrigen(String institucionOrigen) {
        this.institucionOrigen = institucionOrigen;
    }

    public String getActaSoporteUrl() {
        return actaSoporteUrl;
    }

    public void setActaSoporteUrl(String actaSoporteUrl) {
        this.actaSoporteUrl = actaSoporteUrl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaConvalidacion() {
        return fechaConvalidacion;
    }

    public void setFechaConvalidacion(LocalDate fechaConvalidacion) {
        this.fechaConvalidacion = fechaConvalidacion;
    }

    public List<ConvalidacionDetalleRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ConvalidacionDetalleRequest> detalles) {
        this.detalles = detalles;
    }
}
