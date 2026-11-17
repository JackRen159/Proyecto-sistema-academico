package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "registro_emisiones_documentos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_emision_codigo_verificacion", columnNames = {"codigo_verificacion"}),
        @UniqueConstraint(name = "uk_emision_consecutivo", columnNames = {"anio_lectivo_id", "tipo_documento", "numero_consecutivo_completo"})
    },
    indexes = {
        @Index(name = "idx_emisiones_estudiante", columnList = "estudiante_id"),
        @Index(name = "idx_emisiones_matricula", columnList = "matricula_id"),
        @Index(name = "idx_emisiones_anio_tipo", columnList = "anio_lectivo_id, tipo_documento"),
        @Index(name = "idx_emisiones_codigo_verif", columnList = "codigo_verificacion")
    }
)
public class RegistroEmisionDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consecutivo_id")
    private ConsecutivoDocumento consecutivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumentoAcademico tipoDocumento;

    @Column(name = "numero_consecutivo_completo", nullable = false, length = 100)
    private String numeroConsecutivoCompleto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_academico_id")
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "codigo_verificacion", nullable = false, unique = true, length = 64)
    private String codigoVerificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_emisor_id")
    private Usuario usuarioEmisor;

    @Column(length = 200)
    private String destinatario;

    @Column(length = 255)
    private String motivo;

    @Column(name = "metadatos_json", columnDefinition = "TEXT")
    private String metadatosJson;

    @Column(nullable = false)
    private Boolean anulado = false;

    @Column(name = "motivo_anulacion", length = 255)
    private String motivoAnulacion;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_anulador_id")
    private Usuario usuarioAnulador;

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDateTime fechaEmision;

    public RegistroEmisionDocumento() {
    }

    public RegistroEmisionDocumento(ConsecutivoDocumento consecutivo,
                                    TipoDocumentoAcademico tipoDocumento,
                                    String numeroConsecutivoCompleto,
                                    AnioLectivo anioLectivo,
                                    Estudiante estudiante,
                                    Matricula matricula,
                                    PeriodoAcademico periodoAcademico,
                                    Grupo grupo,
                                    String codigoVerificacion,
                                    Usuario usuarioEmisor,
                                    String destinatario,
                                    String motivo,
                                    String metadatosJson) {
        this.consecutivo = consecutivo;
        this.tipoDocumento = tipoDocumento;
        this.numeroConsecutivoCompleto = numeroConsecutivoCompleto;
        this.anioLectivo = anioLectivo;
        this.estudiante = estudiante;
        this.matricula = matricula;
        this.periodoAcademico = periodoAcademico;
        this.grupo = grupo;
        this.codigoVerificacion = codigoVerificacion;
        this.usuarioEmisor = usuarioEmisor;
        this.destinatario = destinatario;
        this.motivo = motivo;
        this.metadatosJson = metadatosJson;
        this.anulado = false;
    }

    public void anular(String motivo, Usuario usuarioAnulador) {
        this.anulado = true;
        this.motivoAnulacion = motivo;
        this.usuarioAnulador = usuarioAnulador;
        this.fechaAnulacion = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConsecutivoDocumento getConsecutivo() {
        return consecutivo;
    }

    public void setConsecutivo(ConsecutivoDocumento consecutivo) {
        this.consecutivo = consecutivo;
    }

    public TipoDocumentoAcademico getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoAcademico tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroConsecutivoCompleto() {
        return numeroConsecutivoCompleto;
    }

    public void setNumeroConsecutivoCompleto(String numeroConsecutivoCompleto) {
        this.numeroConsecutivoCompleto = numeroConsecutivoCompleto;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
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

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getCodigoVerificacion() {
        return codigoVerificacion;
    }

    public void setCodigoVerificacion(String codigoVerificacion) {
        this.codigoVerificacion = codigoVerificacion;
    }

    public Usuario getUsuarioEmisor() {
        return usuarioEmisor;
    }

    public void setUsuarioEmisor(Usuario usuarioEmisor) {
        this.usuarioEmisor = usuarioEmisor;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getMetadatosJson() {
        return metadatosJson;
    }

    public void setMetadatosJson(String metadatosJson) {
        this.metadatosJson = metadatosJson;
    }

    public Boolean getAnulado() {
        return anulado;
    }

    public void setAnulado(Boolean anulado) {
        this.anulado = anulado;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    public LocalDateTime getFechaAnulacion() {
        return fechaAnulacion;
    }

    public void setFechaAnulacion(LocalDateTime fechaAnulacion) {
        this.fechaAnulacion = fechaAnulacion;
    }

    public Usuario getUsuarioAnulador() {
        return usuarioAnulador;
    }

    public void setUsuarioAnulador(Usuario usuarioAnulador) {
        this.usuarioAnulador = usuarioAnulador;
    }

    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
}
