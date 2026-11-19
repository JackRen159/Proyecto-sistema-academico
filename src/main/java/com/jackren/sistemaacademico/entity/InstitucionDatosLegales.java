package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "institucion_datos_legales",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_institucion_dane", columnNames = {"codigo_dane"})
    }
)
public class InstitucionDatosLegales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre_oficial", nullable = false, length = 200)
    private String nombreOficial;

    @Column(name = "codigo_dane", nullable = false, length = 12)
    private String codigoDane;

    @Column(nullable = false, length = 20)
    private String nit;

    @Column(name = "resolucion_aprobacion", nullable = false, length = 500)
    private String resolucionAprobacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CaracterInstitucion caracter = CaracterInstitucion.OFICIAL;

    @Column(name = "correo_institucional", length = 100)
    private String correoInstitucional;

    @Column(length = 50)
    private String telefono;

    @Column(length = 200)
    private String direccion;

    @Column(length = 100)
    private String municipio;

    @Column(length = 100)
    private String departamento;

    @Column(name = "nombre_rector_firmante", nullable = false, length = 150)
    private String nombreRectorFirmante;

    @Column(name = "cargo_rector_firmante", nullable = false, length = 100)
    private String cargoRectorFirmante = "Rector(a)";

    @Column(name = "documento_rector_firmante", length = 30)
    private String documentoRectorFirmante;

    @Column(name = "nombre_secretario_firmante", nullable = false, length = 150)
    private String nombreSecretarioFirmante;

    @Column(name = "cargo_secretario_firmante", nullable = false, length = 100)
    private String cargoSecretarioFirmante = "Secretario(a) Académico(a)";

    @Column(name = "documento_secretario_firmante", length = 30)
    private String documentoSecretarioFirmante;

    @Column(length = 255)
    private String lema;

    @Column(nullable = false)
    private Boolean activo = true;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public InstitucionDatosLegales() {
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
