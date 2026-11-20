package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsignacionAcademica;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class RelevoDocenteRequest {

    @NotNull(message = "El docente de reemplazo es obligatorio")
    private Long docenteReemplazoId;

    @NotNull(message = "El tipo de relevo es obligatorio")
    private EstadoAsignacionAcademica tipoRelevo = EstadoAsignacionAcademica.REEMPLAZO_TEMPORAL;

    private LocalDate fechaInicioVigencia;

    private LocalDate fechaFinVigencia;

    @NotBlank(message = "El motivo del relevo docente es obligatorio")
    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivo;

    public RelevoDocenteRequest() {
    }

    public Long getDocenteReemplazoId() {
        return docenteReemplazoId;
    }

    public void setDocenteReemplazoId(Long docenteReemplazoId) {
        this.docenteReemplazoId = docenteReemplazoId;
    }

    public EstadoAsignacionAcademica getTipoRelevo() {
        return tipoRelevo;
    }

    public void setTipoRelevo(EstadoAsignacionAcademica tipoRelevo) {
        this.tipoRelevo = tipoRelevo;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
