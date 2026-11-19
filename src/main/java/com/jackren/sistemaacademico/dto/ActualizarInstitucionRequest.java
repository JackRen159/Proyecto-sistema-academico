package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.CaracterInstitucion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ActualizarInstitucionRequest {

    @NotBlank(message = "El nombre oficial de la institución es obligatorio")
    private String nombreOficial;

    @NotBlank(message = "El código DANE es obligatorio")
    @Size(min = 12, max = 12, message = "El código DANE debe tener exactamente 12 dígitos")
    private String codigoDane;

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    @NotBlank(message = "La resolución de aprobación es obligatoria")
    private String resolucionAprobacion;

    @NotNull(message = "El carácter (OFICIAL/PRIVADO) es obligatorio")
    private CaracterInstitucion caracter;

    private String correoInstitucional;
    private String telefono;
    private String direccion;
    private String municipio;
    private String departamento;

    @NotBlank(message = "El nombre del rector firmante es obligatorio")
    private String nombreRectorFirmante;

    private String cargoRectorFirmante = "Rector(a)";
    private String documentoRectorFirmante;

    @NotBlank(message = "El nombre de la secretaria firmante es obligatorio")
    private String nombreSecretarioFirmante;

    private String cargoSecretarioFirmante = "Secretario(a) Académico(a)";
    private String documentoSecretarioFirmante;
    private String lema;

    public ActualizarInstitucionRequest() {
    }

    public String getNombreOficial() {
        return nombreOficial;
    }

    public void setNombreOficial(String nombreOficial) {
        this.nombreOficial = nombreOficial;
    }

    public String getCodigoDane() {
        return codigoDane;
    }

    public void setCodigoDane(String codigoDane) {
        this.codigoDane = codigoDane;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getResolucionAprobacion() {
        return resolucionAprobacion;
    }

    public void setResolucionAprobacion(String resolucionAprobacion) {
        this.resolucionAprobacion = resolucionAprobacion;
    }

    public CaracterInstitucion getCaracter() {
        return caracter;
    }

    public void setCaracter(CaracterInstitucion caracter) {
        this.caracter = caracter;
    }

    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correoInstitucional) {
        this.correoInstitucional = correoInstitucional;
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

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNombreRectorFirmante() {
        return nombreRectorFirmante;
    }

    public void setNombreRectorFirmante(String nombreRectorFirmante) {
        this.nombreRectorFirmante = nombreRectorFirmante;
    }

    public String getCargoRectorFirmante() {
        return cargoRectorFirmante;
    }

    public void setCargoRectorFirmante(String cargoRectorFirmante) {
        this.cargoRectorFirmante = cargoRectorFirmante;
    }

    public String getDocumentoRectorFirmante() {
        return documentoRectorFirmante;
    }

    public void setDocumentoRectorFirmante(String documentoRectorFirmante) {
        this.documentoRectorFirmante = documentoRectorFirmante;
    }

    public String getNombreSecretarioFirmante() {
        return nombreSecretarioFirmante;
    }

    public void setNombreSecretarioFirmante(String nombreSecretarioFirmante) {
        this.nombreSecretarioFirmante = nombreSecretarioFirmante;
    }

    public String getCargoSecretarioFirmante() {
        return cargoSecretarioFirmante;
    }

    public void setCargoSecretarioFirmante(String cargoSecretarioFirmante) {
        this.cargoSecretarioFirmante = cargoSecretarioFirmante;
    }

    public String getDocumentoSecretarioFirmante() {
        return documentoSecretarioFirmante;
    }

    public void setDocumentoSecretarioFirmante(String documentoSecretarioFirmante) {
        this.documentoSecretarioFirmante = documentoSecretarioFirmante;
    }

    public String getLema() {
        return lema;
    }

    public void setLema(String lema) {
        this.lema = lema;
    }
}
