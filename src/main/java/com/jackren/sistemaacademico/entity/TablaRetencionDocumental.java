package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "tablas_retencion_documental",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_trd_serie", columnNames = {"codigo_serie"})
    },
    indexes = {
        @Index(name = "idx_trd_serie", columnList = "codigo_serie")
    }
)
public class TablaRetencionDocumental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_serie", nullable = false, length = 30, unique = true)
    private String codigoSerie;

    @Column(name = "nombre_serie", nullable = false, length = 150)
    private String nombreSerie;

    @Column(name = "subserie", length = 150)
    private String subserie;

    @Column(name = "anios_retencion_gestion", nullable = false)
    private Integer aniosRetencionGestion = 2;

    @Column(name = "anios_retencion_central", nullable = false)
    private Integer aniosRetencionCentral = 5;

    @Enumerated(EnumType.STRING)
    @Column(name = "disposicion_final", nullable = false, length = 40)
    private DisposicionFinalTRD disposicionFinal = DisposicionFinalTRD.CONSERVACION_TOTAL;

    @Column(name = "procedimiento_seguridad", columnDefinition = "TEXT")
    private String procedimientoSeguridad;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public TablaRetencionDocumental() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoSerie() { return codigoSerie; }
    public void setCodigoSerie(String codigoSerie) { this.codigoSerie = codigoSerie; }

    public String getNombreSerie() { return nombreSerie; }
    public void setNombreSerie(String nombreSerie) { this.nombreSerie = nombreSerie; }

    public String getSubserie() { return subserie; }
    public void setSubserie(String subserie) { this.subserie = subserie; }

    public Integer getAniosRetencionGestion() { return aniosRetencionGestion; }
    public void setAniosRetencionGestion(Integer aniosRetencionGestion) { this.aniosRetencionGestion = aniosRetencionGestion; }

    public Integer getAniosRetencionCentral() { return aniosRetencionCentral; }
    public void setAniosRetencionCentral(Integer aniosRetencionCentral) { this.aniosRetencionCentral = aniosRetencionCentral; }

    public DisposicionFinalTRD getDisposicionFinal() { return disposicionFinal; }
    public void setDisposicionFinal(DisposicionFinalTRD disposicionFinal) { this.disposicionFinal = disposicionFinal; }

    public String getProcedimientoSeguridad() { return procedimientoSeguridad; }
    public void setProcedimientoSeguridad(String procedimientoSeguridad) { this.procedimientoSeguridad = procedimientoSeguridad; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
