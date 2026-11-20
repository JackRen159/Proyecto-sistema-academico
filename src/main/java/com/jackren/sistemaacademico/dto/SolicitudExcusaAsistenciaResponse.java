package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoSolicitudExcusa;
import com.jackren.sistemaacademico.entity.RolSolicitanteExcusa;
import com.jackren.sistemaacademico.entity.TipoJustificacionAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SolicitudExcusaAsistenciaResponse {

    private Long id;
    private String numeroRadicado;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long solicitanteUsuarioId;
    private RolSolicitanteExcusa rolSolicitante;
    private TipoJustificacionAsistencia tipoJustificacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String motivoExplicacion;
    private String soporteUrl;
    private Boolean esDatoSaludSensible;
    private EstadoSolicitudExcusa estado;
    private LocalDateTime fechaRadicacion;
    private LocalDate fechaLimiteSubsanacion;
    private String observacionesResolucion;
    private String usuarioResolutorNombre;
    private LocalDateTime fechaResolucion;

    public SolicitudExcusaAsistenciaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroRadicado() {
        return numeroRadicado;
    }

    public void setNumeroRadicado(String numeroRadicado) {
        this.numeroRadicado = numeroRadicado;
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

    public Long getSolicitanteUsuarioId() {
        return solicitanteUsuarioId;
    }

    public void setSolicitanteUsuarioId(Long solicitanteUsuarioId) {
        this.solicitanteUsuarioId = solicitanteUsuarioId;
    }

    public RolSolicitanteExcusa getRolSolicitante() {
        return rolSolicitante;
    }

    public void setRolSolicitante(RolSolicitanteExcusa rolSolicitante) {
        this.rolSolicitante = rolSolicitante;
    }

    public TipoJustificacionAsistencia getTipoJustificacion() {
        return tipoJustificacion;
    }

    public void setTipoJustificacion(TipoJustificacionAsistencia tipoJustificacion) {
        this.tipoJustificacion = tipoJustificacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivoExplicacion() {
        return motivoExplicacion;
    }

    public void setMotivoExplicacion(String motivoExplicacion) {
        this.motivoExplicacion = motivoExplicacion;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }

    public Boolean getEsDatoSaludSensible() {
        return esDatoSaludSensible;
    }

    public void setEsDatoSaludSensible(Boolean esDatoSaludSensible) {
        this.esDatoSaludSensible = esDatoSaludSensible;
    }

    public EstadoSolicitudExcusa getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudExcusa estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDateTime fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public LocalDate getFechaLimiteSubsanacion() {
        return fechaLimiteSubsanacion;
    }

    public void setFechaLimiteSubsanacion(LocalDate fechaLimiteSubsanacion) {
        this.fechaLimiteSubsanacion = fechaLimiteSubsanacion;
    }

    public String getObservacionesResolucion() {
        return observacionesResolucion;
    }

    public void setObservacionesResolucion(String observacionesResolucion) {
        this.observacionesResolucion = observacionesResolucion;
    }

    public String getUsuarioResolutorNombre() {
        return usuarioResolutorNombre;
    }

    public void setUsuarioResolutorNombre(String usuarioResolutorNombre) {
        this.usuarioResolutorNombre = usuarioResolutorNombre;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }
}
