package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "auditoria_calificaciones",
    indexes = {
        @Index(name = "idx_auditoria_calif_entidad", columnList = "tipo_entidad, entidad_id"),
        @Index(name = "idx_auditoria_calif_matricula", columnList = "matricula_id"),
        @Index(name = "idx_auditoria_calif_fecha", columnList = "fecha_auditoria")
    }
)
public class AuditoriaCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_entidad", nullable = false, length = 30)
    private TipoEntidadAuditoriaCalificacion tipoEntidad;

    @Column(name = "entidad_id", nullable = false)
    private Long entidadId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(name = "valor_anterior", precision = 4, scale = 2)
    private BigDecimal valorAnterior;

    @Column(name = "valor_nuevo", nullable = false, precision = 4, scale = 2)
    private BigDecimal valorNuevo;

    @Column(nullable = false, length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String username;

    @CreationTimestamp
    @Column(name = "fecha_auditoria", nullable = false, updatable = false)
    private LocalDateTime fechaAuditoria;

    public AuditoriaCalificacion() {
    }

    public AuditoriaCalificacion(TipoEntidadAuditoriaCalificacion tipoEntidad,
                                 Long entidadId,
                                 Matricula matricula,
                                 BigDecimal valorAnterior,
                                 BigDecimal valorNuevo,
                                 String motivo,
                                 Usuario usuario,
                                 String username) {
        this.tipoEntidad = tipoEntidad;
        this.entidadId = entidadId;
        this.matricula = matricula;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.motivo = motivo;
        this.usuario = usuario;
        this.username = username;
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

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
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
