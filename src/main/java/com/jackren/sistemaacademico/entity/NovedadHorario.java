package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "novedades_horario",
    indexes = {
        @Index(name = "idx_novedades_bloque", columnList = "horario_bloque_id"),
        @Index(name = "idx_novedades_fechas", columnList = "fecha_inicio, fecha_fin")
    }
)
public class NovedadHorario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_bloque_id", nullable = false)
    private HorarioBloque horarioBloque;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_novedad", nullable = false, length = 35)
    private TipoNovedadHorario tipoNovedad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_reemplazo_id")
    private Aula aulaReemplazo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_reemplazo_id")
    private Profesor docenteReemplazo;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_autorizador_id")
    private Usuario usuarioAutorizador;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public NovedadHorario() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HorarioBloque getHorarioBloque() {
        return horarioBloque;
    }

    public void setHorarioBloque(HorarioBloque horarioBloque) {
        this.horarioBloque = horarioBloque;
    }

    public TipoNovedadHorario getTipoNovedad() {
        return tipoNovedad;
    }

    public void setTipoNovedad(TipoNovedadHorario tipoNovedad) {
        this.tipoNovedad = tipoNovedad;
    }

    public Aula getAulaReemplazo() {
        return aulaReemplazo;
    }

    public void setAulaReemplazo(Aula aulaReemplazo) {
        this.aulaReemplazo = aulaReemplazo;
    }

    public Profesor getDocenteReemplazo() {
        return docenteReemplazo;
    }

    public void setDocenteReemplazo(Profesor docenteReemplazo) {
        this.docenteReemplazo = docenteReemplazo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Usuario getUsuarioAutorizador() {
        return usuarioAutorizador;
    }

    public void setUsuarioAutorizador(Usuario usuarioAutorizador) {
        this.usuarioAutorizador = usuarioAutorizador;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
