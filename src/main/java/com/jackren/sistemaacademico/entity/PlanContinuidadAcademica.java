package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "planes_continuidad_academica",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_plan_cont_medida", columnNames = {"medida_sancion_id"})
    },
    indexes = {
        @Index(name = "idx_plan_cont_medida", columnList = "medida_sancion_id"),
        @Index(name = "idx_plan_cont_estudiante", columnList = "estudiante_id")
    }
)
public class PlanContinuidadAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medida_sancion_id", nullable = false, unique = true)
    private MedidaSancionConvivencia medidaSancion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "guias_talleres_descripcion", nullable = false, columnDefinition = "TEXT")
    private String guiasTalleresDescripcion;

    @Column(name = "mecanismo_evaluacion", nullable = false, columnDefinition = "TEXT")
    private String mecanismoEvaluacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_tutor_seguimiento_id")
    private Profesor docenteTutorSeguimiento;

    @Column(name = "cumplido_satisfactoriamente", nullable = false)
    private Boolean cumplidoSatisfactoriamente = false;

    @Column(name = "observaciones_evaluacion", columnDefinition = "TEXT")
    private String observacionesEvaluacion;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public PlanContinuidadAcademica() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MedidaSancionConvivencia getMedidaSancion() { return medidaSancion; }
    public void setMedidaSancion(MedidaSancionConvivencia medidaSancion) { this.medidaSancion = medidaSancion; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getGuiasTalleresDescripcion() { return guiasTalleresDescripcion; }
    public void setGuiasTalleresDescripcion(String guiasTalleresDescripcion) { this.guiasTalleresDescripcion = guiasTalleresDescripcion; }

    public String getMecanismoEvaluacion() { return mecanismoEvaluacion; }
    public void setMecanismoEvaluacion(String mecanismoEvaluacion) { this.mecanismoEvaluacion = mecanismoEvaluacion; }

    public Profesor getDocenteTutorSeguimiento() { return docenteTutorSeguimiento; }
    public void setDocenteTutorSeguimiento(Profesor docenteTutorSeguimiento) { this.docenteTutorSeguimiento = docenteTutorSeguimiento; }

    public Boolean getCumplidoSatisfactoriamente() { return cumplidoSatisfactoriamente; }
    public void setCumplidoSatisfactoriamente(Boolean cumplidoSatisfactoriamente) { this.cumplidoSatisfactoriamente = cumplidoSatisfactoriamente; }

    public String getObservacionesEvaluacion() { return observacionesEvaluacion; }
    public void setObservacionesEvaluacion(String observacionesEvaluacion) { this.observacionesEvaluacion = observacionesEvaluacion; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
