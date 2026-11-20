package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoCalificacionActividad;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroCalificacionRequest {

    @NotNull(message = "La matrícula del estudiante es obligatoria")
    private Long matriculaId;

    private BigDecimal valor;

    private EstadoCalificacionActividad estado = EstadoCalificacionActividad.CALIFICADA;

    private LocalDate fechaSupletoria;

    private String valorCualitativo;

    private String observacionDescriptiva;

    private String motivoCorreccion;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public RegistroCalificacionRequest() {
    }

    public RegistroCalificacionRequest(Long matriculaId, BigDecimal valor, String observaciones) {
        this.matriculaId = matriculaId;
        this.valor = valor;
        this.observaciones = observaciones;
        this.estado = EstadoCalificacionActividad.CALIFICADA;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public EstadoCalificacionActividad getEstado() {
        return estado;
    }

    public void setEstado(EstadoCalificacionActividad estado) {
        this.estado = estado;
    }

    public LocalDate getFechaSupletoria() {
        return fechaSupletoria;
    }

    public void setFechaSupletoria(LocalDate fechaSupletoria) {
        this.fechaSupletoria = fechaSupletoria;
    }

    public String getValorCualitativo() {
        return valorCualitativo;
    }

    public void setValorCualitativo(String valorCualitativo) {
        this.valorCualitativo = valorCualitativo;
    }

    public String getObservacionDescriptiva() {
        return observacionDescriptiva;
    }

    public void setObservacionDescriptiva(String observacionDescriptiva) {
        this.observacionDescriptiva = observacionDescriptiva;
    }

    public String getMotivoCorreccion() {
        return motivoCorreccion;
    }

    public void setMotivoCorreccion(String motivoCorreccion) {
        this.motivoCorreccion = motivoCorreccion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
