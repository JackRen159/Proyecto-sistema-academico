package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "actividades_evaluativas",
    indexes = {
        @Index(name = "idx_actividades_asignacion", columnList = "asignacion_academica_id"),
        @Index(name = "idx_actividades_periodo", columnList = "periodo_academico_id"),
        @Index(name = "idx_actividades_estado", columnList = "estado"),
        @Index(name = "idx_actividades_siee_version", columnList = "siee_version_id")
    }
)
public class ActividadEvaluativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignacion_academica_id", nullable = false)
    private AsignacionAcademica asignacionAcademica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "fecha_evaluacion")
    private LocalDate fechaEvaluacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_evaluativa", nullable = false, length = 30)
    private CategoriaEvaluativa categoriaEvaluativa = CategoriaEvaluativa.COGNITIVA;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_componente", nullable = false, length = 30)
    private TipoComponenteEvaluativo tipoComponente = TipoComponenteEvaluativo.TALLER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ModalidadEvaluativa modalidad = ModalidadEvaluativa.HETEROEVALUACION;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoActividadEvaluativa estado = EstadoActividadEvaluativa.PUBLICADA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "siee_version_id")
    private ConfiguracionSieeVersion sieeVersion;

    @Column(name = "es_adaptacion_piar", nullable = false)
    private Boolean esAdaptacionPiar = false;

    @Column(name = "criterios_adaptados_piar", columnDefinition = "TEXT")
    private String criteriosAdaptadosPiar;

    @Column(nullable = false)
    private Boolean activo = true;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public ActividadEvaluativa() {
    }

    public ActividadEvaluativa(AsignacionAcademica asignacionAcademica,
                               PeriodoAcademico periodoAcademico,
                               String nombre,
                               String descripcion,
                               BigDecimal porcentaje,
                               LocalDate fechaEvaluacion) {
        this.asignacionAcademica = asignacionAcademica;
        this.periodoAcademico = periodoAcademico;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.porcentaje = porcentaje;
        this.fechaEvaluacion = fechaEvaluacion;
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AsignacionAcademica getAsignacionAcademica() {
        return asignacionAcademica;
    }

    public void setAsignacionAcademica(AsignacionAcademica asignacionAcademica) {
        this.asignacionAcademica = asignacionAcademica;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public CategoriaEvaluativa getCategoriaEvaluativa() {
        return categoriaEvaluativa;
    }

    public void setCategoriaEvaluativa(CategoriaEvaluativa categoriaEvaluativa) {
        this.categoriaEvaluativa = categoriaEvaluativa;
    }

    public TipoComponenteEvaluativo getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponenteEvaluativo tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public ModalidadEvaluativa getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadEvaluativa modalidad) {
        this.modalidad = modalidad;
    }

    public EstadoActividadEvaluativa getEstado() {
        return estado;
    }

    public void setEstado(EstadoActividadEvaluativa estado) {
        this.estado = estado;
    }

    public ConfiguracionSieeVersion getSieeVersion() {
        return sieeVersion;
    }

    public void setSieeVersion(ConfiguracionSieeVersion sieeVersion) {
        this.sieeVersion = sieeVersion;
    }

    public Boolean getEsAdaptacionPiar() {
        return esAdaptacionPiar;
    }

    public void setEsAdaptacionPiar(Boolean esAdaptacionPiar) {
        this.esAdaptacionPiar = esAdaptacionPiar;
    }

    public String getCriteriosAdaptadosPiar() {
        return criteriosAdaptadosPiar;
    }

    public void setCriteriosAdaptadosPiar(String criteriosAdaptadosPiar) {
        this.criteriosAdaptadosPiar = criteriosAdaptadosPiar;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
