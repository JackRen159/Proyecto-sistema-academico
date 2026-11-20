package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsignacionAcademica;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AsignacionAcademicaRequest {

    @NotNull(message = "El profesor es obligatorio")
    private Long profesorId;

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    @NotNull(message = "La asignatura es obligatoria")
    private Long asignaturaId;

    @NotNull(message = "La intensidad horaria semanal es obligatoria")
    @Min(value = 1, message = "La intensidad horaria semanal debe ser al menos de 1 hora")
    private Integer intensidadHorariaSemanal = 1;

    private Boolean esDirectorGrupo = false;

    private EstadoAsignacionAcademica estadoAsignacion = EstadoAsignacionAcademica.TITULAR;

    private LocalDate fechaInicioVigencia;

    private LocalDate fechaFinVigencia;

    private Boolean esReemplazoTemporal = false;

    private Long docenteTitularRelevadoId;

    @Size(max = 255, message = "El motivo de relevo no puede superar los 255 caracteres")
    private String motivoRelevo;

    @Size(max = 50, message = "El acta rectoral de horas extras no puede superar los 50 caracteres")
    private String actaRectoralHorasExtras;

    public AsignacionAcademicaRequest() {
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
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

    public Long getDocenteTitularRelevadoId() {
        return docenteTitularRelevadoId;
    }

    public void setDocenteTitularRelevadoId(Long docenteTitularRelevadoId) {
        this.docenteTitularRelevadoId = docenteTitularRelevadoId;
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
}
