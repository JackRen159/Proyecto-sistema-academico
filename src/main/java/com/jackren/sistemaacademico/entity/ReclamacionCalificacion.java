package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reclamaciones_calificacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reclamacion_radicado", columnNames = {"numero_radicado"})
    },
    indexes = {
        @Index(name = "idx_reclamacion_matricula", columnList = "matricula_id"),
        @Index(name = "idx_reclamacion_ref", columnList = "tipo_calificacion, referencia_id"),
        @Index(name = "idx_reclamacion_estado", columnList = "estado"),
        @Index(name = "idx_reclamacion_fecha_limite", columnList = "fecha_limite_respuesta")
    }
)
public class ReclamacionCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", nullable = false, length = 50)
    private String numeroRadicado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_calificacion", nullable = false, length = 30)
    private TipoCalificacionReclamacion tipoCalificacion;

    @Column(name = "referencia_id", nullable = false)
    private Long referenciaId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_usuario_id", nullable = false)
    private Usuario solicitanteUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_solicitante", nullable = false, length = 20)
    private RolSolicitanteReclamacion rolSolicitante;

    @Column(name = "motivo_reclamacion", nullable = false, columnDefinition = "TEXT")
    private String motivoReclamacion;

    @Column(name = "soporte_url", length = 255)
    private String soporteUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private EstadoReclamacionCalificacion estado = EstadoReclamacionCalificacion.RADICADA_PRIMERA_INSTANCIA;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Column(name = "fecha_limite_respuesta", nullable = false)
    private LocalDate fechaLimiteRespuesta;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_primera_instancia", length = 30)
    private DecisionReclamacion decisionPrimeraInstancia;

    @Column(name = "motivo_primera_instancia", columnDefinition = "TEXT")
    private String motivoPrimeraInstancia;

    @Column(name = "fecha_decision_primera_instancia")
    private LocalDateTime fechaDecisionPrimeraInstancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_resolutor_id")
    private Usuario docenteResolutor;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_segunda_instancia", length = 30)
    private DecisionReclamacion decisionSegundaInstancia;

    @Column(name = "motivo_segunda_instancia", columnDefinition = "TEXT")
    private String motivoSegundaInstancia;

    @Column(name = "fecha_decision_segunda_instancia")
    private LocalDateTime fechaDecisionSegundaInstancia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinador_resolutor_id")
    private Usuario coordinadorResolutor;

    @Column(name = "nueva_nota_aprobada", precision = 4, scale = 2)
    private BigDecimal nuevaNotaAprobada;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public ReclamacionCalificacion() {
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

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Usuario getSolicitanteUsuario() {
        return solicitanteUsuario;
    }

    public void setSolicitanteUsuario(Usuario solicitanteUsuario) {
        this.solicitanteUsuario = solicitanteUsuario;
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

    public Usuario getDocenteResolutor() {
        return docenteResolutor;
    }

    public void setDocenteResolutor(Usuario docenteResolutor) {
        this.docenteResolutor = docenteResolutor;
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

    public Usuario getCoordinadorResolutor() {
        return coordinadorResolutor;
    }

    public void setCoordinadorResolutor(Usuario coordinadorResolutor) {
        this.coordinadorResolutor = coordinadorResolutor;
    }

    public BigDecimal getNuevaNotaAprobada() {
        return nuevaNotaAprobada;
    }

    public void setNuevaNotaAprobada(BigDecimal nuevaNotaAprobada) {
        this.nuevaNotaAprobada = nuevaNotaAprobada;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
