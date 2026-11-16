package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "calificaciones_actividades",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_calificaciones_actividad_matricula", columnNames = {"actividad_id", "matricula_id"})
    },
    indexes = {
        @Index(name = "idx_calificaciones_actividad", columnList = "actividad_id"),
        @Index(name = "idx_calificaciones_matricula", columnList = "matricula_id")
    }
)
public class CalificacionActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actividad_id", nullable = false)
    private ActividadEvaluativa actividad;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false, precision = 4, scale = 2)
    private BigDecimal valor;

    @Column(length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public CalificacionActividad() {
    }

    public CalificacionActividad(ActividadEvaluativa actividad, Matricula matricula, BigDecimal valor, String observaciones) {
        this.actividad = actividad;
        this.matricula = matricula;
        this.valor = valor;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActividadEvaluativa getActividad() {
        return actividad;
    }

    public void setActividad(ActividadEvaluativa actividad) {
        this.actividad = actividad;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
