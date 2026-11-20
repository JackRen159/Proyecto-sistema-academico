package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoCalendario;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AnioLectivoRequest {

    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotBlank(message = "El nombre del año lectivo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    private TipoCalendario tipoCalendario = TipoCalendario.CALENDARIO_A;

    private Integer semanasLectivas = 40;

    private String numeroActaAprobacion;

    private LocalDate fechaActaAprobacion;

    private String organoAprobador;

    public AnioLectivoRequest() {
    }

    public AnioLectivoRequest(Integer anio, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        this.anio = anio;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
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

    public TipoCalendario getTipoCalendario() {
        return tipoCalendario;
    }

    public void setTipoCalendario(TipoCalendario tipoCalendario) {
        this.tipoCalendario = tipoCalendario;
    }

    public Integer getSemanasLectivas() {
        return semanasLectivas;
    }

    public void setSemanasLectivas(Integer semanasLectivas) {
        this.semanasLectivas = semanasLectivas;
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
