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

    @Column(name = "fecha_inicio_digitacion")
    private LocalDate fechaInicioDigitacion;

    @Column(name = "fecha_fin_digitacion")
    private LocalDate fechaFinDigitacion;

    @Column(name = "numero_acta_aprobacion", length = 50)
    private String numeroActaAprobacion;

    @Column(name = "fecha_acta_aprobacion")
    private LocalDate fechaActaAprobacion;

    @Column(name = "organo_aprobador", length = 40)
    private String organoAprobador;

    @Column(name = "motivo_ajuste_excepcional", length = 500)
    private String motivoAjusteExcepcional;

    @Column(name = "resolucion_rectoral_excepcional", length = 100)
    private String resolucionRectoralExcepcional;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

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

    public LocalDate getFechaInicioDigitacion() {
        return fechaInicioDigitacion;
    }

    public void setFechaInicioDigitacion(LocalDate fechaInicioDigitacion) {
        this.fechaInicioDigitacion = fechaInicioDigitacion;
    }

    public LocalDate getFechaFinDigitacion() {
        return fechaFinDigitacion;
    }

    public void setFechaFinDigitacion(LocalDate fechaFinDigitacion) {
        this.fechaFinDigitacion = fechaFinDigitacion;
    }

    public String getNumeroActaAprobacion() {
        return numeroActaAprobacion;
    }

    public void setNumeroActaAprobacion(String numeroActaAprobacion) {
        this.numeroActaAprobacion = numeroActaAprobacion;
    }

    public LocalDate getFechaActaAprobacion() {
        return fechaActaAprobacion;
    }

    public void setFechaActaAprobacion(LocalDate fechaActaAprobacion) {
        this.fechaActaAprobacion = fechaActaAprobacion;
    }

    public String getOrganoAprobador() {
        return organoAprobador;
    }

    public void setOrganoAprobador(String organoAprobador) {
        this.organoAprobador = organoAprobador;
    }

    public String getMotivoAjusteExcepcional() {
        return motivoAjusteExcepcional;
    }

    public void setMotivoAjusteExcepcional(String motivoAjusteExcepcional) {
        this.motivoAjusteExcepcional = motivoAjusteExcepcional;
    }

    public String getResolucionRectoralExcepcional() {
        return resolucionRectoralExcepcional;
    }

    public void setResolucionRectoralExcepcional(String resolucionRectoralExcepcional) {
        this.resolucionRectoralExcepcional = resolucionRectoralExcepcional;
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
