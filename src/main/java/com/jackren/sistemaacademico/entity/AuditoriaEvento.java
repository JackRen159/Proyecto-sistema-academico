package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "auditoria_eventos",
    indexes = {
        @Index(name = "idx_auditoria_modulo_fecha", columnList = "modulo, fecha_evento"),
        @Index(name = "idx_auditoria_correlation", columnList = "correlation_id"),
        @Index(name = "idx_auditoria_usuario_fecha", columnList = "usuario_id, fecha_evento"),
        @Index(name = "idx_auditoria_entidad", columnList = "entidad, entidad_id"),
        @Index(name = "idx_auditoria_hash", columnList = "hash_evento")
    }
)
public class AuditoriaEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(name = "rol_activo", nullable = false, length = 30)
    private String rolActivo;

    @Column(nullable = false, length = 50)
    private String modulo;

    @Column(nullable = false, length = 50)
    private String accion;

    @Column(nullable = false, length = 50)
    private String entidad;

    @Column(name = "entidad_id", length = 50)
    private String entidadId;

    @Column(name = "valor_anterior", columnDefinition = "TEXT")
    private String valorAnterior;

    @Column(name = "valor_nuevo", columnDefinition = "TEXT")
    private String valorNuevo;

    @Column(length = 255)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoAuditoria resultado = ResultadoAuditoria.EXITO;

    @Column(name = "correlation_id", nullable = false, length = 64)
    private String correlationId;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "fecha_evento", nullable = false, updatable = false)
    private LocalDateTime fechaEvento;

    @Column(name = "hash_anterior", nullable = false, length = 64)
    private String hashAnterior;

    @Column(name = "hash_evento", nullable = false, length = 64)
    private String hashEvento;

    public AuditoriaEvento() {
    }

    public AuditoriaEvento(Usuario usuario,
                           String username,
                           String rolActivo,
                           String modulo,
                           String accion,
                           String entidad,
                           String entidadId,
                           String valorAnterior,
                           String valorNuevo,
                           String motivo,
                           ResultadoAuditoria resultado,
                           String correlationId,
                           String direccionIp,
                           String userAgent,
                           String hashAnterior,
                           String hashEvento) {
        this.usuario = usuario;
        this.username = username;
        this.rolActivo = rolActivo;
        this.modulo = modulo;
        this.accion = accion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.valorAnterior = valorAnterior;
        this.valorNuevo = valorNuevo;
        this.motivo = motivo;
        this.resultado = resultado != null ? resultado : ResultadoAuditoria.EXITO;
        this.correlationId = correlationId;
        this.direccionIp = direccionIp;
        this.userAgent = userAgent;
        this.hashAnterior = hashAnterior;
        this.hashEvento = hashEvento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getRolActivo() {
        return rolActivo;
    }

    public void setRolActivo(String rolActivo) {
        this.rolActivo = rolActivo;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public String getAccion() {
        return accion;
    }

    public void setAccion(String accion) {
        this.accion = accion;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public String getEntidadId() {
        return entidadId;
    }

    public void setEntidadId(String entidadId) {
        this.entidadId = entidadId;
    }

    public String getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(String valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public String getValorNuevo() {
        return valorNuevo;
    }

    public void setValorNuevo(String valorNuevo) {
        this.valorNuevo = valorNuevo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ResultadoAuditoria getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAuditoria resultado) {
        this.resultado = resultado;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public LocalDateTime getFechaEvento() {
        return fechaEvento;
    }

    public void setFechaEvento(LocalDateTime fechaEvento) {
        this.fechaEvento = fechaEvento;
    }

    public String getHashAnterior() {
        return hashAnterior;
    }

    public void setHashAnterior(String hashAnterior) {
        this.hashAnterior = hashAnterior;
    }

    public String getHashEvento() {
        return hashEvento;
    }

    public void setHashEvento(String hashEvento) {
        this.hashEvento = hashEvento;
    }
}
