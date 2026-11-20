package com.jackren.sistemaacademico.dto;

public class ControlSesionesDocenteResponse {

    private Long asignacionId;
    private Long docenteId;
    private String docenteNombreCompleto;
    private Long grupoId;
    private String grupoNombre;
    private Long asignaturaId;
    private String asignaturaNombre;
    private Long sesionesRegistradas;
    private Integer sesionesPendientesEstimadas;
    private Long excusasPendientesRevision;

    public ControlSesionesDocenteResponse() {
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

    public Long getSesionesRegistradas() {
        return sesionesRegistradas;
    }

    public void setSesionesRegistradas(Long sesionesRegistradas) {
        this.sesionesRegistradas = sesionesRegistradas;
    }

    public Integer getSesionesPendientesEstimadas() {
        return sesionesPendientesEstimadas;
    }

    public void setSesionesPendientesEstimadas(Integer sesionesPendientesEstimadas) {
        this.sesionesPendientesEstimadas = sesionesPendientesEstimadas;
    }

    public Long getExcusasPendientesRevision() {
        return excusasPendientesRevision;
    }

    public void setExcusasPendientesRevision(Long excusasPendientesRevision) {
        this.excusasPendientesRevision = excusasPendientesRevision;
    }
}
