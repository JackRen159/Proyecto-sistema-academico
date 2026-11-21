package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "actuaciones_ruta_atencion",
    indexes = {
        @Index(name = "idx_actuacion_caso", columnList = "caso_convivencia_id")
    }
)
public class ActuacionRutaAtencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caso_convivencia_id", nullable = false)
    private CasoConvivencia casoConvivencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actuacion", nullable = false, length = 50)
    private TipoActuacionRutaAtencion tipoActuacion;

    @Column(name = "descripcion_actuacion", nullable = false, columnDefinition = "TEXT")
    private String descripcionActuacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "responsable_usuario_id", nullable = false)
    private Usuario responsableUsuario;

    @CreationTimestamp
    @Column(name = "fecha_actuacion", nullable = false, updatable = false)
    private LocalDateTime fechaActuacion;

    @Column(name = "fecha_vencimiento_seguimiento")
    private LocalDate fechaVencimientoSeguimiento;

    @Column(name = "compromisos_acordados", columnDefinition = "TEXT")
    private String compromisosAcordados;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public ActuacionRutaAtencion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CasoConvivencia getCasoConvivencia() { return casoConvivencia; }
    public void setCasoConvivencia(CasoConvivencia casoConvivencia) { this.casoConvivencia = casoConvivencia; }

    public TipoActuacionRutaAtencion getTipoActuacion() { return tipoActuacion; }
    public void setTipoActuacion(TipoActuacionRutaAtencion tipoActuacion) { this.tipoActuacion = tipoActuacion; }

    public String getDescripcionActuacion() { return descripcionActuacion; }
    public void setDescripcionActuacion(String descripcionActuacion) { this.descripcionActuacion = descripcionActuacion; }

    public Usuario getResponsableUsuario() { return responsableUsuario; }
    public void setResponsableUsuario(Usuario responsableUsuario) { this.responsableUsuario = responsableUsuario; }

    public LocalDateTime getFechaActuacion() { return fechaActuacion; }
    public void setFechaActuacion(LocalDateTime fechaActuacion) { this.fechaActuacion = fechaActuacion; }

    public LocalDate getFechaVencimientoSeguimiento() { return fechaVencimientoSeguimiento; }
    public void setFechaVencimientoSeguimiento(LocalDate fechaVencimientoSeguimiento) { this.fechaVencimientoSeguimiento = fechaVencimientoSeguimiento; }

    public String getCompromisosAcordados() { return compromisosAcordados; }
    public void setCompromisosAcordados(String compromisosAcordados) { this.compromisosAcordados = compromisosAcordados; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
