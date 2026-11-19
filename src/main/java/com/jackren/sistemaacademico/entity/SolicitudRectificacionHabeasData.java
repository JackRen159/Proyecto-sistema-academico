package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "solicitudes_rectificacion_habeas_data",
    indexes = {
        @Index(name = "idx_rectificacion_estudiante", columnList = "estudiante_id"),
        @Index(name = "idx_rectificacion_estado", columnList = "estado")
    }
)
public class SolicitudRectificacionHabeasData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_radicado", nullable = false, unique = true, length = 50)
    private String numeroRadicado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_solicitante_id", nullable = false)
    private Usuario usuarioSolicitante;

    @Column(name = "campos_a_modificar_json", nullable = false, columnDefinition = "TEXT")
    private String camposAModificarJson;

    @Column(name = "motivo_solicitud", nullable = false, length = 500)
    private String motivoSolicitud;

    @Column(name = "soporte_url", length = 255)
    private String soporteUrl;

    @CreationTimestamp
    @Column(name = "fecha_radicacion", nullable = false, updatable = false)
    private LocalDateTime fechaRadicacion;

    @Column(name = "fecha_limite_legal", nullable = false)
    private LocalDate fechaLimiteLegal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoSolicitudRectificacion estado = EstadoSolicitudRectificacion.PENDIENTE;

    @Column(name = "respuesta_motivada", columnDefinition = "TEXT")
    private String respuestaMotivada;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_resolutor_id")
    private Usuario usuarioResolutor;

    public SolicitudRectificacionHabeasData() {
    }

    public SolicitudRectificacionHabeasData(String numeroRadicado,
                                           Estudiante estudiante,
                                           Usuario usuarioSolicitante,
                                           String camposAModificarJson,
                                           String motivoSolicitud,
                                           LocalDate fechaLimiteLegal) {
        this.numeroRadicado = numeroRadicado;
        this.estudiante = estudiante;
        this.usuarioSolicitante = usuarioSolicitante;
        this.camposAModificarJson = camposAModificarJson;
        this.motivoSolicitud = motivoSolicitud;
        this.fechaLimiteLegal = fechaLimiteLegal;
        this.estado = EstadoSolicitudRectificacion.PENDIENTE;
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

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Usuario getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public String getCamposAModificarJson() {
        return camposAModificarJson;
    }

    public void setCamposAModificarJson(String camposAModificarJson) {
        this.camposAModificarJson = camposAModificarJson;
    }

    public String getMotivoSolicitud() {
        return motivoSolicitud;
    }

    public void setMotivoSolicitud(String motivoSolicitud) {
        this.motivoSolicitud = motivoSolicitud;
    }

    public String getSoporteUrl() {
        return soporteUrl;
    }

    public void setSoporteUrl(String soporteUrl) {
        this.soporteUrl = soporteUrl;
    }

    public LocalDateTime getFechaRadicacion() {
        return fechaRadicacion;
    }

    public void setFechaRadicacion(LocalDateTime fechaRadicacion) {
        this.fechaRadicacion = fechaRadicacion;
    }

    public LocalDate getFechaLimiteLegal() {
        return fechaLimiteLegal;
    }

    public void setFechaLimiteLegal(LocalDate fechaLimiteLegal) {
        this.fechaLimiteLegal = fechaLimiteLegal;
    }

    public EstadoSolicitudRectificacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoSolicitudRectificacion estado) {
        this.estado = estado;
    }

    public String getRespuestaMotivada() {
        return respuestaMotivada;
    }

    public void setRespuestaMotivada(String respuestaMotivada) {
        this.respuestaMotivada = respuestaMotivada;
    }

    public LocalDateTime getFechaResolucion() {
        return fechaResolucion;
    }

    public void setFechaResolucion(LocalDateTime fechaResolucion) {
        this.fechaResolucion = fechaResolucion;
    }

    public Usuario getUsuarioResolutor() {
        return usuarioResolutor;
    }

    public void setUsuarioResolutor(Usuario usuarioResolutor) {
        this.usuarioResolutor = usuarioResolutor;
    }
}
