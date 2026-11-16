package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "calificaciones_periodo",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_calif_periodo_mat_asig_per", columnNames = {"matricula_id", "asignatura_id", "periodo_academico_id"})
    },
    indexes = {
        @Index(name = "idx_calif_periodo_matricula", columnList = "matricula_id"),
        @Index(name = "idx_calif_periodo_asignatura", columnList = "asignatura_id"),
        @Index(name = "idx_calif_periodo_periodo", columnList = "periodo_academico_id")
    }
)
public class CalificacionPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(name = "nota_definitiva", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaDefinitiva;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DesempenoNacional desempeno;

    @Column(nullable = false)
    private Boolean aprobada;

    @Column(name = "nota_recuperacion", precision = 4, scale = 2)
    private BigDecimal notaRecuperacion;

    @Column(name = "nota_final", precision = 4, scale = 2)
    private BigDecimal notaFinal;

    @Column(length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_calculo", nullable = false, updatable = false)
    private LocalDateTime fechaCalculo;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public CalificacionPeriodo() {
    }

    public CalificacionPeriodo(Matricula matricula,
                               Asignatura asignatura,
                               PeriodoAcademico periodoAcademico,
                               BigDecimal notaDefinitiva,
                               DesempenoNacional desempeno,
                               Boolean aprobada) {
        this.matricula = matricula;
        this.asignatura = asignatura;
        this.periodoAcademico = periodoAcademico;
        this.notaDefinitiva = notaDefinitiva;
        this.desempeno = desempeno;
        this.aprobada = aprobada;
        this.notaFinal = notaDefinitiva;
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

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public BigDecimal getNotaDefinitiva() {
        return notaDefinitiva;
    }

    public void setNotaDefinitiva(BigDecimal notaDefinitiva) {
        this.notaDefinitiva = notaDefinitiva;
    }

    public DesempenoNacional getDesempeno() {
        return desempeno;
    }

    public void setDesempeno(DesempenoNacional desempeno) {
        this.desempeno = desempeno;
    }

    public Boolean getAprobada() {
        return aprobada;
    }

    public void setAprobada(Boolean aprobada) {
        this.aprobada = aprobada;
    }

    public BigDecimal getNotaRecuperacion() {
        return notaRecuperacion;
    }

    public void setNotaRecuperacion(BigDecimal notaRecuperacion) {
        this.notaRecuperacion = notaRecuperacion;
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(BigDecimal notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDateTime fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
