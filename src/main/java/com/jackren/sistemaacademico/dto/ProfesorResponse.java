package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAdministrativoDocente;
import com.jackren.sistemaacademico.entity.TipoDocumento;
import com.jackren.sistemaacademico.entity.TipoVinculacionDocente;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ProfesorResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String correo;
    private String telefono;
    private String direccion;
    private String tituloAcademico;
    private String especialidad;
    private EstadoAdministrativoDocente estadoAdministrativo;
    private TipoVinculacionDocente tipoVinculacion;
    private Integer horasSemanalesOrdinarias;
    private Integer horasExtrasPermitidas;
    private Integer horasAsignadasActuales;
    private Boolean verificadoInhabilidades;
    private LocalDate fechaVerificacionInhabilidades;
    private String numeroCertificadoInhabilidades;
    private LocalDate fechaInicioSituacion;
    private LocalDate fechaFinSituacion;
    private String motivoSituacion;
    private Long usuarioId;
    private String username;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ProfesorResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
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

    public Integer getHorasAsignadasActuales() {
        return horasAsignadasActuales;
    }

    public void setHorasAsignadasActuales(Integer horasAsignadasActuales) {
        this.horasAsignadasActuales = horasAsignadasActuales;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
