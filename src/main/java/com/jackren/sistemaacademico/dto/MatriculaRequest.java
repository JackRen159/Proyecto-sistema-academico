package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoMatricula;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class MatriculaRequest {

    @NotNull(message = "El estudiante es obligatorio")
    private Long estudianteId;

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    private TipoMatricula tipoMatricula = TipoMatricula.REGULAR;

    @Size(max = 30, message = "El folio no puede superar 30 caracteres")
    private String folio;

    private LocalDate fechaMatricula;

    private LocalDate fechaEfectivaInicio;

    @Size(max = 150, message = "La institución de procedencia no puede superar 150 caracteres")
    private String institucionProcedencia;

    private Long acudientePrincipalId;

    private Long sieeVersionId;

    private Long manualConvivenciaVersionId;

    private java.time.LocalDateTime fechaAceptacionReglamentos;

    private Boolean esRepitente = false;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public MatriculaRequest() {
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public TipoMatricula getTipoMatricula() {
        return tipoMatricula;
    }

    public void setTipoMatricula(TipoMatricula tipoMatricula) {
        this.tipoMatricula = tipoMatricula;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public LocalDate getFechaEfectivaInicio() {
        return fechaEfectivaInicio;
    }

    public void setFechaEfectivaInicio(LocalDate fechaEfectivaInicio) {
        this.fechaEfectivaInicio = fechaEfectivaInicio;
    }

    public String getInstitucionProcedencia() {
        return institucionProcedencia;
    }

    public void setInstitucionProcedencia(String institucionProcedencia) {
        this.institucionProcedencia = institucionProcedencia;
    }

    public Long getAcudientePrincipalId() {
        return acudientePrincipalId;
    }

    public void setAcudientePrincipalId(Long acudientePrincipalId) {
        this.acudientePrincipalId = acudientePrincipalId;
    }

    public Long getSieeVersionId() {
        return sieeVersionId;
    }

    public void setSieeVersionId(Long sieeVersionId) {
        this.sieeVersionId = sieeVersionId;
    }

    public Long getManualConvivenciaVersionId() {
        return manualConvivenciaVersionId;
    }

    public void setManualConvivenciaVersionId(Long manualConvivenciaVersionId) {
        this.manualConvivenciaVersionId = manualConvivenciaVersionId;
    }

    public java.time.LocalDateTime getFechaAceptacionReglamentos() {
        return fechaAceptacionReglamentos;
    }

    public void setFechaAceptacionReglamentos(java.time.LocalDateTime fechaAceptacionReglamentos) {
        this.fechaAceptacionReglamentos = fechaAceptacionReglamentos;
    }

    public Boolean getEsRepitente() {
        return esRepitente;
    }

    public void setEsRepitente(Boolean esRepitente) {
        this.esRepitente = esRepitente;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
