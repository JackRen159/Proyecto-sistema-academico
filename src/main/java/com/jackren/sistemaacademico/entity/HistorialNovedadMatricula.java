package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "historial_novedades_matricula",
    indexes = {
        @Index(name = "idx_novedades_matricula", columnList = "matricula_id"),
        @Index(name = "idx_novedades_tipo", columnList = "tipo_novedad"),
        @Index(name = "idx_novedades_fecha", columnList = "fecha_efectiva")
    }
)
public class HistorialNovedadMatricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_novedad", nullable = false, length = 35)
    private TipoNovedadMatricula tipoNovedad = TipoNovedadMatricula.CAMBIO_GRUPO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_origen_id")
    private Grupo grupoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_destino_id")
    private Grupo grupoDestino;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_origen_id")
    private Grado gradoOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_destino_id")
    private Grado gradoDestino;

    @Column(name = "fecha_efectiva", nullable = false)
    private LocalDate fechaEfectiva = LocalDate.now();

    @Column(nullable = false, length = 500)
    private String motivo;

    @Column(name = "soporte_documento", length = 255)
    private String soporteDocumento;

    @Column(name = "numero_acta_resolucion", length = 100)
    private String numeroActaResolucion;

    @Column(name = "fecha_acta_resolucion")
    private LocalDate fechaActaResolucion;

    @Column(length = 100)
    private String solicitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    public HistorialNovedadMatricula() {
    }

    public HistorialNovedadMatricula(Matricula matricula, TipoNovedadMatricula tipoNovedad,
                                    Grupo grupoOrigen, Grupo grupoDestino, String motivo, Usuario usuario) {
        this.matricula = matricula;
        this.tipoNovedad = tipoNovedad != null ? tipoNovedad : TipoNovedadMatricula.CAMBIO_GRUPO;
        this.grupoOrigen = grupoOrigen;
        this.grupoDestino = grupoDestino;
        this.gradoOrigen = grupoOrigen != null ? grupoOrigen.getGrado() : null;
        this.gradoDestino = grupoDestino != null ? grupoDestino.getGrado() : null;
        this.motivo = motivo;
        this.usuario = usuario;
        this.fechaEfectiva = LocalDate.now();
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

    public TipoNovedadMatricula getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadMatricula tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public Grupo getGrupoOrigen() {
        return grupoOrigen;
    }

    public void setGrupoOrigen(Grupo grupoOrigen) {
        this.grupoOrigen = grupoOrigen;
    }

    public Grupo getGrupoDestino() {
        return grupoDestino;
    }

    public void setGrupoDestino(Grupo grupoDestino) {
        this.grupoDestino = grupoDestino;
    }

    public Grado getGradoOrigen() {
        return gradoOrigen;
    }

    public void setGradoOrigen(Grado gradoOrigen) {
        this.gradoOrigen = gradoOrigen;
    }

    public Grado getGradoDestino() {
        return gradoDestino;
    }

    public void setGradoDestino(Grado gradoDestino) {
        this.gradoDestino = gradoDestino;
    }

    public LocalDate getFechaEfectiva() {
        return fechaEfectiva;
    }

    public void setFechaEfectiva(LocalDate fechaEfectiva) {
        this.fechaEfectiva = fechaEfectiva;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getSoporteDocumento() {
        return soporteDocumento;
    }

    public void setSoporteDocumento(String soporteDocumento) {
        this.soporteDocumento = soporteDocumento;
    }

    public String getNumeroActaResolucion() {
        return numeroActaResolucion;
    }

    public void setNumeroActaResolucion(String numeroActaResolucion) {
        this.numeroActaResolucion = numeroActaResolucion;
    }

    public LocalDate getFechaActaResolucion() {
        return fechaActaResolucion;
    }

    public void setFechaActaResolucion(LocalDate fechaActaResolucion) {
        this.fechaActaResolucion = fechaActaResolucion;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
