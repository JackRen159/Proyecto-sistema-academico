package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class ExpedienteCompletoRequest {

    // ── DATOS ESTUDIANTE ──
    @NotBlank(message = "Los nombres del estudiante son obligatorios")
    private String nombres;

    @NotBlank(message = "Los apellidos del estudiante son obligatorios")
    private String apellidos;

    @NotNull(message = "El tipo de documento del estudiante es obligatorio")
    private TipoDocumento tipoDocumento = TipoDocumento.TI;

    @NotBlank(message = "El documento del estudiante es obligatorio")
    @Size(max = 20)
    private String documento;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    private String correo;
    private String telefono;

    @NotBlank(message = "La dirección de residencia es obligatoria")
    private String direccion;

    private SexoDocumental sexo = SexoDocumental.NO_ESPECIFICADO;
    private String eps = "Sin asegurar / En trámite";
    private String grupoSanguineoRh = "O+";
    private Boolean tieneDiscapacidadPiar = false;
    private String detallesSaludEmergencia;

    // ── ACUDIENTE PRINCIPAL OBLIGATORIO ──
    @NotBlank(message = "Los nombres del acudiente principal son obligatorios")
    private String acudienteNombres;

    @NotBlank(message = "Los apellidos del acudiente principal son obligatorios")
    private String acudienteApellidos;

    private TipoDocumento acudienteTipoDocumento = TipoDocumento.CC;

    @NotBlank(message = "El documento del acudiente principal es obligatorio")
    private String acudienteDocumento;

    @NotBlank(message = "El teléfono del acudiente principal es obligatorio")
    private String acudienteTelefono;

    private String acudienteCorreo;
    private String acudienteDireccion;
    private String acudienteOcupacion = "No especificada";

    @NotNull(message = "El parentesco del acudiente principal es obligatorio")
    private Parentesco parentesco = Parentesco.MADRE;

    private Boolean conviveConEstudiante = true;
    private Boolean autorizadoRetiro = true;
    private Boolean tieneCustodia = true;
    private Boolean representacionLegal = true;

    // ── AUTORIZACIÓN TRATAMIENTO DE DATOS MENOR (Ley 1581) ──
    private Boolean autorizaUsoImagenFotografiaOpcional = false;

    public ExpedienteCompletoRequest() {
    }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public SexoDocumental getSexo() { return sexo; }
    public void setSexo(SexoDocumental sexo) { this.sexo = sexo; }
    public String getEps() { return eps; }
    public void setEps(String eps) { this.eps = eps; }
    public String getGrupoSanguineoRh() { return grupoSanguineoRh; }
    public void setGrupoSanguineoRh(String grupoSanguineoRh) { this.grupoSanguineoRh = grupoSanguineoRh; }
    public Boolean getTieneDiscapacidadPiar() { return tieneDiscapacidadPiar; }
    public void setTieneDiscapacidadPiar(Boolean tieneDiscapacidadPiar) { this.tieneDiscapacidadPiar = tieneDiscapacidadPiar; }
    public String getDetallesSaludEmergencia() { return detallesSaludEmergencia; }
    public void setDetallesSaludEmergencia(String detallesSaludEmergencia) { this.detallesSaludEmergencia = detallesSaludEmergencia; }

    public String getAcudienteNombres() { return acudienteNombres; }
    public void setAcudienteNombres(String acudienteNombres) { this.acudienteNombres = acudienteNombres; }
    public String getAcudienteApellidos() { return acudienteApellidos; }
    public void setAcudienteApellidos(String acudienteApellidos) { this.acudienteApellidos = acudienteApellidos; }
    public TipoDocumento getAcudienteTipoDocumento() { return acudienteTipoDocumento; }
    public void setAcudienteTipoDocumento(TipoDocumento acudienteTipoDocumento) { this.acudienteTipoDocumento = acudienteTipoDocumento; }
    public String getAcudienteDocumento() { return acudienteDocumento; }
    public void setAcudienteDocumento(String acudienteDocumento) { this.acudienteDocumento = acudienteDocumento; }
    public String getAcudienteTelefono() { return acudienteTelefono; }
    public void setAcudienteTelefono(String acudienteTelefono) { this.acudienteTelefono = acudienteTelefono; }
    public String getAcudienteCorreo() { return acudienteCorreo; }
    public void setAcudienteCorreo(String acudienteCorreo) { this.acudienteCorreo = acudienteCorreo; }
    public String getAcudienteDireccion() { return acudienteDireccion; }
    public void setAcudienteDireccion(String acudienteDireccion) { this.acudienteDireccion = acudienteDireccion; }
    public String getAcudienteOcupacion() { return acudienteOcupacion; }
    public void setAcudienteOcupacion(String acudienteOcupacion) { this.acudienteOcupacion = acudienteOcupacion; }
    public Parentesco getParentesco() { return parentesco; }
    public void setParentesco(Parentesco parentesco) { this.parentesco = parentesco; }
    public Boolean getConviveConEstudiante() { return conviveConEstudiante; }
    public void setConviveConEstudiante(Boolean conviveConEstudiante) { this.conviveConEstudiante = conviveConEstudiante; }
    public Boolean getAutorizadoRetiro() { return autorizadoRetiro; }
    public void setAutorizadoRetiro(Boolean autorizadoRetiro) { this.autorizadoRetiro = autorizadoRetiro; }
    public Boolean getTieneCustodia() { return tieneCustodia; }
    public void setTieneCustodia(Boolean tieneCustodia) { this.tieneCustodia = tieneCustodia; }
    public Boolean getRepresentacionLegal() { return representacionLegal; }
    public void setRepresentacionLegal(Boolean representacionLegal) { this.representacionLegal = representacionLegal; }
    public Boolean getAutorizaUsoImagenFotografiaOpcional() { return autorizaUsoImagenFotografiaOpcional; }
    public void setAutorizaUsoImagenFotografiaOpcional(Boolean autorizaUsoImagenFotografiaOpcional) { this.autorizaUsoImagenFotografiaOpcional = autorizaUsoImagenFotografiaOpcional; }
}
