package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;

import java.time.LocalDateTime;

public class RegistroEmisionResponse {

    private Long id;
    private TipoDocumentoAcademico tipoDocumento;
    private String numeroConsecutivoCompleto;
    private String codigoVerificacion;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Long estudianteId;
    private String nombreEstudiante;
    private String documentoEstudiante;
    private Long matriculaId;
    private Long periodoAcademicoId;
    private String nombrePeriodo;
    private Long grupoId;
    private String nombreGrupo;
    private String destinatario;
    private String motivo;
    private Boolean anulado;
    private String motivoAnulacion;
    private LocalDateTime fechaAnulacion;
    private LocalDateTime fechaEmision;

    public RegistroEmisionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDocumentoAcademico getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoAcademico tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroConsecutivoCompleto() {
        return numeroConsecutivoCompleto;
    }

    public void setNumeroConsecutivoCompleto(String numeroConsecutivoCompleto) {
        this.numeroConsecutivoCompleto = numeroConsecutivoCompleto;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
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

    public String getDocumentoEstudiante() {
        return documentoEstudiante;
    }

    public void setDocumentoEstudiante(String documentoEstudiante) {
        this.documentoEstudiante = documentoEstudiante;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getNombrePeriodo() {
        return nombrePeriodo;
    }

    public void setNombrePeriodo(String nombrePeriodo) {
        this.nombrePeriodo = nombrePeriodo;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
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

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
}
