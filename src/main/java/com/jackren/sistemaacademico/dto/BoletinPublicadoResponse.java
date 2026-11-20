package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoBoletinVersion;

import java.time.LocalDateTime;

public class BoletinPublicadoResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoId;
    private String periodoNombre;
    private Integer numeroVersion;
    private EstadoBoletinVersion estadoBoletin;
    private String datosJson;
    private String usuarioPublicadorNombre;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaReapertura;
    private String motivoReapertura;

    public BoletinPublicadoResponse() {
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

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoBoletinVersion getEstadoBoletin() {
        return estadoBoletin;
    }

    public void setEstadoBoletin(EstadoBoletinVersion estadoBoletin) {
        this.estadoBoletin = estadoBoletin;
    }

    public String getDatosJson() {
        return datosJson;
    }

    public void setDatosJson(String datosJson) {
        this.datosJson = datosJson;
    }

    public String getUsuarioPublicadorNombre() {
        return usuarioPublicadorNombre;
    }

    public void setUsuarioPublicadorNombre(String usuarioPublicadorNombre) {
        this.usuarioPublicadorNombre = usuarioPublicadorNombre;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaReapertura() {
        return fechaReapertura;
    }

    public void setFechaReapertura(LocalDateTime fechaReapertura) {
        this.fechaReapertura = fechaReapertura;
    }

    public String getMotivoReapertura() {
        return motivoReapertura;
    }

    public void setMotivoReapertura(String motivoReapertura) {
        this.motivoReapertura = motivoReapertura;
    }
}
