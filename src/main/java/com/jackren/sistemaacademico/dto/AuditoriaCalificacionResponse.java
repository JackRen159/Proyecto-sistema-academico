package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoEntidadAuditoriaCalificacion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditoriaCalificacionResponse {

    private Long id;
    private TipoEntidadAuditoriaCalificacion tipoEntidad;
    private Long entidadId;
    private Long matriculaId;
    private String numeroMatricula;
    private String estudianteNombreCompleto;
    private BigDecimal valorAnterior;
    private BigDecimal valorNuevo;
    private String motivo;
    private Long usuarioId;
    private String username;
    private LocalDateTime fechaAuditoria;

    public AuditoriaCalificacionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoEntidadAuditoriaCalificacion getTipoEntidad() {
        return tipoEntidad;
    }

    public void setTipoEntidad(TipoEntidadAuditoriaCalificacion tipoEntidad) {
        this.tipoEntidad = tipoEntidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(Long entidadId) {
        this.entidadId = entidadId;
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

    public BigDecimal getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(BigDecimal valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public BigDecimal getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(BigDecimal valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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
