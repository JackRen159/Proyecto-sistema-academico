package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "anotaciones_observador_adendas",
    indexes = {
        @Index(name = "idx_adenda_anotacion", columnList = "anotacion_id")
    }
)
public class AnotacionObservadorAdenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anotacion_id", nullable = false)
    private AnotacionObservador anotacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_usuario_id", nullable = false)
    private Usuario autorUsuario;

    @Column(name = "motivo_adenda", nullable = false, columnDefinition = "TEXT")
    private String motivoAdenda;

    @Column(name = "contenido_adenda", nullable = false, columnDefinition = "TEXT")
    private String contenidoAdenda;

    @CreationTimestamp
    @Column(name = "fecha_adenda", nullable = false, updatable = false)
    private LocalDateTime fechaAdenda;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public AnotacionObservadorAdenda() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AnotacionObservador getAnotacion() { return anotacion; }
    public void setAnotacion(AnotacionObservador anotacion) { this.anotacion = anotacion; }

    public Usuario getAutorUsuario() { return autorUsuario; }
    public void setAutorUsuario(Usuario autorUsuario) { this.autorUsuario = autorUsuario; }

    public String getMotivoAdenda() { return motivoAdenda; }
    public void setMotivoAdenda(String motivoAdenda) { this.motivoAdenda = motivoAdenda; }

    public String getContenidoAdenda() { return contenidoAdenda; }
    public void setContenidoAdenda(String contenidoAdenda) { this.contenidoAdenda = contenidoAdenda; }

    public LocalDateTime getFechaAdenda() { return fechaAdenda; }
    public void setFechaAdenda(LocalDateTime fechaAdenda) { this.fechaAdenda = fechaAdenda; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
