package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActuacionSeguimiento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActuacionSeguimientoAusentismoResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private TipoActuacionSeguimiento tipoActuacion;
    private LocalDate fechaActuacion;
    private String descripcionGestion;
    private String compromisosAcordados;
    private String soporteActaUrl;
    private String usuarioActuanteNombre;
    private LocalDateTime fechaRegistro;

    public ActuacionSeguimientoAusentismoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
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

    public TipoActuacionSeguimiento getTipoActuacion() {
        return tipoActuacion;
    }

    public void setTipoActuacion(TipoActuacionSeguimiento tipoActuacion) {
        this.tipoActuacion = tipoActuacion;
    }

    public LocalDate getFechaActuacion() {
        return fechaActuacion;
    }

    public void setFechaActuacion(LocalDate fechaActuacion) {
        this.fechaActuacion = fechaActuacion;
    }

    public String getDescripcionGestion() {
        return descripcionGestion;
    }

    public void setDescripcionGestion(String descripcionGestion) {
        this.descripcionGestion = descripcionGestion;
    }

    public String getCompromisosAcordados() {
        return compromisosAcordados;
    }

    public void setCompromisosAcordados(String compromisosAcordados) {
        this.compromisosAcordados = compromisosAcordados;
    }

    public String getSoporteActaUrl() {
        return soporteActaUrl;
    }

    public void setSoporteActaUrl(String soporteActaUrl) {
        this.soporteActaUrl = soporteActaUrl;
    }

    public String getUsuarioActuanteNombre() {
        return usuarioActuanteNombre;
    }

    public void setUsuarioActuanteNombre(String usuarioActuanteNombre) {
        this.usuarioActuanteNombre = usuarioActuanteNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
