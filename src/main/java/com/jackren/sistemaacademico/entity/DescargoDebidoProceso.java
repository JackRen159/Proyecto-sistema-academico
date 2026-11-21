package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "descargos_debido_proceso",
    indexes = {
        @Index(name = "idx_descargo_expediente", columnList = "expediente_id")
    }
)
public class DescargoDebidoProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expediente_id", nullable = false)
    private ExpedienteDebidoProceso expediente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "presentado_por_usuario_id", nullable = false)
    private Usuario presentadoPorUsuario;

    @Column(name = "contenido_descargos", nullable = false, columnDefinition = "TEXT")
    private String contenidoDescargos;

    @Column(name = "pruebas_aportadas", columnDefinition = "TEXT")
    private String pruebasAportadas;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Column(name = "es_version_libre_audiencia", nullable = false)
    private Boolean esVersionLibreAudiencia = false;

    @Column(name = "participo_personero", nullable = false)
    private Boolean participoPersonero = false;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public DescargoDebidoProceso() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExpedienteDebidoProceso getExpediente() { return expediente; }
    public void setExpediente(ExpedienteDebidoProceso expediente) { this.expediente = expediente; }

    public Usuario getPresentadoPorUsuario() { return presentadoPorUsuario; }
    public void setPresentadoPorUsuario(Usuario presentadoPorUsuario) { this.presentadoPorUsuario = presentadoPorUsuario; }

    public String getContenidoDescargos() { return contenidoDescargos; }
    public void setContenidoDescargos(String contenidoDescargos) { this.contenidoDescargos = contenidoDescargos; }

    public String getPruebasAportadas() { return pruebasAportadas; }
    public void setPruebasAportadas(String pruebasAportadas) { this.pruebasAportadas = pruebasAportadas; }

    public LocalDateTime getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDateTime fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public Boolean getEsVersionLibreAudiencia() { return esVersionLibreAudiencia; }
    public void setEsVersionLibreAudiencia(Boolean esVersionLibreAudiencia) { this.esVersionLibreAudiencia = esVersionLibreAudiencia; }

    public Boolean getParticipoPersonero() { return participoPersonero; }
    public void setParticipoPersonero(Boolean participoPersonero) { this.participoPersonero = participoPersonero; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
