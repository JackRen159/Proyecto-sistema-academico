package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PeriodoAcademicoRequest {

    @NotNull(message = "El número de periodo es obligatorio")
    @Min(value = 1, message = "El número de periodo debe ser al menos 1")
    @Max(value = 10, message = "El número de periodo no puede superar 10")
    private Integer numero;

    @NotBlank(message = "El nombre del periodo es obligatorio")
    @Size(max = 50, message = "El nombre del periodo no puede superar los 50 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.01", message = "El porcentaje debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100")
    private BigDecimal porcentaje;

    private LocalDate fechaInicioDigitacion;

    private LocalDate fechaFinDigitacion;

    private String numeroActaAprobacion;

    private LocalDate fechaActaAprobacion;

    private String organoAprobador;

    public PeriodoAcademicoRequest() {
    }

    public PeriodoAcademicoRequest(Integer numero, String nombre, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal porcentaje) {
        this.numero = numero;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.porcentaje = porcentaje;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
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

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaInicioDigitacion() {
        return fechaInicioDigitacion;
    }

    public void setFechaInicioDigitacion(LocalDate fechaInicioDigitacion) {
        this.fechaInicioDigitacion = fechaInicioDigitacion;
    }

    public LocalDate getFechaFinDigitacion() {
        return fechaFinDigitacion;
    }

    public void setFechaFinDigitacion(LocalDate fechaFinDigitacion) {
        this.fechaFinDigitacion = fechaFinDigitacion;
    }

    public String getNumeroActaAprobacion() {
        return numeroActaAprobacion;
    }

    public void setNumeroActaAprobacion(String numeroActaAprobacion) {
        this.numeroActaAprobacion = numeroActaAprobacion;
    }

    public LocalDate getFechaActaAprobacion() {
        return fechaActaAprobacion;
    }

    public void setFechaActaAprobacion(LocalDate fechaActaAprobacion) {
        this.fechaActaAprobacion = fechaActaAprobacion;
    }

    public String getOrganoAprobador() {
        return organoAprobador;
    }

    public void setOrganoAprobador(String organoAprobador) {
        this.organoAprobador = organoAprobador;
    }
}
