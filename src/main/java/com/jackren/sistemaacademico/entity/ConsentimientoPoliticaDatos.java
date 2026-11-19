package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "consentimientos_politica_datos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_politica_version", columnNames = {"usuario_id", "politica_id"})
    },
    indexes = {
        @Index(name = "idx_consentimiento_usuario", columnList = "usuario_id"),
        @Index(name = "idx_consentimiento_politica", columnList = "politica_id")
    }
)
public class ConsentimientoPoliticaDatos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "politica_id", nullable = false)
    private PoliticaTratamientoDatos politica;

    @CreationTimestamp
    @Column(name = "fecha_aceptacion", updatable = false)
    private LocalDateTime fechaAceptacion;

    @Column(name = "direccion_ip", length = 45)
    private String direccionIp;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private OrigenConsentimiento origen = OrigenConsentimiento.LOGIN_PRIMER_INGRESO;

    public ConsentimientoPoliticaDatos() {
    }

    public ConsentimientoPoliticaDatos(Usuario usuario, PoliticaTratamientoDatos politica, String direccionIp, String userAgent, OrigenConsentimiento origen) {
        this.usuario = usuario;
        this.politica = politica;
        this.direccionIp = direccionIp;
        this.userAgent = userAgent;
        this.origen = origen != null ? origen : OrigenConsentimiento.LOGIN_PRIMER_INGRESO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public PoliticaTratamientoDatos getPolitica() {
        return politica;
    }

    public void setPolitica(PoliticaTratamientoDatos politica) {
        this.politica = politica;
    }

    public LocalDateTime getFechaAceptacion() {
        return fechaAceptacion;
    }

    public void setFechaAceptacion(LocalDateTime fechaAceptacion) {
        this.fechaAceptacion = fechaAceptacion;
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

    public OrigenConsentimiento getOrigen() {
        return origen;
    }

    public void setOrigen(OrigenConsentimiento origen) {
        this.origen = origen;
    }
}
