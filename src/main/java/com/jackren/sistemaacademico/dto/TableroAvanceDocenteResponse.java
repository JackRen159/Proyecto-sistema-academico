package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;

public class TableroAvanceDocenteResponse {

    private Long asignacionId;
    private Long docenteId;
    private String docenteNombreCompleto;
    private Long grupoId;
    private String grupoNombre;
    private Long asignaturaId;
    private String asignaturaNombre;
    private BigDecimal porcentajePlaneado;
    private Integer totalActividades;
    private Integer estudiantesMatriculados;
    private Integer calificacionesRegistradas;
    private Integer calificacionesPendientes;
    private BigDecimal porcentajeAvance;
    private Boolean planeacionCompleta;

    public TableroAvanceDocenteResponse() {
    }

    public Long getAsignacionId() {
        return asignacionId;
    }

    public void setAsignacionId(Long asignacionId) {
        this.asignacionId = asignacionId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombreCompleto() {
        return docenteNombreCompleto;
    }

    public void setDocenteNombreCompleto(String docenteNombreCompleto) {
        this.docenteNombreCompleto = docenteNombreCompleto;
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

    public BigDecimal getPorcentajePlaneado() {
        return porcentajePlaneado;
    }

    public void setPorcentajePlaneado(BigDecimal porcentajePlaneado) {
        this.porcentajePlaneado = porcentajePlaneado;
    }

    public Integer getTotalActividades() {
        return totalActividades;
    }

    public void setTotalActividades(Integer totalActividades) {
        this.totalActividades = totalActividades;
    }

    public Integer getEstudiantesMatriculados() {
        return estudiantesMatriculados;
    }

    public void setEstudiantesMatriculados(Integer estudiantesMatriculados) {
        this.estudiantesMatriculados = estudiantesMatriculados;
    }

    public Integer getCalificacionesRegistradas() {
        return calificacionesRegistradas;
    }

    public void setCalificacionesRegistradas(Integer calificacionesRegistradas) {
        this.calificacionesRegistradas = calificacionesRegistradas;
    }

    public Integer getCalificacionesPendientes() {
        return calificacionesPendientes;
    }

    public void setCalificacionesPendientes(Integer calificacionesPendientes) {
        this.calificacionesPendientes = calificacionesPendientes;
    }

    public BigDecimal getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(BigDecimal porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
    }

    public Boolean getPlaneacionCompleta() {
        return planeacionCompleta;
    }

    public void setPlaneacionCompleta(Boolean planeacionCompleta) {
        this.planeacionCompleta = planeacionCompleta;
    }
}
