package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAdministrativoDocente;
import com.jackren.sistemaacademico.entity.TipoDocumento;
import com.jackren.sistemaacademico.entity.TipoVinculacionDocente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ProfesorRequest {

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumento tipoDocumento = TipoDocumento.CC;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no puede superar 20 caracteres")
    private String documento;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 50, message = "Los nombres no pueden superar 50 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 50, message = "Los apellidos no pueden superar 50 caracteres")
    private String apellidos;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El formato de correo no es válido")
    @Size(max = 100, message = "El correo no puede superar 100 caracteres")
    private String correo;

    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    private String telefono;

    @Size(max = 150, message = "La dirección no puede superar 150 caracteres")
    private String direccion;

    @Size(max = 100, message = "El título académico no puede superar 100 caracteres")
    private String tituloAcademico;

    @Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
    private String especialidad;

    private EstadoAdministrativoDocente estadoAdministrativo = EstadoAdministrativoDocente.ACTIVO;

    private TipoVinculacionDocente tipoVinculacion = TipoVinculacionDocente.PROPIEDAD_ESTATUTO_1278;

    private Integer horasSemanalesOrdinarias = 22;

    private Integer horasExtrasPermitidas = 0;

    private Boolean verificadoInhabilidades = true;

    private LocalDate fechaVerificacionInhabilidades;

    @Size(max = 50, message = "El certificado de inhabilidades no puede superar 50 caracteres")
    private String numeroCertificadoInhabilidades;

    private LocalDate fechaInicioSituacion;

    private LocalDate fechaFinSituacion;

    @Size(max = 255, message = "El motivo no puede superar 255 caracteres")
    private String motivoSituacion;

    private Long usuarioId;

    public ProfesorRequest() {
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
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
}
