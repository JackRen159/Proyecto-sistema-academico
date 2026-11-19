package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "historial_documentos_identidad",
    indexes = {
        @Index(name = "idx_historial_doc_estudiante", columnList = "estudiante_id"),
        @Index(name = "idx_historial_doc_anterior", columnList = "numero_documento_anterior")
    }
)
public class HistorialDocumentoIdentidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_anterior", nullable = false, length = 20)
    private TipoDocumento tipoDocumentoAnterior;

    @Column(name = "numero_documento_anterior", nullable = false, length = 20)
    private String numeroDocumentoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento_nuevo", nullable = false, length = 20)
    private TipoDocumento tipoDocumentoNuevo;

    @Column(name = "numero_documento_nuevo", nullable = false, length = 20)
    private String numeroDocumentoNuevo;

    @CreationTimestamp
    @Column(name = "fecha_cambio", nullable = false, updatable = false)
    private LocalDateTime fechaCambio;

    @Column(name = "motivo_cambio", nullable = false, length = 255)
    private String motivoCambio;

    @Column(name = "soporte_documento_url", length = 255)
    private String soporteDocumentoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_autorizador_id")
    private Usuario usuarioAutorizador;

    public HistorialDocumentoIdentidad() {
    }

    public HistorialDocumentoIdentidad(Estudiante estudiante,
                                       TipoDocumento tipoDocumentoAnterior,
                                       String numeroDocumentoAnterior,
                                       TipoDocumento tipoDocumentoNuevo,
                                       String numeroDocumentoNuevo,
                                       String motivoCambio,
                                       String soporteDocumentoUrl,
                                       Usuario usuarioAutorizador) {
        this.estudiante = estudiante;
        this.tipoDocumentoAnterior = tipoDocumentoAnterior;
        this.numeroDocumentoAnterior = numeroDocumentoAnterior;
        this.tipoDocumentoNuevo = tipoDocumentoNuevo;
        this.numeroDocumentoNuevo = numeroDocumentoNuevo;
        this.motivoCambio = motivoCambio;
        this.soporteDocumentoUrl = soporteDocumentoUrl;
        this.usuarioAutorizador = usuarioAutorizador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public TipoDocumento getTipoDocumentoAnterior() {
        return tipoDocumentoAnterior;
    }

    public void setTipoDocumentoAnterior(TipoDocumento tipoDocumentoAnterior) {
        this.tipoDocumentoAnterior = tipoDocumentoAnterior;
    }

    public String getNumeroDocumentoAnterior() {
        return numeroDocumentoAnterior;
    }

    public void setNumeroDocumentoAnterior(String numeroDocumentoAnterior) {
        this.numeroDocumentoAnterior = numeroDocumentoAnterior;
    }

    public TipoDocumento getTipoDocumentoNuevo() {
        return tipoDocumentoNuevo;
    }

    public void setTipoDocumentoNuevo(TipoDocumento tipoDocumentoNuevo) {
        this.tipoDocumentoNuevo = tipoDocumentoNuevo;
    }

    public String getNumeroDocumentoNuevo() {
        return numeroDocumentoNuevo;
    }

    public void setNumeroDocumentoNuevo(String numeroDocumentoNuevo) {
        this.numeroDocumentoNuevo = numeroDocumentoNuevo;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public void setFechaCambio(LocalDateTime fechaCambio) {
        this.fechaCambio = fechaCambio;
    }

    public String getMotivoCambio() {
        return motivoCambio;
    }

    public void setMotivoCambio(String motivoCambio) {
        this.motivoCambio = motivoCambio;
    }

    public String getSoporteDocumentoUrl() {
        return soporteDocumentoUrl;
    }

    public void setSoporteDocumentoUrl(String soporteDocumentoUrl) {
        this.soporteDocumentoUrl = soporteDocumentoUrl;
    }

    public Usuario getUsuarioAutorizador() {
        return usuarioAutorizador;
    }

    public void setUsuarioAutorizador(Usuario usuarioAutorizador) {
        this.usuarioAutorizador = usuarioAutorizador;
    }
}
