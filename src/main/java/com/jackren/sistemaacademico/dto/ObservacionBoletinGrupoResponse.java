package com.jackren.sistemaacademico.dto;

import java.time.LocalDateTime;

public class ObservacionBoletinGrupoResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long periodoId;
    private String periodoNombre;
    private Long docenteDirectorId;
    private String docenteDirectorNombre;
    private String observacionFormativa;
    private String recomendacionesPedagogicas;
    private LocalDateTime fechaRegistro;

    public ObservacionBoletinGrupoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public Long getPeriodoId() {
        return periodoId;
    }

    public void setPeriodoId(Long periodoId) {
        this.periodoId = periodoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Long getDocenteDirectorId() {
        return docenteDirectorId;
    }

    public void setDocenteDirectorId(Long docenteDirectorId) {
        this.docenteDirectorId = docenteDirectorId;
    }

    public String getDocenteDirectorNombre() {
        return docenteDirectorNombre;
    }

    public void setDocenteDirectorNombre(String docenteDirectorNombre) {
        this.docenteDirectorNombre = docenteDirectorNombre;
    }

    public String getObservacionFormativa() {
        return observacionFormativa;
    }

    public void setObservacionFormativa(String observacionFormativa) {
        this.observacionFormativa = observacionFormativa;
    }

    public String getRecomendacionesPedagogicas() {
        return recomendacionesPedagogicas;
    }

    public void setRecomendacionesPedagogicas(String recomendacionesPedagogicas) {
        this.recomendacionesPedagogicas = recomendacionesPedagogicas;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
