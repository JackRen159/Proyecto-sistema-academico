package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsistencia;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class RegistroAsistenciaResponse {

    private Long id;
    private Long sesionAsistenciaId;
    private LocalDate fechaSesion;
    private Integer bloqueHorario;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private EstadoAsistencia estado;
    private Integer minutosRetardo;
    private String observaciones;
    private Boolean justificado;
    private String motivoJustificacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public RegistroAsistenciaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSesionAsistenciaId() {
        return sesionAsistenciaId;
    }

    public void setSesionAsistenciaId(Long sesionAsistenciaId) {
        this.sesionAsistenciaId = sesionAsistenciaId;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public Integer getBloqueHorario() {
        return bloqueHorario;
    }

    public void setBloqueHorario(Integer bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
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

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public Integer getMinutosRetardo() {
        return minutosRetardo;
    }

    public void setMinutosRetardo(Integer minutosRetardo) {
        this.minutosRetardo = minutosRetardo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getJustificado() {
        return justificado;
    }

    public void setJustificado(Boolean justificado) {
        this.justificado = justificado;
    }

    public String getMotivoJustificacion() {
        return motivoJustificacion;
    }

    public void setMotivoJustificacion(String motivoJustificacion) {
        this.motivoJustificacion = motivoJustificacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
