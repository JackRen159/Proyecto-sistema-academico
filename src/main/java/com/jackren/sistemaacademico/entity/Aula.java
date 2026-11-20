package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "aulas",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_aula_sede_codigo", columnNames = {"sede", "codigo"})
    },
    indexes = {
        @Index(name = "idx_aulas_sede", columnList = "sede"),
        @Index(name = "idx_aulas_tipo", columnList = "tipo_espacio"),
        @Index(name = "idx_aulas_estado", columnList = "estado")
    }
)
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sede = "SEDE_PRINCIPAL";

    @Column(nullable = false, length = 20)
    private String codigo;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_espacio", nullable = false, length = 35)
    private TipoEspacioFisico tipoEspacio = TipoEspacioFisico.AULA_ESTANDAR;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima = 40;

    @Column(name = "es_multigrupo", nullable = false)
    private Boolean esMultigrupo = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoAula estado = EstadoAula.DISPONIBLE;

    @Column(length = 255)
    private String observaciones;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public Aula() {
    }

    public Aula(String sede, String codigo, String nombre, TipoEspacioFisico tipoEspacio, Integer capacidadMaxima) {
        this.sede = sede != null ? sede : "SEDE_PRINCIPAL";
        this.codigo = codigo;
        this.nombre = nombre;
        this.tipoEspacio = tipoEspacio != null ? tipoEspacio : TipoEspacioFisico.AULA_ESTANDAR;
        this.capacidadMaxima = capacidadMaxima != null ? capacidadMaxima : 40;
        this.esMultigrupo = false;
        this.estado = EstadoAula.DISPONIBLE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEspacioFisico getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacioFisico tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Boolean getEsMultigrupo() {
        return esMultigrupo;
    }

    public void setEsMultigrupo(Boolean esMultigrupo) {
        this.esMultigrupo = esMultigrupo;
    }

    public EstadoAula getEstado() {
        return estado;
    }

    public void setEstado(EstadoAula estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
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
