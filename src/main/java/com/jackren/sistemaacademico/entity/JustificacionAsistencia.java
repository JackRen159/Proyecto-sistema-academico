package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "justificaciones_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_justificacion_registro", columnNames = {"registro_asistencia_id"})
    },
    indexes = {
        @Index(name = "idx_justificaciones_registro", columnList = "registro_asistencia_id"),
        @Index(name = "idx_justificaciones_fecha", columnList = "fecha_radicacion")
    }
)
public class JustificacionAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "registro_asistencia_id", nullable = false)
    private RegistroAsistencia registroAsistencia;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "numero_radicado_soporte", length = 50)
    private String numeroRadicadoSoporte;

    @Column(name = "fecha_radicacion", nullable = false)
    private LocalDate fechaRadicacion = LocalDate.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_autorizador_id")
    private Usuario usuarioAutorizador;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public JustificacionAsistencia() {
    }

    public JustificacionAsistencia(RegistroAsistencia registroAsistencia,
                                   String motivo,
                                   String numeroRadicadoSoporte,
                                   LocalDate fechaRadicacion,
                                   Usuario usuarioAutorizador,
                                   String username,
                                   String observaciones) {
        this.registroAsistencia = registroAsistencia;
        this.motivo = motivo;
        this.numeroRadicadoSoporte = numeroRadicadoSoporte;
        this.fechaRadicacion = fechaRadicacion != null ? fechaRadicacion : LocalDate.now();
        this.usuarioAutorizador = usuarioAutorizador;
        this.username = username;
        this.observaciones = observaciones;
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

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroRadicadoSoporte() {
        return numeroRadicadoSoporte;
    }

    public void setNumeroRadicadoSoporte(String numeroRadicadoSoporte) {
        this.numeroRadicadoSoporte = numeroRadicadoSoporte;
    }

    public LocalDate getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDate fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public Usuario getUsuarioAutorizador() {
        return usuarioAutorizador;
    }

    public void setUsuarioAutorizador(Usuario usuarioAutorizador) {
        this.usuarioAutorizador = usuarioAutorizador;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
