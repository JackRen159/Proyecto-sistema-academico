package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "configuraciones_alerta_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_alerta_asistencia_anio", columnNames = {"anio_lectivo_id"})
    },
    indexes = {
        @Index(name = "idx_alerta_asistencia_anio", columnList = "anio_lectivo_id")
    }
)
public class ConfiguracionAlertaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "porcentaje_alerta_amarilla", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAlertaAmarilla = new BigDecimal("15.00");

    @Column(name = "porcentaje_alerta_roja", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeAlertaRoja = new BigDecimal("25.00");

    @Column(length = 255)
    private String descripcion;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public ConfiguracionAlertaAsistencia() {
    }

    public ConfiguracionAlertaAsistencia(AnioLectivo anioLectivo,
                                         BigDecimal porcentajeAlertaAmarilla,
                                         BigDecimal porcentajeAlertaRoja,
                                         String descripcion) {
        this.anioLectivo = anioLectivo;
        this.porcentajeAlertaAmarilla = porcentajeAlertaAmarilla;
        this.porcentajeAlertaRoja = porcentajeAlertaRoja;
        this.descripcion = descripcion;
    }

    public boolean esAlertaRoja(BigDecimal porcentajeInasistencia) {
        if (porcentajeInasistencia == null) {
            return false;
        }
        return porcentajeInasistencia.compareTo(porcentajeAlertaRoja) >= 0;
    }

    public boolean esAlertaAmarilla(BigDecimal porcentajeInasistencia) {
        if (porcentajeInasistencia == null) {
            return false;
        }
        return porcentajeInasistencia.compareTo(porcentajeAlertaAmarilla) >= 0 &&
                porcentajeInasistencia.compareTo(porcentajeAlertaRoja) < 0;
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

    public BigDecimal getPorcentajeAlertaAmarilla() {
        return porcentajeAlertaAmarilla;
    }

    public void setPorcentajeAlertaAmarilla(BigDecimal porcentajeAlertaAmarilla) {
        this.porcentajeAlertaAmarilla = porcentajeAlertaAmarilla;
    }

    public BigDecimal getPorcentajeAlertaRoja() {
        return porcentajeAlertaRoja;
    }

    public void setPorcentajeAlertaRoja(BigDecimal porcentajeAlertaRoja) {
        this.porcentajeAlertaRoja = porcentajeAlertaRoja;
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
