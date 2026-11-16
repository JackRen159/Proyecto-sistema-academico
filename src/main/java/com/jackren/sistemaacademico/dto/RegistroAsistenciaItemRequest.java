package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsistencia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistroAsistenciaItemRequest {

    @NotNull(message = "La matrícula del estudiante es obligatoria")
    private Long matriculaId;

    @NotNull(message = "El estado de asistencia es obligatorio")
    private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;

    @Min(value = 0, message = "Los minutos de retardo no pueden ser negativos")
    private Integer minutosRetardo = 0;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public RegistroAsistenciaItemRequest() {
    }

    public RegistroAsistenciaItemRequest(Long matriculaId, EstadoAsistencia estado, Integer minutosRetardo, String observaciones) {
        this.matriculaId = matriculaId;
        this.estado = estado != null ? estado : EstadoAsistencia.PRESENTE;
        this.minutosRetardo = minutosRetardo != null ? minutosRetardo : 0;
        this.observaciones = observaciones;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public Integer getMinutosRetardo() {
        return minutosRetardo;
    }

    public void setMinutosRetardo(Integer minutosRetardo) {
        this.minutosRetardo = minutosRetardo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
