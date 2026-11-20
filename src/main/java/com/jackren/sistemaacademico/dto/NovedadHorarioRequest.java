package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoNovedadHorario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class NovedadHorarioRequest {

    @NotNull(message = "El bloque horario es obligatorio")
    private Long horarioBloqueId;

    @NotNull(message = "El tipo de novedad es obligatorio")
    private TipoNovedadHorario tipoNovedad;

    private Long aulaReemplazoId;

    private Long docenteReemplazoId;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotBlank(message = "El motivo es obligatorio")
    @Size(max = 2000, message = "El motivo no puede superar 2000 caracteres")
    private String motivo;

    public NovedadHorarioRequest() {
    }

    public Long getHorarioBloqueId() {
        return horarioBloqueId;
    }

    public void setHorarioBloqueId(Long horarioBloqueId) {
        this.horarioBloqueId = horarioBloqueId;
    }

    public TipoNovedadHorario getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadHorario tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public Long getAulaReemplazoId() {
        return aulaReemplazoId;
    }

    public void setAulaReemplazoId(Long aulaReemplazoId) {
        this.aulaReemplazoId = aulaReemplazoId;
    }

    public Long getDocenteReemplazoId() {
        return docenteReemplazoId;
    }

    public void setDocenteReemplazoId(Long docenteReemplazoId) {
        this.docenteReemplazoId = docenteReemplazoId;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
