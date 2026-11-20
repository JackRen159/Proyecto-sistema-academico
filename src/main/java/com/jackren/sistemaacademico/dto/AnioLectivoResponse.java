package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import com.jackren.sistemaacademico.entity.TipoCalendario;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnioLectivoResponse {

    private Long id;
    private Integer anio;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoAnioLectivo estado;
    private TipoCalendario tipoCalendario;
    private Integer semanasLectivas;
    private String numeroActaAprobacion;
    private LocalDate fechaActaAprobacion;
    private String organoAprobador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AnioLectivoResponse(Long id, Integer anio, String nombre, LocalDate fechaInicio, LocalDate fechaFin,
                               EstadoAnioLectivo estado, TipoCalendario tipoCalendario, Integer semanasLectivas,
                               String numeroActaAprobacion, LocalDate fechaActaAprobacion, String organoAprobador,
                               LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anio = anio;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.tipoCalendario = tipoCalendario != null ? tipoCalendario : TipoCalendario.CALENDARIO_A;
        this.semanasLectivas = semanasLectivas != null ? semanasLectivas : 40;
        this.numeroActaAprobacion = numeroActaAprobacion;
        this.fechaActaAprobacion = fechaActaAprobacion;
        this.organoAprobador = organoAprobador;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public AnioLectivoResponse(Long id, Integer anio, String nombre, LocalDate fechaInicio, LocalDate fechaFin,
                               EstadoAnioLectivo estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this(id, anio, nombre, fechaInicio, fechaFin, estado, TipoCalendario.CALENDARIO_A, 40, null, null, null, fechaCreacion, fechaActualizacion);
    }

    public Long getId() {
        return id;
    }

    public Integer getAnio() {
        return anio;
    }

    public String getNombre() {
        return nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public EstadoAnioLectivo getEstado() {
        return estado;
    }

    public TipoCalendario getTipoCalendario() {
        return tipoCalendario;
    }

    public Integer getSemanasLectivas() {
        return semanasLectivas;
    }

    public String getNumeroActaAprobacion() {
        return numeroActaAprobacion;
    }

    public LocalDate getFechaActaAprobacion() {
        return fechaActaAprobacion;
    }

    public String getOrganoAprobador() {
        return organoAprobador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
