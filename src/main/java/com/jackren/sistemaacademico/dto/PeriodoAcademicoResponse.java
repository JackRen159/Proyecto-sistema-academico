package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoPeriodo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PeriodoAcademicoResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer numero;
    private String nombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal porcentaje;
    private EstadoPeriodo estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public PeriodoAcademicoResponse(Long id, Long anioLectivoId, Integer numero, String nombre,
                                    LocalDate fechaInicio, LocalDate fechaFin, BigDecimal porcentaje,
                                    EstadoPeriodo estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anioLectivoId = anioLectivoId;
        this.numero = numero;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.porcentaje = porcentaje;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public Long getId() {
        return id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public Integer getNumero() {
        return numero;
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

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public EstadoPeriodo getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
