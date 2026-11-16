package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "configuraciones_escala_siee",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_escala_siee_anio", columnNames = {"anio_lectivo_id"})
    },
    indexes = {
        @Index(name = "idx_escala_siee_anio", columnList = "anio_lectivo_id")
    }
)
public class ConfiguracionEscalaSiee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "nota_minima", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaMinima = new BigDecimal("1.00");

    @Column(name = "nota_maxima", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaMaxima = new BigDecimal("5.00");

    @Column(name = "nota_aprobatoria", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaAprobatoria = new BigDecimal("3.00");

    @Column(name = "escala_basico_min", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaBasicoMin = new BigDecimal("3.00");

    @Column(name = "escala_alto_min", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaAltoMin = new BigDecimal("4.00");

    @Column(name = "escala_superior_min", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaSuperiorMin = new BigDecimal("4.60");

    @Column(name = "decimales_redondeo", nullable = false)
    private Integer decimalesRedondeo = 1;

    @Column(length = 255)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public ConfiguracionEscalaSiee() {
    }

    public ConfiguracionEscalaSiee(AnioLectivo anioLectivo,
                                   BigDecimal notaMinima,
                                   BigDecimal notaMaxima,
                                   BigDecimal notaAprobatoria,
                                   BigDecimal escalaBasicoMin,
                                   BigDecimal escalaAltoMin,
                                   BigDecimal escalaSuperiorMin) {
        this.anioLectivo = anioLectivo;
        this.notaMinima = notaMinima;
        this.notaMaxima = notaMaxima;
        this.notaAprobatoria = notaAprobatoria;
        this.escalaBasicoMin = escalaBasicoMin;
        this.escalaAltoMin = escalaAltoMin;
        this.escalaSuperiorMin = escalaSuperiorMin;
    }

    public DesempenoNacional homologarDesempeno(BigDecimal nota) {
        if (nota == null) {
            return DesempenoNacional.BAJO;
        }
        if (nota.compareTo(escalaSuperiorMin) >= 0) {
            return DesempenoNacional.SUPERIOR;
        }
        if (nota.compareTo(escalaAltoMin) >= 0) {
            return DesempenoNacional.ALTO;
        }
        if (nota.compareTo(escalaBasicoMin) >= 0) {
            return DesempenoNacional.BASICO;
        }
        return DesempenoNacional.BAJO;
    }

    public boolean esAprobada(BigDecimal nota) {
        if (nota == null) {
            return false;
        }
        return nota.compareTo(notaAprobatoria) >= 0;
    }

    public BigDecimal redondear(BigDecimal valor) {
        if (valor == null) {
            return null;
        }
        int scale = decimalesRedondeo != null ? decimalesRedondeo : 1;
        return valor.setScale(scale, RoundingMode.HALF_UP);
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

    public BigDecimal getNotaMinima() {
        return notaMinima;
    }

    public void setNotaMinima(BigDecimal notaMinima) {
        this.notaMinima = notaMinima;
    }

    public BigDecimal getNotaMaxima() {
        return notaMaxima;
    }

    public void setNotaMaxima(BigDecimal notaMaxima) {
        this.notaMaxima = notaMaxima;
    }

    public BigDecimal getNotaAprobatoria() {
        return notaAprobatoria;
    }

    public void setNotaAprobatoria(BigDecimal notaAprobatoria) {
        this.notaAprobatoria = notaAprobatoria;
    }

    public BigDecimal getEscalaBasicoMin() {
        return escalaBasicoMin;
    }

    public void setEscalaBasicoMin(BigDecimal escalaBasicoMin) {
        this.escalaBasicoMin = escalaBasicoMin;
    }

    public BigDecimal getEscalaAltoMin() {
        return escalaAltoMin;
    }

    public void setEscalaAltoMin(BigDecimal escalaAltoMin) {
        this.escalaAltoMin = escalaAltoMin;
    }

    public BigDecimal getEscalaSuperiorMin() {
        return escalaSuperiorMin;
    }

    public void setEscalaSuperiorMin(BigDecimal escalaSuperiorMin) {
        this.escalaSuperiorMin = escalaSuperiorMin;
    }

    public Integer getDecimalesRedondeo() {
        return decimalesRedondeo;
    }

    public void setDecimalesRedondeo(Integer decimalesRedondeo) {
        this.decimalesRedondeo = decimalesRedondeo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
