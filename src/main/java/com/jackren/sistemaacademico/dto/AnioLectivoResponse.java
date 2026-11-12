package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AnioLectivoResponse {

    private Long id;
    private Integer anio;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoAnioLectivo estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AnioLectivoResponse(Long id, Integer anio, String nombre, LocalDate fechaInicio, LocalDate fechaFin,
                               EstadoAnioLectivo estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anio = anio;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
