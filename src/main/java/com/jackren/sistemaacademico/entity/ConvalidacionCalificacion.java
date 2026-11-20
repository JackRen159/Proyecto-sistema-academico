package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "convalidaciones_calificacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_convalidacion_acta", columnNames = {"numero_acta"})
    },
    indexes = {
        @Index(name = "idx_convalidacion_matricula", columnList = "matricula_id"),
        @Index(name = "idx_convalidacion_periodo", columnList = "periodo_academico_id")
    }
)
public class ConvalidacionCalificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_acta", nullable = false, length = 50)
    private String numeroActa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_origen", nullable = false, length = 35)
    private TipoOrigenConvalidacion tipoOrigen;

    @Column(name = "institucion_origen", length = 150)
    private String institucionOrigen;

    @Column(name = "acta_soporte_url", length = 255)
    private String actaSoporteUrl;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coordinador_id")
    private Usuario coordinador;

    @Column(name = "fecha_convalidacion", nullable = false)
    private LocalDate fechaConvalidacion = LocalDate.now();

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "convalidacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConvalidacionCalificacionDetalle> detalles = new ArrayList<>();

    public ConvalidacionCalificacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public TipoOrigenConvalidacion getTipoOrigen() {
        return tipoOrigen;
    }

    public void setTipoOrigen(TipoOrigenConvalidacion tipoOrigen) {
        this.tipoOrigen = tipoOrigen;
    }

    public String getInstitucionOrigen() {
        return institucionOrigen;
    }

    public void setInstitucionOrigen(String institucionOrigen) {
        this.institucionOrigen = institucionOrigen;
    }

    public String getActaSoporteUrl() {
        return actaSoporteUrl;
    }

    public void setActaSoporteUrl(String actaSoporteUrl) {
        this.actaSoporteUrl = actaSoporteUrl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getCoordinador() {
        return coordinador;
    }

    public void setCoordinador(Usuario coordinador) {
        this.coordinador = coordinador;
    }

    public LocalDate getFechaConvalidacion() {
        return fechaConvalidacion;
    }

    public void setFechaConvalidacion(LocalDate fechaConvalidacion) {
        this.fechaConvalidacion = fechaConvalidacion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<ConvalidacionCalificacionDetalle> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<ConvalidacionCalificacionDetalle> detalles) {
        this.detalles = detalles;
    }

    public void addDetalle(ConvalidacionCalificacionDetalle detalle) {
        detalles.add(detalle);
        detalle.setConvalidacion(this);
    }
}
