package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "casos_convivencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_caso_conv_codigo", columnNames = {"codigo_expediente"})
    },
    indexes = {
        @Index(name = "idx_caso_conv_anio", columnList = "anio_lectivo_id"),
        @Index(name = "idx_caso_conv_est", columnList = "estudiante_implicado_id"),
        @Index(name = "idx_caso_conv_tipo", columnList = "tipo_situacion"),
        @Index(name = "idx_caso_conv_estado", columnList = "estado_caso")
    }
)
public class CasoConvivencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_expediente", nullable = false, length = 60, unique = true)
    private String codigoExpediente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_implicado_id", nullable = false)
    private Estudiante estudianteImplicado;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_situacion", nullable = false, length = 30)
    private TipoSituacionConvivencia tipoSituacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_caso", nullable = false, length = 65)
    private EstadoCasoConvivencia estadoCaso = EstadoCasoConvivencia.ABIERTO_PRELIMINAR_URGENTE;

    @Column(name = "fecha_hechos", nullable = false)
    private LocalDate fechaHechos;

    @Column(name = "lugar_hechos", nullable = false, length = 150)
    private String lugarHechos;

    @Column(name = "descripcion_situacion", nullable = false, columnDefinition = "TEXT")
    private String descripcionSituacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reportante_usuario_id")
    private Usuario reportanteUsuario;

    @Column(name = "es_reporte_confidencial", nullable = false)
    private Boolean esReporteConfidencial = false;

    @Column(name = "atencion_medica_urgente", nullable = false)
    private Boolean atencionMedicaUrgente = false;

    @Column(name = "detalle_atencion_medica", columnDefinition = "TEXT")
    private String detalleAtencionMedica;

    @Column(name = "reserva_proteccion_contra_acudiente", nullable = false)
    private Boolean reservaProteccionContraAcudiente = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acudiente_agresor_id")
    private Acudiente acudienteAgresor;

    @Column(name = "codigo_siuce", length = 80)
    private String codigoSiuce;

    @Column(name = "remitido_autoridad_externa", nullable = false)
    private Boolean remitidoAutoridadExterna = false;

    @Column(name = "autoridad_externa_detalle", columnDefinition = "TEXT")
    private String autoridadExternaDetalle;

    @Column(name = "fecha_remision_externa")
    private LocalDate fechaRemisionExterna;

    @Column(name = "radicado_remision_externa", length = 100)
    private String radicadoRemisionExterna;

    @CreationTimestamp
    @Column(name = "fecha_apertura", nullable = false, updatable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "motivo_cierre", columnDefinition = "TEXT")
    private String motivoCierre;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public CasoConvivencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoExpediente() { return codigoExpediente; }
    public void setCodigoExpediente(String codigoExpediente) { this.codigoExpediente = codigoExpediente; }

    public AnioLectivo getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(AnioLectivo anioLectivo) { this.anioLectivo = anioLectivo; }

    public Estudiante getEstudianteImplicado() { return estudianteImplicado; }
    public void setEstudianteImplicado(Estudiante estudianteImplicado) { this.estudianteImplicado = estudianteImplicado; }

    public TipoSituacionConvivencia getTipoSituacion() { return tipoSituacion; }
    public void setTipoSituacion(TipoSituacionConvivencia tipoSituacion) { this.tipoSituacion = tipoSituacion; }

    public EstadoCasoConvivencia getEstadoCaso() { return estadoCaso; }
    public void setEstadoCaso(EstadoCasoConvivencia estadoCaso) { this.estadoCaso = estadoCaso; }

    public LocalDate getFechaHechos() { return fechaHechos; }
    public void setFechaHechos(LocalDate fechaHechos) { this.fechaHechos = fechaHechos; }

    public String getLugarHechos() { return lugarHechos; }
    public void setLugarHechos(String lugarHechos) { this.lugarHechos = lugarHechos; }

    public String getDescripcionSituacion() { return descripcionSituacion; }
    public void setDescripcionSituacion(String descripcionSituacion) { this.descripcionSituacion = descripcionSituacion; }

    public Usuario getReportanteUsuario() { return reportanteUsuario; }
    public void setReportanteUsuario(Usuario reportanteUsuario) { this.reportanteUsuario = reportanteUsuario; }

    public Boolean getEsReporteConfidencial() { return esReporteConfidencial; }
    public void setEsReporteConfidencial(Boolean esReporteConfidencial) { this.esReporteConfidencial = esReporteConfidencial; }

    public Boolean getAtencionMedicaUrgente() { return atencionMedicaUrgente; }
    public void setAtencionMedicaUrgente(Boolean atencionMedicaUrgente) { this.atencionMedicaUrgente = atencionMedicaUrgente; }

    public String getDetalleAtencionMedica() { return detalleAtencionMedica; }
    public void setDetalleAtencionMedica(String detalleAtencionMedica) { this.detalleAtencionMedica = detalleAtencionMedica; }

    public Boolean getReservaProteccionContraAcudiente() { return reservaProteccionContraAcudiente; }
    public void setReservaProteccionContraAcudiente(Boolean reservaProteccionContraAcudiente) { this.reservaProteccionContraAcudiente = reservaProteccionContraAcudiente; }

    public Acudiente getAcudienteAgresor() { return acudienteAgresor; }
    public void setAcudienteAgresor(Acudiente acudienteAgresor) { this.acudienteAgresor = acudienteAgresor; }

    public String getCodigoSiuce() { return codigoSiuce; }
    public void setCodigoSiuce(String codigoSiuce) { this.codigoSiuce = codigoSiuce; }

    public Boolean getRemitidoAutoridadExterna() { return remitidoAutoridadExterna; }
    public void setRemitidoAutoridadExterna(Boolean remitidoAutoridadExterna) { this.remitidoAutoridadExterna = remitidoAutoridadExterna; }

    public String getAutoridadExternaDetalle() { return autoridadExternaDetalle; }
    public void setAutoridadExternaDetalle(String autoridadExternaDetalle) { this.autoridadExternaDetalle = autoridadExternaDetalle; }

    public LocalDate getFechaRemisionExterna() { return fechaRemisionExterna; }
    public void setFechaRemisionExterna(LocalDate fechaRemisionExterna) { this.fechaRemisionExterna = fechaRemisionExterna; }

    public String getRadicadoRemisionExterna() { return radicadoRemisionExterna; }
    public void setRadicadoRemisionExterna(String radicadoRemisionExterna) { this.radicadoRemisionExterna = radicadoRemisionExterna; }

    public LocalDateTime getFechaApertura() { return fechaApertura; }
    public void setFechaApertura(LocalDateTime fechaApertura) { this.fechaApertura = fechaApertura; }

    public LocalDateTime getFechaCierre() { return fechaCierre; }
    public void setFechaCierre(LocalDateTime fechaCierre) { this.fechaCierre = fechaCierre; }

    public String getMotivoCierre() { return motivoCierre; }
    public void setMotivoCierre(String motivoCierre) { this.motivoCierre = motivoCierre; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
