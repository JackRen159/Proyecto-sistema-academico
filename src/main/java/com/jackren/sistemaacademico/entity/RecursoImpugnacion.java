package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "recursos_impugnacion",
    indexes = {
        @Index(name = "idx_recurso_medida", columnList = "medida_sancion_id"),
        @Index(name = "idx_recurso_estado", columnList = "estado_recurso")
    }
)
public class RecursoImpugnacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medida_sancion_id", nullable = false)
    private MedidaSancionConvivencia medidaSancion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_recurso", nullable = false, length = 45)
    private TipoRecursoImpugnacion tipoRecurso;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recurrente_usuario_id", nullable = false)
    private Usuario recurrenteUsuario;

    @Column(name = "sustentacion_recurso", nullable = false, columnDefinition = "TEXT")
    private String sustentacionRecurso;

    @Column(name = "pruebas_adjuntas", columnDefinition = "TEXT")
    private String pruebasAdjuntas;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_recurso", nullable = false, length = 45)
    private EstadoRecursoImpugnacion estadoRecurso = EstadoRecursoImpugnacion.RADICADO_EN_TRAMITE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolutor_usuario_id")
    private Usuario resolutorUsuario;

    @Column(name = "resolutor_rol", length = 40)
    private String resolutorRol;

    @Column(name = "acta_consejo_directivo_resolucion", length = 80)
    private String actaConsejoDirectivoResolucion;

    @Column(name = "motivo_resolucion_recurso", columnDefinition = "TEXT")
    private String motivoResolucionRecurso;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public RecursoImpugnacion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedidaSancionConvivencia getMedidaSancion() { return medidaSancion; }
    public void setMedidaSancion(MedidaSancionConvivencia medidaSancion) { this.medidaSancion = medidaSancion; }

    public TipoRecursoImpugnacion getTipoRecurso() { return tipoRecurso; }
    public void setTipoRecurso(TipoRecursoImpugnacion tipoRecurso) { this.tipoRecurso = tipoRecurso; }

    public Usuario getRecurrenteUsuario() { return recurrenteUsuario; }
    public void setRecurrenteUsuario(Usuario recurrenteUsuario) { this.recurrenteUsuario = recurrenteUsuario; }

    public String getSustentacionRecurso() { return sustentacionRecurso; }
    public void setSustentacionRecurso(String sustentacionRecurso) { this.sustentacionRecurso = sustentacionRecurso; }

    public String getPruebasAdjuntas() { return pruebasAdjuntas; }
    public void setPruebasAdjuntas(String pruebasAdjuntas) { this.pruebasAdjuntas = pruebasAdjuntas; }

    public LocalDateTime getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDateTime fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public EstadoRecursoImpugnacion getEstadoRecurso() { return estadoRecurso; }
    public void setEstadoRecurso(EstadoRecursoImpugnacion estadoRecurso) { this.estadoRecurso = estadoRecurso; }

    public Usuario getResolutorUsuario() { return resolutorUsuario; }
    public void setResolutorUsuario(Usuario resolutorUsuario) { this.resolutorUsuario = resolutorUsuario; }

    public String getResolutorRol() { return resolutorRol; }
    public void setResolutorRol(String resolutorRol) { this.resolutorRol = resolutorRol; }

    public String getActaConsejoDirectivoResolucion() { return actaConsejoDirectivoResolucion; }
    public void setActaConsejoDirectivoResolucion(String actaConsejoDirectivoResolucion) { this.actaConsejoDirectivoResolucion = actaConsejoDirectivoResolucion; }

    public String getMotivoResolucionRecurso() { return motivoResolucionRecurso; }
    public void setMotivoResolucionRecurso(String motivoResolucionRecurso) { this.motivoResolucionRecurso = motivoResolucionRecurso; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
