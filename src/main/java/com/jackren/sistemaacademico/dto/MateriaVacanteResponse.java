package com.jackren.sistemaacademico.dto;

public class MateriaVacanteResponse {

    private Long grupoId;
    private String grupoNombre;
    private Long gradoId;
    private String gradoNombre;
    private Long asignaturaId;
    private String asignaturaNombre;
    private String asignaturaCodigo;
    private Integer intensidadHoraria;
    private Boolean tieneDocenteAsignado;
    private Long profesorId;
    private String profesorNombre;
    private String estadoAsignacion;

    public MateriaVacanteResponse() {
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

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
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

    public String getAsignaturaCodigo() {
        return asignaturaCodigo;
    }

    public void setAsignaturaCodigo(String asignaturaCodigo) {
        this.asignaturaCodigo = asignaturaCodigo;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

    public Boolean getTieneDocenteAsignado() {
        return tieneDocenteAsignado;
    }

    public void setTieneDocenteAsignado(Boolean tieneDocenteAsignado) {
        this.tieneDocenteAsignado = tieneDocenteAsignado;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }

    public String getEstadoAsignacion() {
        return estadoAsignacion;
    }

    public void setEstadoAsignacion(String estadoAsignacion) {
        this.estadoAsignacion = estadoAsignacion;
    }
}
