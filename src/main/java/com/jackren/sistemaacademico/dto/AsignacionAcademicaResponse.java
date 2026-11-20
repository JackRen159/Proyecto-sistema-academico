package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsignacionAcademica;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AsignacionAcademicaResponse {

    private Long id;
    private Long profesorId;
    private String profesorNombreCompleto;
    private String profesorDocumento;
    private Long grupoId;
    private String grupoNombre;
    private String gradoNombre;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Long asignaturaId;
    private String asignaturaNombre;
    private String asignaturaCodigo;
    private Integer intensidadHorariaSemanal;
    private Boolean esDirectorGrupo;
    private EstadoAsignacionAcademica estadoAsignacion;
    private LocalDate fechaInicioVigencia;
    private LocalDate fechaFinVigencia;
    private Boolean esReemplazoTemporal;
    private Long docenteTitularRelevadoId;
    private String docenteTitularRelevadoNombre;
    private String motivoRelevo;
    private String actaRectoralHorasExtras;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public AsignacionAcademicaResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(Long profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombreCompleto() {
        return profesorNombreCompleto;
    }

    public void setProfesorNombreCompleto(String profesorNombreCompleto) {
        this.profesorNombreCompleto = profesorNombreCompleto;
    }

    public String getProfesorDocumento() {
        return profesorDocumento;
    }

    public void setProfesorDocumento(String profesorDocumento) {
        this.profesorDocumento = profesorDocumento;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public void setAsignaturaId(Long asignaturaId) {
        this.asignaturaId = asignaturaId;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public void setAsignaturaNombre(String asignaturaNombre) {
        this.asignaturaNombre = asignaturaNombre;
    }

    public String getAsignaturaCodigo() {
        return asignaturaCodigo;
    }

    public void setAsignaturaCodigo(String asignaturaCodigo) {
        this.asignaturaCodigo = asignaturaCodigo;
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

    public String getDocenteTitularRelevadoNombre() {
        return docenteTitularRelevadoNombre;
    }

    public void setDocenteTitularRelevadoNombre(String docenteTitularRelevadoNombre) {
        this.docenteTitularRelevadoNombre = docenteTitularRelevadoNombre;
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
