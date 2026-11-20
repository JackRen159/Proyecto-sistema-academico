package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "registros_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_registro_asistencia_sesion_matricula", columnNames = {"sesion_asistencia_id", "matricula_id"})
    },
    indexes = {
        @Index(name = "idx_registros_sesion", columnList = "sesion_asistencia_id"),
        @Index(name = "idx_registros_matricula", columnList = "matricula_id"),
        @Index(name = "idx_registros_estado", columnList = "estado")
    }
)
public class RegistroAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_asistencia_id", nullable = false)
    private SesionAsistencia sesionAsistencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private EstadoAsistencia estado = EstadoAsistencia.PRESENTE;

    @Column(name = "minutos_retardo", nullable = false)
    private Integer minutosRetardo = 0;

    @Column(length = 255)
    private String observaciones;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public RegistroAsistencia() {
    }

    public RegistroAsistencia(SesionAsistencia sesionAsistencia,
                              Matricula matricula,
                              EstadoAsistencia estado,
                              Integer minutosRetardo,
                              String observaciones) {
        this.sesionAsistencia = sesionAsistencia;
        this.matricula = matricula;
        this.estado = estado != null ? estado : EstadoAsistencia.PRESENTE;
        this.minutosRetardo = minutosRetardo != null ? minutosRetardo : 0;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SesionAsistencia getSesionAsistencia() {
        return sesionAsistencia;
    }

    public void setSesionAsistencia(SesionAsistencia sesionAsistencia) {
        this.sesionAsistencia = sesionAsistencia;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public EstadoAsistencia getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistencia estado) {
        this.estado = estado;
    }

    public Integer getMinutosRetardo() {
        return minutosRetardo;
    }

    public void setMinutosRetardo(Integer minutosRetardo) {
        this.minutosRetardo = minutosRetardo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
