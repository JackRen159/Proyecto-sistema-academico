package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "medidas_sanciones_convivencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_medida_sancion_num", columnNames = {"numero_resolucion"})
    },
    indexes = {
        @Index(name = "idx_medida_expediente", columnList = "expediente_id"),
        @Index(name = "idx_medida_estado", columnList = "estado_decision")
    }
)
public class MedidaSancionConvivencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "expediente_id", nullable = false)
    private ExpedienteDebidoProceso expediente;

    @Column(name = "numero_resolucion", nullable = false, length = 60, unique = true)
    private String numeroResolucion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_medida", nullable = false, length = 50)
    private TipoMedidaSancion tipoMedida;

    @Column(name = "es_sancion_disciplinaria", nullable = false)
    private Boolean esSancionDisciplinaria = false;

    @Column(name = "motivacion_hechos_normas", nullable = false, columnDefinition = "TEXT")
    private String motivacionHechosNormas;

    @Column(name = "analisis_proporcionalidad", nullable = false, columnDefinition = "TEXT")
    private String analisisProporcionalidad;

    @Column(name = "dias_suspension")
    private Integer diasSuspension;

    @Column(name = "fecha_inicio_suspension")
    private LocalDate fechaInicioSuspension;

    @Column(name = "fecha_fin_suspension")
    private LocalDate fechaFinSuspension;

    @Column(name = "acta_consejo_directivo", length = 80)
    private String actaConsejoDirectivo;

    @Column(name = "fecha_acta_consejo_directivo")
    private LocalDate fechaActaConsejoDirectivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoridad_firmante_id", nullable = false)
    private Usuario autoridadFirmante;

    @Column(name = "rol_firmante", nullable = false, length = 40)
    private String rolFirmante;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_decision", nullable = false, length = 45)
    private EstadoDecisionSancion estadoDecision = EstadoDecisionSancion.EMITIDA_PENDIENTE_NOTIFICACION;

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_notificacion")
    private LocalDateTime fechaNotificacion;

    @Column(name = "fecha_firmeza")
    private LocalDateTime fechaFirmeza;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public MedidaSancionConvivencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ExpedienteDebidoProceso getExpediente() { return expediente; }
    public void setExpediente(ExpedienteDebidoProceso expediente) { this.expediente = expediente; }

    public String getNumeroResolucion() { return numeroResolucion; }
    public void setNumeroResolucion(String numeroResolucion) { this.numeroResolucion = numeroResolucion; }

    public TipoMedidaSancion getTipoMedida() { return tipoMedida; }
    public void setTipoMedida(TipoMedidaSancion tipoMedida) { this.tipoMedida = tipoMedida; }

    public Boolean getEsSancionDisciplinaria() { return esSancionDisciplinaria; }
    public void setEsSancionDisciplinaria(Boolean esSancionDisciplinaria) { this.esSancionDisciplinaria = esSancionDisciplinaria; }

    public String getMotivacionHechosNormas() { return motivacionHechosNormas; }
    public void setMotivacionHechosNormas(String motivacionHechosNormas) { this.motivacionHechosNormas = motivacionHechosNormas; }

    public String getAnalisisProporcionalidad() { return analisisProporcionalidad; }
    public void setAnalisisProporcionalidad(String analisisProporcionalidad) { this.analisisProporcionalidad = analisisProporcionalidad; }

    public Integer getDiasSuspension() { return diasSuspension; }
    public void setDiasSuspension(Integer diasSuspension) { this.diasSuspension = diasSuspension; }

    public LocalDate getFechaInicioSuspension() { return fechaInicioSuspension; }
    public void setFechaInicioSuspension(LocalDate fechaInicioSuspension) { this.fechaInicioSuspension = fechaInicioSuspension; }

    public LocalDate getFechaFinSuspension() { return fechaFinSuspension; }
    public void setFechaFinSuspension(LocalDate fechaFinSuspension) { this.fechaFinSuspension = fechaFinSuspension; }

    public String getActaConsejoDirectivo() { return actaConsejoDirectivo; }
    public void setActaConsejoDirectivo(String actaConsejoDirectivo) { this.actaConsejoDirectivo = actaConsejoDirectivo; }

    public LocalDate getFechaActaConsejoDirectivo() { return fechaActaConsejoDirectivo; }
    public void setFechaActaConsejoDirectivo(LocalDate fechaActaConsejoDirectivo) { this.fechaActaConsejoDirectivo = fechaActaConsejoDirectivo; }

    public Usuario getAutoridadFirmante() { return autoridadFirmante; }
    public void setAutoridadFirmante(Usuario autoridadFirmante) { this.autoridadFirmante = autoridadFirmante; }

    public String getRolFirmante() { return rolFirmante; }
    public void setRolFirmante(String rolFirmante) { this.rolFirmante = rolFirmante; }

    public EstadoDecisionSancion getEstadoDecision() { return estadoDecision; }
    public void setEstadoDecision(EstadoDecisionSancion estadoDecision) { this.estadoDecision = estadoDecision; }

    public LocalDateTime getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDateTime fechaEmision) { this.fechaEmision = fechaEmision; }

    public LocalDateTime getFechaNotificacion() { return fechaNotificacion; }
    public void setFechaNotificacion(LocalDateTime fechaNotificacion) { this.fechaNotificacion = fechaNotificacion; }

    public LocalDateTime getFechaFirmeza() { return fechaFirmeza; }
    public void setFechaFirmeza(LocalDateTime fechaFirmeza) { this.fechaFirmeza = fechaFirmeza; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
