package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class ConstanciaMatriculaResponse {

    private String codigoVerificacion;
    private String numeroConsecutivo;
    private LocalDateTime fechaEmision;
    private String nombreInstitucion;
    private String nitInstitucion;
    private String daneInstitucion;
    private String resolucionAprobacion;
    private Long estudianteId;
    private String nombreEstudiante;
    private String tipoDocumentoEstudiante;
    private String numeroDocumentoEstudiante;
    private String codigoMatricula;
    private Integer anioLectivo;
    private String grado;
    private String grupo;
    private String jornada;
    private String estadoMatricula;
    private String destinatario;
    private String motivo;
    private String textoCertificacion;

    public ConstanciaMatriculaResponse() {
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public String getNumeroConsecutivo() {
        return numeroConsecutivo;
    }

    public void setNumeroConsecutivo(String numeroConsecutivo) {
        this.numeroConsecutivo = numeroConsecutivo;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getNombreInstitucion() {
        return nombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        this.nombreInstitucion = nombreInstitucion;
    }

    public String getNitInstitucion() {
        return nitInstitucion;
    }

    public void setNitInstitucion(String nitInstitucion) {
        this.nitInstitucion = nitInstitucion;
    }

    public String getDaneInstitucion() {
        return daneInstitucion;
    }

    public void setDaneInstitucion(String daneInstitucion) {
        this.daneInstitucion = daneInstitucion;
    }

    public String getResolucionAprobacion() {
        return resolucionAprobacion;
    }

    public void setResolucionAprobacion(String resolucionAprobacion) {
        this.resolucionAprobacion = resolucionAprobacion;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getTipoDocumentoEstudiante() {
        return tipoDocumentoEstudiante;
    }

    public void setTipoDocumentoEstudiante(String tipoDocumentoEstudiante) {
        this.tipoDocumentoEstudiante = tipoDocumentoEstudiante;
    }

    public String getNumeroDocumentoEstudiante() {
        return numeroDocumentoEstudiante;
    }

    public void setNumeroDocumentoEstudiante(String numeroDocumentoEstudiante) {
        this.numeroDocumentoEstudiante = numeroDocumentoEstudiante;
    }

    public String getCodigoMatricula() {
        return codigoMatricula;
    }

    public void setCodigoMatricula(String codigoMatricula) {
        this.codigoMatricula = codigoMatricula;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getJornada() {
        return jornada;
    }

    public void setJornada(String jornada) {
        this.jornada = jornada;
    }

    public String getEstadoMatricula() {
        return estadoMatricula;
    }

    public void setEstadoMatricula(String estadoMatricula) {
        this.estadoMatricula = estadoMatricula;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getTextoCertificacion() {
        return textoCertificacion;
    }

    public void setTextoCertificacion(String textoCertificacion) {
        this.textoCertificacion = textoCertificacion;
    }
}
