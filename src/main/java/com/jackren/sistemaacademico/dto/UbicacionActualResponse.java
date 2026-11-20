package com.jackren.sistemaacademico.dto;

import java.time.LocalTime;

public class UbicacionActualResponse {

    private String tipoConsulta;
    private Long id;
    private String nombre;
    private String grupoNombre;
    private String asignaturaNombre;
    private String profesorNombre;
    private String aulaCodigo;
    private String aulaNombre;
    private String sede;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private Boolean claseEnCurso;

    public UbicacionActualResponse() {
    }

    public String getTipoConsulta() {
        return tipoConsulta;
    }

    public void setTipoConsulta(String tipoConsulta) {
        this.tipoConsulta = tipoConsulta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
    }

    public String getAulaCodigo() {
        return aulaCodigo;
    }

    public void setAulaCodigo(String aulaCodigo) {
        this.aulaCodigo = aulaCodigo;
    }

    public String getAulaNombre() {
        return aulaNombre;
    }

    public void setAulaNombre(String aulaNombre) {
        this.aulaNombre = aulaNombre;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public Boolean getClaseEnCurso() {
        return claseEnCurso;
    }

    public void setClaseEnCurso(Boolean claseEnCurso) {
        this.claseEnCurso = claseEnCurso;
    }
}
