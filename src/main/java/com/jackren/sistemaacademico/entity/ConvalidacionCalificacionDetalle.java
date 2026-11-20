package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
    name = "convalidaciones_calificacion_detalle",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_conv_det_conv_asig", columnNames = {"convalidacion_id", "asignatura_id"})
    },
    indexes = {
        @Index(name = "idx_conv_det_convalidacion", columnList = "convalidacion_id")
    }
)
public class ConvalidacionCalificacionDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convalidacion_id", nullable = false)
    private ConvalidacionCalificacion convalidacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(name = "nota_convalidada", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaConvalidada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DesempenoNacional desempeno;

    @Column(length = 255)
    private String observaciones;

    public ConvalidacionCalificacionDetalle() {
    }

    public ConvalidacionCalificacionDetalle(ConvalidacionCalificacion convalidacion,
                                           Asignatura asignatura,
                                           BigDecimal notaConvalidada,
                                           DesempenoNacional desempeno,
                                           String observaciones) {
        this.convalidacion = convalidacion;
        this.asignatura = asignatura;
        this.notaConvalidada = notaConvalidada;
        this.desempeno = desempeno;
        this.observaciones = observaciones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvalidacionCalificacion getConvalidacion() {
        return convalidacion;
    }

    public void setConvalidacion(ConvalidacionCalificacion convalidacion) {
        this.convalidacion = convalidacion;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public BigDecimal getNotaConvalidada() {
        return notaConvalidada;
    }

    public void setNotaConvalidada(BigDecimal notaConvalidada) {
        this.notaConvalidada = notaConvalidada;
    }

    public DesempenoNacional getDesempeno() {
        return desempeno;
    }

    public void setDesempeno(DesempenoNacional desempeno) {
        this.desempeno = desempeno;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
