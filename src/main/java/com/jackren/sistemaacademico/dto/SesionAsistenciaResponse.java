package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoRegistroAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SesionAsistenciaResponse {

    private Long id;
    private Long asignacionAcademicaId;
    private String profesorNombreCompleto;
    private String asignaturaNombre;
    private Long grupoId;
    private String grupoNombre;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private Integer periodoNumero;
    private LocalDate fecha;
    private Integer bloqueHorario;
    private TipoRegistroAsistencia tipoRegistro;
    private String tema;
    private String observaciones;
    private Long usuarioCreadorId;
    private LocalDateTime fechaCreacion;

    public SesionAsistenciaResponse() {
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

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getBloqueHorario() {
        return bloqueHorario;
    }

    public void setBloqueHorario(Integer bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
    }

    public TipoRegistroAsistencia getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistroAsistencia tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getUsuarioCreadorId() {
        return usuarioCreadorId;
    }

    public void setUsuarioCreadorId(Long usuarioCreadorId) {
        this.usuarioCreadorId = usuarioCreadorId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
