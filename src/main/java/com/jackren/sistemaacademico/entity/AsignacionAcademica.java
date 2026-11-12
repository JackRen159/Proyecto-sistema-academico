package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
