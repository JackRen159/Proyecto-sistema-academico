package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DecisionReclamacion;
import com.jackren.sistemaacademico.entity.EstadoReclamacionCalificacion;
import com.jackren.sistemaacademico.entity.RolSolicitanteReclamacion;
import com.jackren.sistemaacademico.entity.TipoCalificacionReclamacion;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReclamacionCalificacionResponse {

    private Long id;
    private String numeroRadicado;
    private TipoCalificacionReclamacion tipoCalificacion;
    private Long referenciaId;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long solicitanteUsuarioId;
    private RolSolicitanteReclamacion rolSolicitante;
    private String motivoReclamacion;
    private String soporteUrl;
    private EstadoReclamacionCalificacion estado;
    private LocalDateTime fechaRadicacion;
    private LocalDate fechaLimiteRespuesta;
    private DecisionReclamacion decisionPrimeraInstancia;
    private String motivoPrimeraInstancia;
    private LocalDateTime fechaDecisionPrimeraInstancia;
    private String docenteResolutorNombre;
    private DecisionReclamacion decisionSegundaInstancia;
    private String motivoSegundaInstancia;
    private LocalDateTime fechaDecisionSegundaInstancia;
    private String coordinadorResolutorNombre;
    private BigDecimal nuevaNotaAprobada;

    public ReclamacionCalificacionResponse() {
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

    public TipoCalificacionReclamacion getTipoCalificacion() {
        return tipoCalificacion;
    }

    public void setTipoCalificacion(TipoCalificacionReclamacion tipoCalificacion) {
        this.tipoCalificacion = tipoCalificacion;
    }

    public Long getReferenciaId() {
        return referenciaId;
    }

    public void setReferenciaId(Long referenciaId) {
        this.referenciaId = referenciaId;
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

    public RolSolicitanteReclamacion getRolSolicitante() {
        return rolSolicitante;
    }

    public void setRolSolicitante(RolSolicitanteReclamacion rolSolicitante) {
        this.rolSolicitante = rolSolicitante;
    }

    public String getMotivoReclamacion() {
        return motivoReclamacion;
    }

    public void setMotivoReclamacion(String motivoReclamacion) {
        this.motivoReclamacion = motivoReclamacion;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }

    public EstadoReclamacionCalificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoReclamacionCalificacion estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDateTime fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public LocalDate getFechaLimiteRespuesta() {
        return fechaLimiteRespuesta;
    }

    public void setFechaLimiteRespuesta(LocalDate fechaLimiteRespuesta) {
        this.fechaLimiteRespuesta = fechaLimiteRespuesta;
    }

    public DecisionReclamacion getDecisionPrimeraInstancia() {
        return decisionPrimeraInstancia;
    }

    public void setDecisionPrimeraInstancia(DecisionReclamacion decisionPrimeraInstancia) {
        this.decisionPrimeraInstancia = decisionPrimeraInstancia;
    }

    public String getMotivoPrimeraInstancia() {
        return motivoPrimeraInstancia;
    }

    public void setMotivoPrimeraInstancia(String motivoPrimeraInstancia) {
        this.motivoPrimeraInstancia = motivoPrimeraInstancia;
    }

    public LocalDateTime getFechaDecisionPrimeraInstancia() {
        return fechaDecisionPrimeraInstancia;
    }

    public void setFechaDecisionPrimeraInstancia(LocalDateTime fechaDecisionPrimeraInstancia) {
        this.fechaDecisionPrimeraInstancia = fechaDecisionPrimeraInstancia;
    }

    public String getDocenteResolutorNombre() {
        return docenteResolutorNombre;
    }

    public void setDocenteResolutorNombre(String docenteResolutorNombre) {
        this.docenteResolutorNombre = docenteResolutorNombre;
    }

    public DecisionReclamacion getDecisionSegundaInstancia() {
        return decisionSegundaInstancia;
    }

    public void setDecisionSegundaInstancia(DecisionReclamacion decisionSegundaInstancia) {
        this.decisionSegundaInstancia = decisionSegundaInstancia;
    }

    public String getMotivoSegundaInstancia() {
        return motivoSegundaInstancia;
    }

    public void setMotivoSegundaInstancia(String motivoSegundaInstancia) {
        this.motivoSegundaInstancia = motivoSegundaInstancia;
    }

    public LocalDateTime getFechaDecisionSegundaInstancia() {
        return fechaDecisionSegundaInstancia;
    }

    public void setFechaDecisionSegundaInstancia(LocalDateTime fechaDecisionSegundaInstancia) {
        this.fechaDecisionSegundaInstancia = fechaDecisionSegundaInstancia;
    }

    public String getCoordinadorResolutorNombre() {
        return coordinadorResolutorNombre;
    }

    public void setCoordinadorResolutorNombre(String coordinadorResolutorNombre) {
        this.coordinadorResolutorNombre = coordinadorResolutorNombre;
    }

    public BigDecimal getNuevaNotaAprobada() {
        return nuevaNotaAprobada;
    }

    public void setNuevaNotaAprobada(BigDecimal nuevaNotaAprobada) {
        this.nuevaNotaAprobada = nuevaNotaAprobada;
    }
}
