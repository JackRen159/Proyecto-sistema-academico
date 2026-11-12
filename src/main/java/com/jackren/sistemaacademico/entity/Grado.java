package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "grados",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_grados_codigo", columnNames = {"codigo"}),
        @UniqueConstraint(name = "uk_grados_orden", columnNames = {"orden_jerarquico"})
    }
)
public class Grado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NivelEducativo nivel;

    @Column(name = "orden_jerarquico", nullable = false, unique = true)
    private Integer ordenJerarquico;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Grado() {
    }

    public Grado(String codigo, String nombre, NivelEducativo nivel, Integer ordenJerarquico) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.nivel = nivel;
        this.ordenJerarquico = ordenJerarquico;
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public NivelEducativo getNivel() {
        return nivel;
    }

    public void setNivel(NivelEducativo nivel) {
        this.nivel = nivel;
    }

    public Integer getOrdenJerarquico() {
        return ordenJerarquico;
    }

    public void setOrdenJerarquico(Integer ordenJerarquico) {
        this.ordenJerarquico = ordenJerarquico;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
