package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "configuracion_manual_convivencia_versiones",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_manual_convivencia_version", columnNames = {"numero_version"})
    }
)
public class ConfiguracionManualConvivenciaVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Column(name = "nombre_version", nullable = false, length = 100)
    private String nombreVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoVersionConfiguracion estado = EstadoVersionConfiguracion.BORRADOR;

    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;

    @Column(name = "acta_comite_convivencia", length = 100)
    private String actaComiteConvivencia;

    @Column(name = "acta_consejo_directivo", length = 100)
    private String actaConsejoDirectivo;

    @Column(name = "situaciones_tipo_1_json", columnDefinition = "TEXT")
    private String situacionesTipo1Json;

    @Column(name = "situaciones_tipo_2_json", columnDefinition = "TEXT")
    private String situacionesTipo2Json;

    @Column(name = "situaciones_tipo_3_json", columnDefinition = "TEXT")
    private String situacionesTipo3Json;

    @Column(name = "plazos_debido_proceso_json", columnDefinition = "TEXT")
    private String plazosDebidoProcesoJson;

    @Column(name = "soporte_pdf_url", length = 255)
    private String soportePdfUrl;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id")
    private Usuario usuarioCreador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_aprobador_id")
    private Usuario usuarioAprobador;

    public ConfiguracionManualConvivenciaVersion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public EstadoVersionConfiguracion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVersionConfiguracion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public String getActaComiteConvivencia() {
        return actaComiteConvivencia;
    }

    public void setActaComiteConvivencia(String actaComiteConvivencia) {
        this.actaComiteConvivencia = actaComiteConvivencia;
    }

    public String getActaConsejoDirectivo() {
        return actaConsejoDirectivo;
    }

    public void setActaConsejoDirectivo(String actaConsejoDirectivo) {
        this.actaConsejoDirectivo = actaConsejoDirectivo;
    }

    public String getSituacionesTipo1Json() {
        return situacionesTipo1Json;
    }

    public void setSituacionesTipo1Json(String situacionesTipo1Json) {
        this.situacionesTipo1Json = situacionesTipo1Json;
    }

    public String getSituacionesTipo2Json() {
        return situacionesTipo2Json;
    }

    public void setSituacionesTipo2Json(String situacionesTipo2Json) {
        this.situacionesTipo2Json = situacionesTipo2Json;
    }

    public String getSituacionesTipo3Json() {
        return situacionesTipo3Json;
    }

    public void setSituacionesTipo3Json(String situacionesTipo3Json) {
        this.situacionesTipo3Json = situacionesTipo3Json;
    }

    public String getPlazosDebidoProcesoJson() {
        return plazosDebidoProcesoJson;
    }

    public void setPlazosDebidoProcesoJson(String plazosDebidoProcesoJson) {
        this.plazosDebidoProcesoJson = plazosDebidoProcesoJson;
    }

    public String getSoportePdfUrl() {
        return soportePdfUrl;
    }

    public void setSoportePdfUrl(String soportePdfUrl) {
        this.soportePdfUrl = soportePdfUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Usuario getUsuarioAprobador() {
        return usuarioAprobador;
    }

    public void setUsuarioAprobador(Usuario usuarioAprobador) {
        this.usuarioAprobador = usuarioAprobador;
    }
}
