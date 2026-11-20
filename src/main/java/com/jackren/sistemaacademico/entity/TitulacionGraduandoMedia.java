package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "titulacion_graduandos_media",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_titulacion_matricula", columnNames = {"matricula_id"})
    },
    indexes = {
        @Index(name = "idx_titulacion_mat", columnList = "matricula_id"),
        @Index(name = "idx_titulacion_estado", columnList = "estado_titulacion"),
        @Index(name = "idx_titulacion_fecha", columnList = "fecha_graduacion")
    }
)
public class TitulacionGraduandoMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad_titulo", nullable = false, length = 50)
    private ModalidadTituloBachiller modalidadTitulo = ModalidadTituloBachiller.BACHILLER_ACADEMICO;

    @Column(length = 100)
    private String especialidad;

    @Column(name = "horas_servicio_social", nullable = false)
    private Integer horasServicioSocial = 0;

    @Column(name = "soporte_servicio_social_url", length = 255)
    private String soporteServicioSocialUrl;

    @Column(name = "codigo_registro_icfes", length = 50)
    private String codigoRegistroIcfes;

    @Column(name = "documento_identidad_verificado", nullable = false)
    private Boolean documentoIdentidadVerificado = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_titulacion", nullable = false, length = 35)
    private EstadoTitulacionGraduando estadoTitulacion = EstadoTitulacionGraduando.APTO_GRADUACION;

    @Column(name = "numero_acta_general", length = 50)
    private String numeroActaGeneral;

    @Column(name = "numero_acta_individual", length = 50)
    private String numeroActaIndividual;

    @Column(name = "numero_diploma", length = 50)
    private String numeroDiploma;

    @Column(name = "numero_libro", length = 20)
    private String numeroLibro;

    @Column(name = "numero_folio", length = 20)
    private String numeroFolio;

    @Column(name = "fecha_graduacion")
    private LocalDate fechaGraduacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_autorizador_id")
    private Usuario usuarioAutorizador;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public TitulacionGraduandoMedia() {
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

    public ModalidadTituloBachiller getModalidadTitulo() {
        return modalidadTitulo;
    }

    public void setModalidadTitulo(ModalidadTituloBachiller modalidadTitulo) {
        this.modalidadTitulo = modalidadTitulo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getHorasServicioSocial() {
        return horasServicioSocial;
    }

    public void setHorasServicioSocial(Integer horasServicioSocial) {
        this.horasServicioSocial = horasServicioSocial;
    }

    public String getSoporteServicioSocialUrl() {
        return soporteServicioSocialUrl;
    }

    public void setSoporteServicioSocialUrl(String soporteServicioSocialUrl) {
        this.soporteServicioSocialUrl = soporteServicioSocialUrl;
    }

    public String getCodigoRegistroIcfes() {
        return codigoRegistroIcfes;
    }

    public void setCodigoRegistroIcfes(String codigoRegistroIcfes) {
        this.codigoRegistroIcfes = codigoRegistroIcfes;
    }

    public Boolean getDocumentoIdentidadVerificado() {
        return documentoIdentidadVerificado;
    }

    public void setDocumentoIdentidadVerificado(Boolean documentoIdentidadVerificado) {
        this.documentoIdentidadVerificado = documentoIdentidadVerificado;
    }

    public EstadoTitulacionGraduando getEstadoTitulacion() {
        return estadoTitulacion;
    }

    public void setEstadoTitulacion(EstadoTitulacionGraduando estadoTitulacion) {
        this.estadoTitulacion = estadoTitulacion;
    }

    public String getNumeroActaGeneral() {
        return numeroActaGeneral;
    }

    public void setNumeroActaGeneral(String numeroActaGeneral) {
        this.numeroActaGeneral = numeroActaGeneral;
    }

    public String getNumeroActaIndividual() {
        return numeroActaIndividual;
    }

    public void setNumeroActaIndividual(String numeroActaIndividual) {
        this.numeroActaIndividual = numeroActaIndividual;
    }

    public String getNumeroDiploma() {
        return numeroDiploma;
    }

    public void setNumeroDiploma(String numeroDiploma) {
        this.numeroDiploma = numeroDiploma;
    }

    public String getNumeroLibro() {
        return numeroLibro;
    }

    public void setNumeroLibro(String numeroLibro) {
        this.numeroLibro = numeroLibro;
    }

    public String getNumeroFolio() {
        return numeroFolio;
    }

    public void setNumeroFolio(String numeroFolio) {
        this.numeroFolio = numeroFolio;
    }

    public LocalDate getFechaGraduacion() {
        return fechaGraduacion;
    }

    public void setFechaGraduacion(LocalDate fechaGraduacion) {
        this.fechaGraduacion = fechaGraduacion;
    }

    public Usuario getUsuarioAutorizador() {
        return usuarioAutorizador;
    }

    public void setUsuarioAutorizador(Usuario usuarioAutorizador) {
        this.usuarioAutorizador = usuarioAutorizador;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
