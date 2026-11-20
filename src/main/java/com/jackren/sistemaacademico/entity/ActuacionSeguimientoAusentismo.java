package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "actuaciones_seguimiento_ausentismo",
    indexes = {
        @Index(name = "idx_actuacion_asistencia_mat", columnList = "matricula_id"),
        @Index(name = "idx_actuacion_asistencia_per", columnList = "periodo_academico_id"),
        @Index(name = "idx_actuacion_asistencia_tipo", columnList = "tipo_actuacion")
    }
)
public class ActuacionSeguimientoAusentismo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_actuacion", nullable = false, length = 45)
    private TipoActuacionSeguimiento tipoActuacion;

    @Column(name = "fecha_actuacion", nullable = false)
    private LocalDate fechaActuacion = LocalDate.now();

    @Column(name = "descripcion_gestion", nullable = false, columnDefinition = "TEXT")
    private String descripcionGestion;

    @Column(name = "compromisos_acordados", columnDefinition = "TEXT")
    private String compromisosAcordados;

    @Column(name = "soporte_acta_url", length = 255)
    private String soporteActaUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_actuante_id")
    private Usuario usuarioActuante;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ActuacionSeguimientoAusentismo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public TipoActuacionSeguimiento getTipoActuacion() {
        return tipoActuacion;
    }

    public void setTipoActuacion(TipoActuacionSeguimiento tipoActuacion) {
        this.tipoActuacion = tipoActuacion;
    }

    public LocalDate getFechaActuacion() {
        return fechaActuacion;
    }

    public void setFechaActuacion(LocalDate fechaActuacion) {
        this.fechaActuacion = fechaActuacion;
    }

    public String getDescripcionGestion() {
        return descripcionGestion;
    }

    public void setDescripcionGestion(String descripcionGestion) {
        this.descripcionGestion = descripcionGestion;
    }

    public String getCompromisosAcordados() {
        return compromisosAcordados;
    }

    public void setCompromisosAcordados(String compromisosAcordados) {
        this.compromisosAcordados = compromisosAcordados;
    }

    public String getSoporteActaUrl() {
        return soporteActaUrl;
    }

    public void setSoporteActaUrl(String soporteActaUrl) {
        this.soporteActaUrl = soporteActaUrl;
    }

    public Usuario getUsuarioActuante() {
        return usuarioActuante;
    }

    public void setUsuarioActuante(Usuario usuarioActuante) {
        this.usuarioActuante = usuarioActuante;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
