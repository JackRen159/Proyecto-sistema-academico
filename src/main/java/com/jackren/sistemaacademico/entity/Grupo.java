package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "grupos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_grupos_anio_grado_nombre", columnNames = {"anio_lectivo_id", "grado_id", "nombre"})
    },
    indexes = {
        @Index(name = "idx_grupos_anio_lectivo", columnList = "anio_lectivo_id"),
        @Index(name = "idx_grupos_grado", columnList = "grado_id")
    }
)
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @Column(nullable = false, length = 20)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Jornada jornada = Jornada.MANANA;

    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo = 35;

    @Column(nullable = false, length = 100)
    private String sede = "SEDE_PRINCIPAL";

    @Column(name = "aula_base", length = 50)
    private String aulaBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoGrupo estado = EstadoGrupo.ACTIVO;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Grupo() {
    }

    public Grupo(AnioLectivo anioLectivo, Grado grado, String nombre, Jornada jornada, Integer cupoMaximo) {
        this.anioLectivo = anioLectivo;
        this.grado = grado;
        this.nombre = nombre;
        this.jornada = jornada != null ? jornada : Jornada.MANANA;
        this.cupoMaximo = cupoMaximo != null ? cupoMaximo : 35;
        this.activo = true;
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

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getAulaBase() {
        return aulaBase;
    }

    public void setAulaBase(String aulaBase) {
        this.aulaBase = aulaBase;
    }

    public EstadoGrupo getEstado() {
        return estado;
    }

    public void setEstado(EstadoGrupo estado) {
        this.estado = estado;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
