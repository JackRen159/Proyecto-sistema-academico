package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoRegistroAsistencia;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class SesionAsistenciaRequest {

    private Long asignacionAcademicaId;

    @NotNull(message = "El grupo es obligatorio")
    private Long grupoId;

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    private LocalDate fecha;

    @Min(value = 1, message = "El bloque horario debe ser al menos 1")
    private Integer bloqueHorario = 1;

    private TipoRegistroAsistencia tipoRegistro = TipoRegistroAsistencia.SESION_ASIGNATURA;

    @Size(max = 150, message = "El tema no puede superar 150 caracteres")
    private String tema;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public SesionAsistenciaRequest() {
    }

    public Long getAsignacionAcademicaId() {
        return asignacionAcademicaId;
    }

    public void setAsignacionAcademicaId(Long asignacionAcademicaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
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
}
