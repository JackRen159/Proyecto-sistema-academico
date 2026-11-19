package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "autorizaciones_tratamiento_datos_menor",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_autorizacion_menor_politica", columnNames = {"estudiante_id", "politica_id"})
    },
    indexes = {
        @Index(name = "idx_aut_menor_estudiante", columnList = "estudiante_id")
    }
)
public class AutorizacionTratamientoDatosMenor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acudiente_id")
    private Acudiente acudiente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "politica_id", nullable = false)
    private PoliticaTratamientoDatos politica;

    @Column(name = "autoriza_finalidades_educativas_obligatorias", nullable = false)
    private Boolean autorizaFinalidadesEducativasObligatorias = true;

    @Column(name = "autoriza_uso_imagen_fotografia_opcional", nullable = false)
    private Boolean autorizaUsoImagenFotografiaOpcional = false;

    @CreationTimestamp
    @Column(name = "fecha_autorizacion", nullable = false, updatable = false)
    private LocalDateTime fechaAutorizacion;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    public AutorizacionTratamientoDatosMenor() {
    }

    public AutorizacionTratamientoDatosMenor(Estudiante estudiante,
                                            Acudiente acudiente,
                                            PoliticaTratamientoDatos politica,
                                            Boolean autorizaFinalidadesEducativasObligatorias,
                                            Boolean autorizaUsoImagenFotografiaOpcional,
                                            String direccionIp,
                                            String userAgent) {
        this.estudiante = estudiante;
        this.acudiente = acudiente;
        this.politica = politica;
        this.autorizaFinalidadesEducativasObligatorias = autorizaFinalidadesEducativasObligatorias != null ? autorizaFinalidadesEducativasObligatorias : true;
        this.autorizaUsoImagenFotografiaOpcional = autorizaUsoImagenFotografiaOpcional != null ? autorizaUsoImagenFotografiaOpcional : false;
        this.direccionIp = direccionIp;
        this.userAgent = userAgent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Acudiente getAcudiente() {
        return acudiente;
    }

    public void setAcudiente(Acudiente acudiente) {
        this.acudiente = acudiente;
    }

    public PoliticaTratamientoDatos getPolitica() {
        return politica;
    }

    public void setPolitica(PoliticaTratamientoDatos politica) {
        this.politica = politica;
    }

    public Boolean getAutorizaFinalidadesEducativasObligatorias() {
        return autorizaFinalidadesEducativasObligatorias;
    }

    public void setAutorizaFinalidadesEducativasObligatorias(Boolean autorizaFinalidadesEducativasObligatorias) {
        this.autorizaFinalidadesEducativasObligatorias = autorizaFinalidadesEducativasObligatorias;
    }

    public Boolean getAutorizaUsoImagenFotografiaOpcional() {
        return autorizaUsoImagenFotografiaOpcional;
    }

    public void setAutorizaUsoImagenFotografiaOpcional(Boolean autorizaUsoImagenFotografiaOpcional) {
        this.autorizaUsoImagenFotografiaOpcional = autorizaUsoImagenFotografiaOpcional;
    }

    public LocalDateTime getFechaAutorizacion() {
        return fechaAutorizacion;
    }

    public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
        this.fechaAutorizacion = fechaAutorizacion;
    }

    public String getDireccionIp() {
        return direccionIp;
    }

    public void setDireccionIp(String direccionIp) {
        this.direccionIp = direccionIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
}
