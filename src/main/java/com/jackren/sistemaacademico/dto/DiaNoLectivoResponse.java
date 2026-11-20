package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDiaNoLectivo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class DiaNoLectivoResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivoAnio;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private TipoDiaNoLectivo tipo;
    private String motivo;
    private String sede;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public DiaNoLectivoResponse(Long id, Long anioLectivoId, Integer anioLectivoAnio, String nombre,
                                LocalDate fechaInicio, LocalDate fechaFin, TipoDiaNoLectivo tipo,
                                String motivo, String sede, Boolean activo,
                                LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anioLectivoId = anioLectivoId;
        this.anioLectivoAnio = anioLectivoAnio;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.tipo = tipo;
        this.motivo = motivo;
        this.sede = sede;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public Integer getAnioLectivoAnio() {
        return anioLectivoAnio;
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

    public TipoDiaNoLectivo getTipo() {
        return tipo;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getSede() {
        return sede;
    }

    public Boolean getActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
