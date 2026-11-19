package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "organos_colegiados_miembros",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_organo_usuario_anio", columnNames = {"tipo_organo", "usuario_id", "anio_lectivo_id"})
    },
    indexes = {
        @Index(name = "idx_organos_usuario", columnList = "usuario_id"),
        @Index(name = "idx_organos_tipo_anio", columnList = "tipo_organo, anio_lectivo_id")
    }
)
public class OrganoColegiadoMiembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_organo", nullable = false, length = 50)
    private TipoOrganoColegiado tipoOrgano;

    @Column(name = "acta_designacion", nullable = false, length = 100)
    private String actaDesignacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio = LocalDate.now();

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(length = 255)
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "designado_por_id")
    private Usuario designadoPor;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public OrganoColegiadoMiembro() {
    }

    public OrganoColegiadoMiembro(Usuario usuario, TipoOrganoColegiado tipoOrgano, String actaDesignacion, AnioLectivo anioLectivo) {
        this.usuario = usuario;
        this.tipoOrgano = tipoOrgano;
        this.actaDesignacion = actaDesignacion;
        this.anioLectivo = anioLectivo;
        this.activo = true;
        this.fechaInicio = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoOrganoColegiado getTipoOrgano() {
        return tipoOrgano;
    }

    public void setTipoOrgano(TipoOrganoColegiado tipoOrgano) {
        this.tipoOrgano = tipoOrgano;
    }

    public String getActaDesignacion() {
        return actaDesignacion;
    }

    public void setActaDesignacion(String actaDesignacion) {
        this.actaDesignacion = actaDesignacion;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getDesignadoPor() {
        return designadoPor;
    }

    public void setDesignadoPor(Usuario designadoPor) {
        this.designadoPor = designadoPor;
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
