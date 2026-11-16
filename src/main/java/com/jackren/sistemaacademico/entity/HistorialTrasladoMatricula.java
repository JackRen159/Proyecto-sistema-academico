package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_traslados_matricula")
public class HistorialTrasladoMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_origen_id", nullable = false)
    private Grupo grupoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_destino_id", nullable = false)
    private Grupo grupoDestino;

    @Column(nullable = false, length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_traslado", nullable = false, updatable = false)
    private LocalDateTime fechaTraslado;

    public HistorialTrasladoMatricula() {
    }

    public HistorialTrasladoMatricula(Matricula matricula, Grupo grupoOrigen, Grupo grupoDestino, String motivo, Usuario usuario) {
        this.matricula = matricula;
        this.grupoOrigen = grupoOrigen;
        this.grupoDestino = grupoDestino;
        this.motivo = motivo;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Grupo getGrupoOrigen() {
        return grupoOrigen;
    }

    public void setGrupoOrigen(Grupo grupoOrigen) {
        this.grupoOrigen = grupoOrigen;
    }

    public Grupo getGrupoDestino() {
        return grupoDestino;
    }

    public void setGrupoDestino(Grupo grupoDestino) {
        this.grupoDestino = grupoDestino;
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

    public LocalDateTime getFechaTraslado() {
        return fechaTraslado;
    }

    public void setFechaTraslado(LocalDateTime fechaTraslado) {
        this.fechaTraslado = fechaTraslado;
    }
}
