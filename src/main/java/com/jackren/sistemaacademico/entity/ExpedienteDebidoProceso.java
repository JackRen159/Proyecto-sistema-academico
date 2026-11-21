package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "expedientes_debido_proceso",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_exp_deb_numero", columnNames = {"numero_expediente"})
    },
    indexes = {
        @Index(name = "idx_exp_deb_caso", columnList = "caso_convivencia_id"),
        @Index(name = "idx_exp_deb_estudiante", columnList = "estudiante_id"),
        @Index(name = "idx_exp_deb_estado", columnList = "estado_expediente")
    }
)
public class ExpedienteDebidoProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_expediente", nullable = false, length = 60, unique = true)
    private String numeroExpediente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caso_convivencia_id", nullable = false)
    private CasoConvivencia casoConvivencia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_expediente", nullable = false, length = 65)
    private EstadoExpedienteDebidoProceso estadoExpediente = EstadoExpedienteDebidoProceso.APERTURA_NOTIFICADA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instructor_usuario_id", nullable = false)
    private Usuario instructorUsuario;

    @Column(name = "auto_apertura_hechos", nullable = false, columnDefinition = "TEXT")
    private String autoAperturaHechos;

    @Column(name = "normas_manual_infringidas", nullable = false, columnDefinition = "TEXT")
    private String normasManualInfringidas;

    @Column(name = "pruebas_trasladadas_anonimizadas", nullable = false, columnDefinition = "TEXT")
    private String pruebasTrasladadasAnonimizadas;

    @CreationTimestamp
    @Column(name = "fecha_notificacion_cargos", nullable = false, updatable = false)
    private LocalDateTime fechaNotificacionCargos;

    @Column(name = "dias_habiles_descargos", nullable = false)
    private Integer diasHabilesDescargos = 5;

    @Column(name = "fecha_limite_descargos", nullable = false)
    private LocalDate fechaLimiteDescargos;

    @Column(name = "constancia_vencimiento_descargos", columnDefinition = "TEXT")
    private String constanciaVencimientoDescargos;

    @Column(name = "acompanamiento_personero", nullable = false)
    private Boolean acompanamientoPersonero = false;

    @Column(name = "concepto_orientacion", columnDefinition = "TEXT")
    private String conceptoOrientacion;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public ExpedienteDebidoProceso() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroExpediente() { return numeroExpediente; }
    public void setNumeroExpediente(String numeroExpediente) { this.numeroExpediente = numeroExpediente; }

    public CasoConvivencia getCasoConvivencia() { return casoConvivencia; }
    public void setCasoConvivencia(CasoConvivencia casoConvivencia) { this.casoConvivencia = casoConvivencia; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public EstadoExpedienteDebidoProceso getEstadoExpediente() { return estadoExpediente; }
    public void setEstadoExpediente(EstadoExpedienteDebidoProceso estadoExpediente) { this.estadoExpediente = estadoExpediente; }

    public Usuario getInstructorUsuario() { return instructorUsuario; }
    public void setInstructorUsuario(Usuario instructorUsuario) { this.instructorUsuario = instructorUsuario; }

    public String getAutoAperturaHechos() { return autoAperturaHechos; }
    public void setAutoAperturaHechos(String autoAperturaHechos) { this.autoAperturaHechos = autoAperturaHechos; }

    public String getNormasManualInfringidas() { return normasManualInfringidas; }
    public void setNormasManualInfringidas(String normasManualInfringidas) { this.normasManualInfringidas = normasManualInfringidas; }

    public String getPruebasTrasladadasAnonimizadas() { return pruebasTrasladadasAnonimizadas; }
    public void setPruebasTrasladadasAnonimizadas(String pruebasTrasladadasAnonimizadas) { this.pruebasTrasladadasAnonimizadas = pruebasTrasladadasAnonimizadas; }

    public LocalDateTime getFechaNotificacionCargos() { return fechaNotificacionCargos; }
    public void setFechaNotificacionCargos(LocalDateTime fechaNotificacionCargos) { this.fechaNotificacionCargos = fechaNotificacionCargos; }

    public Integer getDiasHabilesDescargos() { return diasHabilesDescargos; }
    public void setDiasHabilesDescargos(Integer diasHabilesDescargos) { this.diasHabilesDescargos = diasHabilesDescargos; }

    public LocalDate getFechaLimiteDescargos() { return fechaLimiteDescargos; }
    public void setFechaLimiteDescargos(LocalDate fechaLimiteDescargos) { this.fechaLimiteDescargos = fechaLimiteDescargos; }

    public String getConstanciaVencimientoDescargos() { return constanciaVencimientoDescargos; }
    public void setConstanciaVencimientoDescargos(String constanciaVencimientoDescargos) { this.constanciaVencimientoDescargos = constanciaVencimientoDescargos; }

    public Boolean getAcompanamientoPersonero() { return acompanamientoPersonero; }
    public void setAcompanamientoPersonero(Boolean acompanamientoPersonero) { this.acompanamientoPersonero = acompanamientoPersonero; }

    public String getConceptoOrientacion() { return conceptoOrientacion; }
    public void setConceptoOrientacion(String conceptoOrientacion) { this.conceptoOrientacion = conceptoOrientacion; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
