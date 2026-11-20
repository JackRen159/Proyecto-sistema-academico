package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "profesores")
@AttributeOverride(
    name = "direccion",
    column = @Column(name = "direccion", length = 150)
)
public class Profesor extends Persona {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 10)
    private TipoDocumento tipoDocumento = TipoDocumento.CC;

    @Column(name = "titulo_academico", length = 100)
    private String tituloAcademico;

    @Column(length = 100)
    private String especialidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_administrativo", nullable = false, length = 30)
    private EstadoAdministrativoDocente estadoAdministrativo = EstadoAdministrativoDocente.ACTIVO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vinculacion", nullable = false, length = 40)
    private TipoVinculacionDocente tipoVinculacion = TipoVinculacionDocente.PROPIEDAD_ESTATUTO_1278;

    @Column(name = "horas_semanales_ordinarias", nullable = false)
    private Integer horasSemanalesOrdinarias = 22;

    @Column(name = "horas_extras_permitidas", nullable = false)
    private Integer horasExtrasPermitidas = 0;

    @Column(name = "verificado_inhabilidades", nullable = false)
    private Boolean verificadoInhabilidades = true;

    @Column(name = "fecha_verificacion_inhabilidades")
    private LocalDate fechaVerificacionInhabilidades;

    @Column(name = "numero_certificado_inhabilidades", length = 50)
    private String numeroCertificadoInhabilidades;

    @Column(name = "fecha_inicio_situacion")
    private LocalDate fechaInicioSituacion;

    @Column(name = "fecha_fin_situacion")
    private LocalDate fechaFinSituacion;

    @Column(name = "motivo_situacion")
    private String motivoSituacion;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    public Profesor() {
        super();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTituloAcademico() {
        return tituloAcademico;
    }

    public void setTituloAcademico(String tituloAcademico) {
        this.tituloAcademico = tituloAcademico;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public EstadoAdministrativoDocente getEstadoAdministrativo() {
        return estadoAdministrativo;
    }

    public void setEstadoAdministrativo(EstadoAdministrativoDocente estadoAdministrativo) {
        this.estadoAdministrativo = estadoAdministrativo;
    }

    public TipoVinculacionDocente getTipoVinculacion() {
        return tipoVinculacion;
    }

    public void setTipoVinculacion(TipoVinculacionDocente tipoVinculacion) {
        this.tipoVinculacion = tipoVinculacion;
    }

    public Integer getHorasSemanalesOrdinarias() {
        return horasSemanalesOrdinarias;
    }

    public void setHorasSemanalesOrdinarias(Integer horasSemanalesOrdinarias) {
        this.horasSemanalesOrdinarias = horasSemanalesOrdinarias;
    }

    public Integer getHorasExtrasPermitidas() {
        return horasExtrasPermitidas;
    }

    public void setHorasExtrasPermitidas(Integer horasExtrasPermitidas) {
        this.horasExtrasPermitidas = horasExtrasPermitidas;
    }

    public Boolean getVerificadoInhabilidades() {
        return verificadoInhabilidades;
    }

    public void setVerificadoInhabilidades(Boolean verificadoInhabilidades) {
        this.verificadoInhabilidades = verificadoInhabilidades;
    }

    public LocalDate getFechaVerificacionInhabilidades() {
        return fechaVerificacionInhabilidades;
    }

    public void setFechaVerificacionInhabilidades(LocalDate fechaVerificacionInhabilidades) {
        this.fechaVerificacionInhabilidades = fechaVerificacionInhabilidades;
    }

    public String getNumeroCertificadoInhabilidades() {
        return numeroCertificadoInhabilidades;
    }

    public void setNumeroCertificadoInhabilidades(String numeroCertificadoInhabilidades) {
        this.numeroCertificadoInhabilidades = numeroCertificadoInhabilidades;
    }

    public LocalDate getFechaInicioSituacion() {
        return fechaInicioSituacion;
    }

    public void setFechaInicioSituacion(LocalDate fechaInicioSituacion) {
        this.fechaInicioSituacion = fechaInicioSituacion;
    }

    public LocalDate getFechaFinSituacion() {
        return fechaFinSituacion;
    }

    public void setFechaFinSituacion(LocalDate fechaFinSituacion) {
        this.fechaFinSituacion = fechaFinSituacion;
    }

    public String getMotivoSituacion() {
        return motivoSituacion;
    }

    public void setMotivoSituacion(String motivoSituacion) {
        this.motivoSituacion = motivoSituacion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
