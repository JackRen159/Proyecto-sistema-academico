package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "periodos_academicos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_periodos_anio_numero", columnNames = {"anio_lectivo_id", "numero"})
    },
    indexes = {
        @Index(name = "idx_periodos_anio_lectivo_id", columnList = "anio_lectivo_id")
    }
)
public class PeriodoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(nullable = false)
    private Integer numero;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPeriodo estado = EstadoPeriodo.CERRADO;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public PeriodoAcademico() {
    }

    public PeriodoAcademico(AnioLectivo anioLectivo, Integer numero, String nombre, LocalDate fechaInicio, LocalDate fechaFin, BigDecimal porcentaje) {
        this.anioLectivo = anioLectivo;
        this.numero = numero;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.porcentaje = porcentaje;
        this.estado = EstadoPeriodo.CERRADO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public EstadoPeriodo getEstado() {
        return estado;
    }

    public void setEstado(EstadoPeriodo estado) {
        this.estado = estado;
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
