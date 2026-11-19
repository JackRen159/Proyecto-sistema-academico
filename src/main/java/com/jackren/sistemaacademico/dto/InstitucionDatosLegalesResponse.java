package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.CaracterInstitucion;

import java.time.LocalDateTime;

public class InstitucionDatosLegalesResponse {

    private Long id;
    private String nombreOficial;
    private String codigoDane;
    private String nit;
    private String resolucionAprobacion;
    private CaracterInstitucion caracter;
    private String correoInstitucional;
    private String telefono;
    private String direccion;
    private String municipio;
    private String departamento;
    private String nombreRectorFirmante;
    private String cargoRectorFirmante;
    private String documentoRectorFirmante;
    private String nombreSecretarioFirmante;
    private String cargoSecretarioFirmante;
    private String documentoSecretarioFirmante;
    private String lema;
    private Boolean activo;
    private LocalDateTime fechaActualizacion;

    public InstitucionDatosLegalesResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
