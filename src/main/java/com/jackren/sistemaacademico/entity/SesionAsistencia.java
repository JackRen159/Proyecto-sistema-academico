package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "sesiones_asistencia",
    indexes = {
        @Index(name = "idx_sesiones_grupo", columnList = "grupo_id"),
        @Index(name = "idx_sesiones_asignacion", columnList = "asignacion_academica_id"),
        @Index(name = "idx_sesiones_periodo", columnList = "periodo_academico_id"),
        @Index(name = "idx_sesiones_fecha", columnList = "fecha")
    }
)
public class SesionAsistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_academica_id")
    private AsignacionAcademica asignacionAcademica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @Column(nullable = false)
    private LocalDate fecha = LocalDate.now();

    @Column(name = "bloque_horario", nullable = false)
    private Integer bloqueHorario = 1;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_registro", nullable = false, length = 30)
    private TipoRegistroAsistencia tipoRegistro = TipoRegistroAsistencia.SESION_ASIGNATURA;

    @Column(length = 150)
    private String tema;

    @Column(length = 255)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id")
    private Usuario usuarioCreador;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public SesionAsistencia() {
    }

    public SesionAsistencia(AsignacionAcademica asignacionAcademica,
                            Grupo grupo,
                            PeriodoAcademico periodoAcademico,
                            LocalDate fecha,
                            Integer bloqueHorario,
                            TipoRegistroAsistencia tipoRegistro,
                            String tema,
                            Usuario usuarioCreador) {
        this.asignacionAcademica = asignacionAcademica;
        this.grupo = grupo;
        this.periodoAcademico = periodoAcademico;
        this.fecha = fecha != null ? fecha : LocalDate.now();
        this.bloqueHorario = bloqueHorario != null ? bloqueHorario : 1;
        this.tipoRegistro = tipoRegistro != null ? tipoRegistro : TipoRegistroAsistencia.SESION_ASIGNATURA;
        this.tema = tema;
        this.usuarioCreador = usuarioCreador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AsignacionAcademica getAsignacionAcademica() {
        return asignacionAcademica;
    }

    public void setAsignacionAcademica(AsignacionAcademica asignacionAcademica) {
        this.asignacionAcademica = asignacionAcademica;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Integer getBloqueHorario() {
        return bloqueHorario;
    }

    public void setBloqueHorario(Integer bloqueHorario) {
        this.bloqueHorario = bloqueHorario;
    }

    public TipoRegistroAsistencia getTipoRegistro() {
        return tipoRegistro;
    }

    public void setTipoRegistro(TipoRegistroAsistencia tipoRegistro) {
        this.tipoRegistro = tipoRegistro;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
