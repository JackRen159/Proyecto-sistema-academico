package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsistencia;

import java.time.LocalDateTime;

public class AuditoriaAsistenciaResponse {

    private Long id;
    private Long registroAsistenciaId;
    private Long matriculaId;
    private String numeroMatricula;
    private String estudianteNombreCompleto;
    private EstadoAsistencia estadoAnterior;
    private EstadoAsistencia estadoNuevo;
    private String motivoCorreccion;
    private Long usuarioId;
    private String username;
    private LocalDateTime fechaAuditoria;

    public AuditoriaAsistenciaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRegistroAsistenciaId() {
        return registroAsistenciaId;
    }

    public void setRegistroAsistenciaId(Long registroAsistenciaId) {
        this.registroAsistenciaId = registroAsistenciaId;
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

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public EstadoAsistencia getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoAsistencia estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoAsistencia getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoAsistencia estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getMotivoCorreccion() {
        return motivoCorreccion;
    }

    public void setMotivoCorreccion(String motivoCorreccion) {
        this.motivoCorreccion = motivoCorreccion;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getFechaAuditoria() {
        return fechaAuditoria;
    }

    public void setFechaAuditoria(LocalDateTime fechaAuditoria) {
        this.fechaAuditoria = fechaAuditoria;
    }
}
