package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDiaNoLectivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class DiaNoLectivoRequest {

    @NotBlank(message = "El nombre del día o evento no lectivo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El tipo de día no lectivo es obligatorio")
    private TipoDiaNoLectivo tipo = TipoDiaNoLectivo.FESTIVO;

    @Size(max = 255, message = "El motivo no puede superar los 255 caracteres")
    private String motivo;

    @Size(max = 100, message = "La sede no puede superar los 100 caracteres")
    private String sede;

    public DiaNoLectivoRequest() {
    }

    public DiaNoLectivoRequest(String nombre, LocalDate fechaInicio, LocalDate fechaFin, TipoDiaNoLectivo tipo, String motivo) {
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo != null ? tipo : TipoDiaNoLectivo.FESTIVO;
        this.motivo = motivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public TipoDiaNoLectivo getTipo() {
        return tipo;
    }

    public void setTipo(TipoDiaNoLectivo tipo) {
        this.tipo = tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }
}
