package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "asignaciones_academicas")
public class AsignacionAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id", nullable = false)
    private Profesor profesor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(name = "intensidad_horaria_semanal", nullable = false)
    private Integer intensidadHorariaSemanal = 1;

    @Column(name = "es_director_grupo", nullable = false)
    private Boolean esDirectorGrupo = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_asignacion", nullable = false, length = 30)
    private EstadoAsignacionAcademica estadoAsignacion = EstadoAsignacionAcademica.TITULAR;

    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;

    @Column(name = "fecha_fin_vigencia")
    private LocalDate fechaFinVigencia;

    @Column(name = "es_reemplazo_temporal", nullable = false)
    private Boolean esReemplazoTemporal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_titular_relevado_id")
    private Profesor docenteTitularRelevado;

    @Column(name = "motivo_relevo")
    private String motivoRelevo;

    @Column(name = "acta_rectoral_horas_extras", length = 50)
    private String actaRectoralHorasExtras;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public AsignacionAcademica() {
    }

    public AsignacionAcademica(Profesor profesor, Grupo grupo, Asignatura asignatura, Integer intensidadHorariaSemanal) {
        this.profesor = profesor;
        this.grupo = grupo;
        this.asignatura = asignatura;
        this.intensidadHorariaSemanal = intensidadHorariaSemanal;
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public Integer getIntensidadHorariaSemanal() {
        return intensidadHorariaSemanal;
    }

    public void setIntensidadHorariaSemanal(Integer intensidadHorariaSemanal) {
        this.intensidadHorariaSemanal = intensidadHorariaSemanal;
    }

    public Boolean getEsDirectorGrupo() {
        return esDirectorGrupo;
    }

    public void setEsDirectorGrupo(Boolean esDirectorGrupo) {
        this.esDirectorGrupo = esDirectorGrupo;
    }

    public EstadoAsignacionAcademica getEstadoAsignacion() {
        return estadoAsignacion;
    }

    public void setEstadoAsignacion(EstadoAsignacionAcademica estadoAsignacion) {
        this.estadoAsignacion = estadoAsignacion;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public Boolean getEsReemplazoTemporal() {
        return esReemplazoTemporal;
    }

    public void setEsReemplazoTemporal(Boolean esReemplazoTemporal) {
        this.esReemplazoTemporal = esReemplazoTemporal;
    }

    public Profesor getDocenteTitularRelevado() {
        return docenteTitularRelevado;
    }

    public void setDocenteTitularRelevado(Profesor docenteTitularRelevado) {
        this.docenteTitularRelevado = docenteTitularRelevado;
    }

    public String getMotivoRelevo() {
        return motivoRelevo;
    }

    public void setMotivoRelevo(String motivoRelevo) {
        this.motivoRelevo = motivoRelevo;
    }

    public String getActaRectoralHorasExtras() {
        return actaRectoralHorasExtras;
    }

    public void setActaRectoralHorasExtras(String actaRectoralHorasExtras) {
        this.actaRectoralHorasExtras = actaRectoralHorasExtras;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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
