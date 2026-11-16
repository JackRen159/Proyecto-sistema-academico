package com.jackren.sistemaacademico.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JustificacionAsistenciaResponse {

    private Long id;
    private Long registroAsistenciaId;
    private Long matriculaId;
    private String numeroMatricula;
    private String estudianteNombreCompleto;
    private String motivo;
    private String numeroRadicadoSoporte;
    private LocalDate fechaRadicacion;
    private Long usuarioAutorizadorId;
    private String username;
    private String observaciones;
    private LocalDateTime fechaCreacion;

    public JustificacionAsistenciaResponse() {
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroRadicadoSoporte() {
        return numeroRadicadoSoporte;
    }

    public void setNumeroRadicadoSoporte(String numeroRadicadoSoporte) {
        this.numeroRadicadoSoporte = numeroRadicadoSoporte;
    }

    public LocalDate getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDate fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public Long getUsuarioAutorizadorId() {
        return usuarioAutorizadorId;
    }

    public void setUsuarioAutorizadorId(Long usuarioAutorizadorId) {
        this.usuarioAutorizadorId = usuarioAutorizadorId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
