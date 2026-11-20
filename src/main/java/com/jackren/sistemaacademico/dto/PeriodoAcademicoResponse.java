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
    private LocalDate fechaInicioDigitacion;
    private LocalDate fechaFinDigitacion;
    private String numeroActaAprobacion;
    private LocalDate fechaActaAprobacion;
    private String organoAprobador;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public PeriodoAcademicoResponse(Long id, Long anioLectivoId, Integer numero, String nombre,
                                    LocalDate fechaInicio, LocalDate fechaFin, BigDecimal porcentaje,
                                    EstadoPeriodo estado, LocalDate fechaInicioDigitacion, LocalDate fechaFinDigitacion,
                                    String numeroActaAprobacion, LocalDate fechaActaAprobacion, String organoAprobador,
                                    LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anioLectivoId = anioLectivoId;
        this.numero = numero;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.porcentaje = porcentaje;
        this.estado = estado;
        this.fechaInicioDigitacion = fechaInicioDigitacion;
        this.fechaFinDigitacion = fechaFinDigitacion;
        this.numeroActaAprobacion = numeroActaAprobacion;
        this.fechaActaAprobacion = fechaActaAprobacion;
        this.organoAprobador = organoAprobador;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public PeriodoAcademicoResponse(Long id, Long anioLectivoId, Integer numero, String nombre,
                                    LocalDate fechaInicio, LocalDate fechaFin, BigDecimal porcentaje,
                                    EstadoPeriodo estado, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this(id, anioLectivoId, numero, nombre, fechaInicio, fechaFin, porcentaje, estado, null, null, null, null, null, fechaCreacion, fechaActualizacion);
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

    public LocalDate getFechaInicioDigitacion() {
        return fechaInicioDigitacion;
    }

    public LocalDate getFechaFinDigitacion() {
        return fechaFinDigitacion;
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
