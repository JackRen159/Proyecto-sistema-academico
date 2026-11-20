package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "promocion_estudiantes_anual",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_promocion_estudiante_matricula", columnNames = {"matricula_id"})
    },
    indexes = {
        @Index(name = "idx_prom_est_mat", columnList = "matricula_id"),
        @Index(name = "idx_prom_est_estado", columnList = "estado_promocion"),
        @Index(name = "idx_prom_est_acta", columnList = "acta_promocion_id")
    }
)
public class PromocionEstudianteAnual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "acta_promocion_id", nullable = false)
    private ActaPromocionAnual actaPromocion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_promocion", nullable = false, length = 40)
    private EstadoPromocionEstudiante estadoPromocion;

    @Column(name = "promedio_general", nullable = false, precision = 4, scale = 2)
    private BigDecimal promedioGeneral;

    @Column(name = "areas_reprobadas", nullable = false)
    private Integer areasReprobadas = 0;

    @Column(name = "asignaturas_reprobadas", nullable = false)
    private Integer asignaturasReprobadas = 0;

    @Column(name = "porcentaje_inasistencia_anual", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeInasistenciaAnual = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grado_destino_id")
    private Grado gradoDestino;

    @Column(name = "motivo_decision", columnDefinition = "TEXT")
    private String motivoDecision;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public PromocionEstudianteAnual() {
    }

    public PromocionEstudianteAnual(ActaPromocionAnual actaPromocion,
                                   Matricula matricula,
                                   EstadoPromocionEstudiante estadoPromocion,
                                   BigDecimal promedioGeneral,
                                   Integer areasReprobadas,
                                   Integer asignaturasReprobadas,
                                   BigDecimal porcentajeInasistenciaAnual,
                                   Grado gradoDestino,
                                   String motivoDecision) {
        this.actaPromocion = actaPromocion;
        this.matricula = matricula;
        this.estadoPromocion = estadoPromocion;
        this.promedioGeneral = promedioGeneral;
        this.areasReprobadas = areasReprobadas != null ? areasReprobadas : 0;
        this.asignaturasReprobadas = asignaturasReprobadas != null ? asignaturasReprobadas : 0;
        this.porcentajeInasistenciaAnual = porcentajeInasistenciaAnual != null ? porcentajeInasistenciaAnual : BigDecimal.ZERO;
        this.gradoDestino = gradoDestino;
        this.motivoDecision = motivoDecision;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ActaPromocionAnual getActaPromocion() {
        return actaPromocion;
    }

    public void setActaPromocion(ActaPromocionAnual actaPromocion) {
        this.actaPromocion = actaPromocion;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public EstadoPromocionEstudiante getEstadoPromocion() {
        return estadoPromocion;
    }

    public void setEstadoPromocion(EstadoPromocionEstudiante estadoPromocion) {
        this.estadoPromocion = estadoPromocion;
    }

    public BigDecimal getPromedioGeneral() {
        return promedioGeneral;
    }

    public void setPromedioGeneral(BigDecimal promedioGeneral) {
        this.promedioGeneral = promedioGeneral;
    }

    public Integer getAreasReprobadas() {
        return areasReprobadas;
    }

    public void setAreasReprobadas(Integer areasReprobadas) {
        this.areasReprobadas = areasReprobadas;
    }

    public Integer getAsignaturasReprobadas() {
        return asignaturasReprobadas;
    }

    public void setAsignaturasReprobadas(Integer asignaturasReprobadas) {
        this.asignaturasReprobadas = asignaturasReprobadas;
    }

    public BigDecimal getPorcentajeInasistenciaAnual() {
        return porcentajeInasistenciaAnual;
    }

    public void setPorcentajeInasistenciaAnual(BigDecimal porcentajeInasistenciaAnual) {
        this.porcentajeInasistenciaAnual = porcentajeInasistenciaAnual;
    }

    public Grado getGradoDestino() {
        return gradoDestino;
    }

    public void setGradoDestino(Grado gradoDestino) {
        this.gradoDestino = gradoDestino;
    }

    public String getMotivoDecision() {
        return motivoDecision;
    }

    public void setMotivoDecision(String motivoDecision) {
        this.motivoDecision = motivoDecision;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
