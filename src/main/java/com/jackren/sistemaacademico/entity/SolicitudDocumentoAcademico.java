package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "solicitudes_documentos_academicos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_sol_doc_radicado", columnNames = {"numero_radicado"})
    },
    indexes = {
        @Index(name = "idx_sol_doc_est", columnList = "estudiante_id"),
        @Index(name = "idx_sol_doc_estado", columnList = "estado_solicitud")
    }
)
public class SolicitudDocumentoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", nullable = false, length = 60, unique = true)
    private String numeroRadicado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "solicitante_usuario_id", nullable = false)
    private Usuario solicitanteUsuario;

    @Column(name = "rol_solicitante", nullable = false, length = 30)
    private String rolSolicitante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumentoAcademico tipoDocumento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "motivo_solicitud", nullable = false, columnDefinition = "TEXT")
    private String motivoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_solicitud", nullable = false, length = 40)
    private EstadoSolicitudDocumento estadoSolicitud = EstadoSolicitudDocumento.RADICADA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emision_generada_id")
    private RegistroEmisionDocumento emisionGenerada;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @Column(name = "observacion_secretaria", columnDefinition = "TEXT")
    private String observacionSecretaria;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public SolicitudDocumentoAcademico() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumeroRadicado() { return numeroRadicado; }
    public void setNumeroRadicado(String numeroRadicado) { this.numeroRadicado = numeroRadicado; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Usuario getSolicitanteUsuario() { return solicitanteUsuario; }
    public void setSolicitanteUsuario(Usuario solicitanteUsuario) { this.solicitanteUsuario = solicitanteUsuario; }

    public String getRolSolicitante() { return rolSolicitante; }
    public void setRolSolicitante(String rolSolicitante) { this.rolSolicitante = rolSolicitante; }

    public TipoDocumentoAcademico getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumentoAcademico tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public AnioLectivo getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(AnioLectivo anioLectivo) { this.anioLectivo = anioLectivo; }

    public String getMotivoSolicitud() { return motivoSolicitud; }
    public void setMotivoSolicitud(String motivoSolicitud) { this.motivoSolicitud = motivoSolicitud; }

    public EstadoSolicitudDocumento getEstadoSolicitud() { return estadoSolicitud; }
    public void setEstadoSolicitud(EstadoSolicitudDocumento estadoSolicitud) { this.estadoSolicitud = estadoSolicitud; }

    public RegistroEmisionDocumento getEmisionGenerada() { return emisionGenerada; }
    public void setEmisionGenerada(RegistroEmisionDocumento emisionGenerada) { this.emisionGenerada = emisionGenerada; }

    public LocalDateTime getFechaRadicacion() { return fechaRadicacion; }
    public void setFechaRadicacion(LocalDateTime fechaRadicacion) { this.fechaRadicacion = fechaRadicacion; }

    public LocalDateTime getFechaResolucion() { return fechaResolucion; }
    public void setFechaResolucion(LocalDateTime fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    public String getObservacionSecretaria() { return observacionSecretaria; }
    public void setObservacionSecretaria(String observacionSecretaria) { this.observacionSecretaria = observacionSecretaria; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
