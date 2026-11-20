package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "solicitudes_excusa_asistencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_solicitud_excusa_radicado", columnNames = {"numero_radicado"})
    },
    indexes = {
        @Index(name = "idx_solicitud_excusa_mat", columnList = "matricula_id"),
        @Index(name = "idx_solicitud_excusa_estado", columnList = "estado"),
        @Index(name = "idx_solicitud_excusa_fechas", columnList = "fecha_inicio, fecha_fin")
    }
)
public class SolicitudExcusaAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", nullable = false, length = 50)
    private String numeroRadicado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_usuario_id", nullable = false)
    private Usuario solicitanteUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol_solicitante", nullable = false, length = 20)
    private RolSolicitanteExcusa rolSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_justificacion", nullable = false, length = 35)
    private TipoJustificacionAsistencia tipoJustificacion;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "motivo_explicacion", nullable = false, columnDefinition = "TEXT")
    private String motivoExplicacion;

    @Column(name = "soporte_url", length = 255)
    private String soporteUrl;

    @Column(name = "es_dato_salud_sensible", nullable = false)
    private Boolean esDatoSaludSensible = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitudExcusa estado = EstadoSolicitudExcusa.PENDIENTE_REVISION;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Column(name = "fecha_limite_subsanacion")
    private LocalDate fechaLimiteSubsanacion;

    @Column(name = "observaciones_resolucion", columnDefinition = "TEXT")
    private String observacionesResolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_resolutor_id")
    private Usuario usuarioResolutor;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public SolicitudExcusaAsistencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroRadicado() {
        return numeroRadicado;
    }

    public void setNumeroRadicado(String numeroRadicado) {
        this.numeroRadicado = numeroRadicado;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Usuario getSolicitanteUsuario() {
        return solicitanteUsuario;
    }

    public void setSolicitanteUsuario(Usuario solicitanteUsuario) {
        this.solicitanteUsuario = solicitanteUsuario;
    }

    public RolSolicitanteExcusa getRolSolicitante() {
        return rolSolicitante;
    }

    public void setRolSolicitante(RolSolicitanteExcusa rolSolicitante) {
        this.rolSolicitante = rolSolicitante;
    }

    public TipoJustificacionAsistencia getTipoJustificacion() {
        return tipoJustificacion;
    }

    public void setTipoJustificacion(TipoJustificacionAsistencia tipoJustificacion) {
        this.tipoJustificacion = tipoJustificacion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivoExplicacion() {
        return motivoExplicacion;
    }

    public void setMotivoExplicacion(String motivoExplicacion) {
        this.motivoExplicacion = motivoExplicacion;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }

    public Boolean getEsDatoSaludSensible() {
        return esDatoSaludSensible;
    }

    public void setEsDatoSaludSensible(Boolean esDatoSaludSensible) {
        this.esDatoSaludSensible = esDatoSaludSensible;
    }

    public EstadoSolicitudExcusa getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudExcusa estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDateTime fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public LocalDate getFechaLimiteSubsanacion() {
        return fechaLimiteSubsanacion;
    }

    public void setFechaLimiteSubsanacion(LocalDate fechaLimiteSubsanacion) {
        this.fechaLimiteSubsanacion = fechaLimiteSubsanacion;
    }

    public String getObservacionesResolucion() {
        return observacionesResolucion;
    }

    public void setObservacionesResolucion(String observacionesResolucion) {
        this.observacionesResolucion = observacionesResolucion;
    }

    public Usuario getUsuarioResolutor() {
        return usuarioResolutor;
    }

    public void setUsuarioResolutor(Usuario usuarioResolutor) {
        this.usuarioResolutor = usuarioResolutor;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
