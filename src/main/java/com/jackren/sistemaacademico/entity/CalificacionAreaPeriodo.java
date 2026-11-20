package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "calificaciones_area_periodo",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_calif_area_mat_area_per", columnNames = {"matricula_id", "area_id", "periodo_academico_id"})
    },
    indexes = {
        @Index(name = "idx_calif_area_matricula", columnList = "matricula_id"),
        @Index(name = "idx_calif_area_area", columnList = "area_id"),
        @Index(name = "idx_calif_area_periodo", columnList = "periodo_academico_id")
    }
)
public class CalificacionAreaPeriodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private OrigenCalificacion origen = OrigenCalificacion.CALCULADA;

    @CreationTimestamp
    @Column(name = "fecha_calculo", nullable = false, updatable = false)
    private LocalDateTime fechaCalculo;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public CalificacionAreaPeriodo() {
    }

    public CalificacionAreaPeriodo(Matricula matricula,
                                  Area area,
                                  PeriodoAcademico periodoAcademico,
                                  BigDecimal notaDefinitiva,
                                  DesempenoNacional desempeno,
                                  Boolean aprobada) {
        this.matricula = matricula;
        this.area = area;
        this.periodoAcademico = periodoAcademico;
        this.notaDefinitiva = notaDefinitiva;
        this.desempeno = desempeno;
        this.aprobada = aprobada;
        this.origen = OrigenCalificacion.CALCULADA;
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

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
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

    public OrigenCalificacion getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenCalificacion origen) {
        this.origen = origen;
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
