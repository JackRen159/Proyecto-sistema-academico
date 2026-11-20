package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "boletines_publicados_version",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_boletin_mat_per_version", columnNames = {"matricula_id", "periodo_academico_id", "numero_version"})
    },
    indexes = {
        @Index(name = "idx_boletin_ver_mat", columnList = "matricula_id"),
        @Index(name = "idx_boletin_ver_per", columnList = "periodo_academico_id"),
        @Index(name = "idx_boletin_ver_estado", columnList = "estado_boletin")
    }
)
public class BoletinPublicadoVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_boletin", nullable = false, length = 35)
    private EstadoBoletinVersion estadoBoletin = EstadoBoletinVersion.PUBLICADO_OFICIAL;

    @Column(name = "datos_json", nullable = false, columnDefinition = "TEXT")
    private String datosJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_publicador_id")
    private Usuario usuarioPublicador;

    @CreationTimestamp
    @Column(name = "fecha_publicacion", nullable = false, updatable = false)
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_reapertura")
    private LocalDateTime fechaReapertura;

    @Column(name = "motivo_reapertura", columnDefinition = "TEXT")
    private String motivoReapertura;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public BoletinPublicadoVersion() {
    }

    public BoletinPublicadoVersion(Matricula matricula,
                                   PeriodoAcademico periodoAcademico,
                                   Integer numeroVersion,
                                   EstadoBoletinVersion estadoBoletin,
                                   String datosJson,
                                   Usuario usuarioPublicador) {
        this.matricula = matricula;
        this.periodoAcademico = periodoAcademico;
        this.numeroVersion = numeroVersion != null ? numeroVersion : 1;
        this.estadoBoletin = estadoBoletin != null ? estadoBoletin : EstadoBoletinVersion.PUBLICADO_OFICIAL;
        this.datosJson = datosJson;
        this.usuarioPublicador = usuarioPublicador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoBoletinVersion getEstadoBoletin() {
        return estadoBoletin;
    }

    public void setEstadoBoletin(EstadoBoletinVersion estadoBoletin) {
        this.estadoBoletin = estadoBoletin;
    }

    public String getDatosJson() {
        return datosJson;
    }

    public void setDatosJson(String datosJson) {
        this.datosJson = datosJson;
    }

    public Usuario getUsuarioPublicador() {
        return usuarioPublicador;
    }

    public void setUsuarioPublicador(Usuario usuarioPublicador) {
        this.usuarioPublicador = usuarioPublicador;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public LocalDateTime getFechaReapertura() {
        return fechaReapertura;
    }

    public void setFechaReapertura(LocalDateTime fechaReapertura) {
        this.fechaReapertura = fechaReapertura;
    }

    public String getMotivoReapertura() {
        return motivoReapertura;
    }

    public void setMotivoReapertura(String motivoReapertura) {
        this.motivoReapertura = motivoReapertura;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
