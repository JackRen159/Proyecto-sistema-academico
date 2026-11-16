package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActividadEvaluativaResponse {

    private Long id;
    private Long asignacionAcademicaId;
    private String profesorNombreCompleto;
    private Long grupoId;
    private String grupoNombre;
    private Long asignaturaId;
    private String asignaturaNombre;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private Integer periodoNumero;
    private String nombre;
    private String descripcion;
    private BigDecimal porcentaje;
    private LocalDate fechaEvaluacion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    public ActividadEvaluativaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAsignacionAcademicaId() {
        return asignacionAcademicaId;
    }

    public void setAsignacionAcademicaId(Long asignacionAcademicaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
    }

    public String getProfesorNombreCompleto() {
        return profesorNombreCompleto;
    }

    public void setProfesorNombreCompleto(String profesorNombreCompleto) {
        this.profesorNombreCompleto = profesorNombreCompleto;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Integer getPeriodoNumero() {
        return periodoNumero;
    }

    public void setPeriodoNumero(Integer periodoNumero) {
        this.periodoNumero = periodoNumero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
