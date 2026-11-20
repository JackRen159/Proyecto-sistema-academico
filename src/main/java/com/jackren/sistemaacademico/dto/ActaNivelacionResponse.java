package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;
import com.jackren.sistemaacademico.entity.FormulaRecuperacionSiee;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ActaNivelacionResponse {

    private Long id;
    private String numeroActa;
    private Long calificacionPeriodoId;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long asignaturaId;
    private String asignaturaNombre;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private Long docenteId;
    private String docenteNombreCompleto;
    private LocalDate fechaSustentacion;
    private Boolean asistencia;
    private BigDecimal notaOriginal;
    private BigDecimal notaObtenida;
    private FormulaRecuperacionSiee formulaAplicada;
    private BigDecimal notaFinalResultante;
    private DesempenoNacional desempenoResultante;
    private String estrategiaPedagogica;
    private String evidenciasSoporte;
    private String observaciones;
    private LocalDateTime fechaRegistro;

    public ActaNivelacionResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public Long getCalificacionPeriodoId() {
        return calificacionPeriodoId;
    }

    public void setCalificacionPeriodoId(Long calificacionPeriodoId) {
        this.calificacionPeriodoId = calificacionPeriodoId;
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

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getPeriodoNombre() {
        return periodoNombre;
    }

    public void setPeriodoNombre(String periodoNombre) {
        this.periodoNombre = periodoNombre;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public void setDocenteId(Long docenteId) {
        this.docenteId = docenteId;
    }

    public String getDocenteNombreCompleto() {
        return docenteNombreCompleto;
    }

    public void setDocenteNombreCompleto(String docenteNombreCompleto) {
        this.docenteNombreCompleto = docenteNombreCompleto;
    }

    public LocalDate getFechaSustentacion() {
        return fechaSustentacion;
    }

    public void setFechaSustentacion(LocalDate fechaSustentacion) {
        this.fechaSustentacion = fechaSustentacion;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }

    public BigDecimal getNotaOriginal() {
        return notaOriginal;
    }

    public void setNotaOriginal(BigDecimal notaOriginal) {
        this.notaOriginal = notaOriginal;
    }

    public BigDecimal getNotaObtenida() {
        return notaObtenida;
    }

    public void setNotaObtenida(BigDecimal notaObtenida) {
        this.notaObtenida = notaObtenida;
    }

    public FormulaRecuperacionSiee getFormulaAplicada() {
        return formulaAplicada;
    }

    public void setFormulaAplicada(FormulaRecuperacionSiee formulaAplicada) {
        this.formulaAplicada = formulaAplicada;
    }

    public BigDecimal getNotaFinalResultante() {
        return notaFinalResultante;
    }

    public void setNotaFinalResultante(BigDecimal notaFinalResultante) {
        this.notaFinalResultante = notaFinalResultante;
    }

    public DesempenoNacional getDesempenoResultante() {
        return desempenoResultante;
    }

    public void setDesempenoResultante(DesempenoNacional desempenoResultante) {
        this.desempenoResultante = desempenoResultante;
    }

    public String getEstrategiaPedagogica() {
        return estrategiaPedagogica;
    }

    public void setEstrategiaPedagogica(String estrategiaPedagogica) {
        this.estrategiaPedagogica = estrategiaPedagogica;
    }

    public String getEvidenciasSoporte() {
        return evidenciasSoporte;
    }

    public void setEvidenciasSoporte(String evidenciasSoporte) {
        this.evidenciasSoporte = evidenciasSoporte;
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
