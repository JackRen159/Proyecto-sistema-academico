package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "auditoria_asistencia",
    indexes = {
        @Index(name = "idx_auditoria_asistencia_reg", columnList = "registro_asistencia_id"),
        @Index(name = "idx_auditoria_asistencia_mat", columnList = "matricula_id"),
        @Index(name = "idx_auditoria_asistencia_fecha", columnList = "fecha_auditoria")
    }
)
public class AuditoriaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registro_asistencia_id", nullable = false)
    private RegistroAsistencia registroAsistencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", length = 35)
    private EstadoAsistencia estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, length = 35)
    private EstadoAsistencia estadoNuevo;

    @Column(name = "motivo_correccion", nullable = false, length = 255)
    private String motivoCorreccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 50)
    private String username;

    @CreationTimestamp
    @Column(name = "fecha_auditoria", nullable = false, updatable = false)
    private LocalDateTime fechaAuditoria;

    public AuditoriaAsistencia() {
    }

    public AuditoriaAsistencia(RegistroAsistencia registroAsistencia,
                               Matricula matricula,
                               EstadoAsistencia estadoAnterior,
                               EstadoAsistencia estadoNuevo,
                               String motivoCorreccion,
                               Usuario usuario,
                               String username) {
        this.registroAsistencia = registroAsistencia;
        this.matricula = matricula;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.motivoCorreccion = motivoCorreccion;
        this.usuario = usuario;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroAsistencia getRegistroAsistencia() {
        return registroAsistencia;
    }

    public void setRegistroAsistencia(RegistroAsistencia registroAsistencia) {
        this.registroAsistencia = registroAsistencia;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public EstadoAsistencia getEstadoAnterior() {
        return estadoAnterior;
    }

    public void setEstadoAnterior(EstadoAsistencia estadoAnterior) {
        this.estadoAnterior = estadoAnterior;
    }

    public EstadoAsistencia getEstadoNuevo() {
        return estadoNuevo;
    }

    public void setEstadoNuevo(EstadoAsistencia estadoNuevo) {
        this.estadoNuevo = estadoNuevo;
    }

    public String getMotivoCorreccion() {
        return motivoCorreccion;
    }

    public void setMotivoCorreccion(String motivoCorreccion) {
        this.motivoCorreccion = motivoCorreccion;
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
