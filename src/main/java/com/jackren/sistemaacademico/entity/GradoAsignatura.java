package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "grados_asignaturas",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_grados_asignaturas", columnNames = {"grado_id", "asignatura_id"})
    },
    indexes = {
        @Index(name = "idx_grados_asignaturas_grado", columnList = "grado_id"),
        @Index(name = "idx_grados_asignaturas_asig", columnList = "asignatura_id")
    }
)
public class GradoAsignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_id")
    private AnioLectivo anioLectivo;

    @Column(name = "intensidad_horaria", nullable = false)
    private Integer intensidadHoraria = 1;

    @Column(name = "es_optativa", nullable = false)
    private Boolean esOptativa = false;

    @Column(name = "peso_porcentual_en_area", nullable = false, precision = 5, scale = 2)
    private BigDecimal pesoPorcentualEnArea = new BigDecimal("100.00");

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_componente", nullable = false, length = 30)
    private TipoComponenteCurricular tipoComponente = TipoComponenteCurricular.ASIGNATURA;

    @Column(name = "computa_promocion", nullable = false)
    private Boolean computaPromocion = true;

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

    public GradoAsignatura() {
    }

    public GradoAsignatura(Grado grado, Asignatura asignatura, Integer intensidadHoraria, Boolean esOptativa) {
        this.grado = grado;
        this.asignatura = asignatura;
        this.intensidadHoraria = intensidadHoraria;
        this.esOptativa = esOptativa != null ? esOptativa : false;
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public BigDecimal getPesoPorcentualEnArea() {
        return pesoPorcentualEnArea;
    }

    public void setPesoPorcentualEnArea(BigDecimal pesoPorcentualEnArea) {
        this.pesoPorcentualEnArea = pesoPorcentualEnArea;
    }

    public TipoComponenteCurricular getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponenteCurricular tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public Boolean getComputaPromocion() {
        return computaPromocion;
    }

    public void setComputaPromocion(Boolean computaPromocion) {
        this.computaPromocion = computaPromocion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public void setIntensidadHoraria(Integer intensidadHoraria) {
        this.intensidadHoraria = intensidadHoraria;
    }

    public Boolean getEsOptativa() {
        return esOptativa;
    }

    public void setEsOptativa(Boolean esOptativa) {
        this.esOptativa = esOptativa;
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
