package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "alertas_seguridad_auditoria",
    indexes = {
        @Index(name = "idx_alerta_aud_tipo", columnList = "tipo_alerta"),
        @Index(name = "idx_alerta_aud_fecha", columnList = "fecha_alerta")
    }
)
public class AlertaSeguridadAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_alerta", nullable = false, length = 50)
    private TipoAlertaSeguridad tipoAlerta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private SeveridadAlertaSeguridad severidad = SeveridadAlertaSeguridad.ALTA;

    @Column(name = "descripcion_evento", nullable = false, columnDefinition = "TEXT")
    private String descripcionEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_involucrado_id")
    private Usuario usuarioInvolucrado;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "leida_por_rector", nullable = false)
    private Boolean leidaPorRector = false;

    @CreationTimestamp
    @Column(name = "fecha_alerta", nullable = false, updatable = false)
    private LocalDateTime fechaAlerta;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public AlertaSeguridadAuditoria() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoAlertaSeguridad getTipoAlerta() { return tipoAlerta; }
    public void setTipoAlerta(TipoAlertaSeguridad tipoAlerta) { this.tipoAlerta = tipoAlerta; }

    public SeveridadAlertaSeguridad getSeveridad() { return severidad; }
    public void setSeveridad(SeveridadAlertaSeguridad severidad) { this.severidad = severidad; }

    public String getDescripcionEvento() { return descripcionEvento; }
    public void setDescripcionEvento(String descripcionEvento) { this.descripcionEvento = descripcionEvento; }

    public Usuario getUsuarioInvolucrado() { return usuarioInvolucrado; }
    public void setUsuarioInvolucrado(Usuario usuarioInvolucrado) { this.usuarioInvolucrado = usuarioInvolucrado; }

    public String getIpOrigen() { return ipOrigen; }
    public void setIpOrigen(String ipOrigen) { this.ipOrigen = ipOrigen; }

    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }

    public Boolean getLeidaPorRector() { return leidaPorRector; }
    public void setLeidaPorRector(Boolean leidaPorRector) { this.leidaPorRector = leidaPorRector; }

    public LocalDateTime getFechaAlerta() { return fechaAlerta; }
    public void setFechaAlerta(LocalDateTime fechaAlerta) { this.fechaAlerta = fechaAlerta; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
