package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "autorizaciones_salida_anticipada",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_autorizacion_salida_num", columnNames = {"numero_autorizacion"})
    },
    indexes = {
        @Index(name = "idx_autorizacion_salida_mat", columnList = "matricula_id"),
        @Index(name = "idx_autorizacion_salida_fecha", columnList = "fecha")
    }
)
public class AutorizacionSalidaAnticipada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_autorizacion", nullable = false, length = 50)
    private String numeroAutorizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(name = "hora_salida", nullable = false, length = 10)
    private String horaSalida;

    @Column(nullable = false, length = 255)
    private String motivo;

    @Column(name = "adulto_retira_nombre", nullable = false, length = 150)
    private String adultoRetiraNombre;

    @Column(name = "adulto_retira_documento", nullable = false, length = 30)
    private String adultoRetiraDocumento;

    @Column(name = "parentesco_autorizado", nullable = false, length = 50)
    private String parentescoAutorizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_porteria_validador_id")
    private Usuario usuarioPorteriaValidador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private EstadoAutorizacionSalida estado = EstadoAutorizacionSalida.AUTORIZADA_VERIFICADA;

    @Column(length = 255)
    private String observaciones;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public AutorizacionSalidaAnticipada() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroAutorizacion() {
        return numeroAutorizacion;
    }

    public void setNumeroAutorizacion(String numeroAutorizacion) {
        this.numeroAutorizacion = numeroAutorizacion;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getAdultoRetiraNombre() {
        return adultoRetiraNombre;
    }

    public void setAdultoRetiraNombre(String adultoRetiraNombre) {
        this.adultoRetiraNombre = adultoRetiraNombre;
    }

    public String getAdultoRetiraDocumento() {
        return adultoRetiraDocumento;
    }

    public void setAdultoRetiraDocumento(String adultoRetiraDocumento) {
        this.adultoRetiraDocumento = adultoRetiraDocumento;
    }

    public String getParentescoAutorizado() {
        return parentescoAutorizado;
    }

    public void setParentescoAutorizado(String parentescoAutorizado) {
        this.parentescoAutorizado = parentescoAutorizado;
    }

    public Usuario getUsuarioPorteriaValidador() {
        return usuarioPorteriaValidador;
    }

    public void setUsuarioPorteriaValidador(Usuario usuarioPorteriaValidador) {
        this.usuarioPorteriaValidador = usuarioPorteriaValidador;
    }

    public EstadoAutorizacionSalida getEstado() {
        return estado;
    }

    public void setEstado(EstadoAutorizacionSalida estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
