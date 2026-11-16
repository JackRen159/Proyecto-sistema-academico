package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;

public class ResumenAsistenciaEstudianteResponse {

    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private Long grupoId;
    private String grupoNombre;
    private Long asignaturaId;
    private String asignaturaNombre;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private long totalSesiones;
    private long totalPresente;
    private long totalAusenciasInjustificadas;
    private long totalAusenciasJustificadas;
    private long totalRetardos;
    private long totalRetirosAnticipados;
    private BigDecimal porcentajeAsistencia;
    private BigDecimal porcentajeInasistencia;
    private String nivelAlerta; // NINGUNA, AMARILLA, ROJA

    public ResumenAsistenciaEstudianteResponse() {
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

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
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

    public long getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(long totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public long getTotalPresente() {
        return totalPresente;
    }

    public void setTotalPresente(long totalPresente) {
        this.totalPresente = totalPresente;
    }

    public long getTotalAusenciasInjustificadas() {
        return totalAusenciasInjustificadas;
    }

    public void setTotalAusenciasInjustificadas(long totalAusenciasInjustificadas) {
        this.totalAusenciasInjustificadas = totalAusenciasInjustificadas;
    }

    public long getTotalAusenciasJustificadas() {
        return totalAusenciasJustificadas;
    }

    public void setTotalAusenciasJustificadas(long totalAusenciasJustificadas) {
        this.totalAusenciasJustificadas = totalAusenciasJustificadas;
    }

    public long getTotalRetardos() {
        return totalRetardos;
    }

    public void setTotalRetardos(long totalRetardos) {
        this.totalRetardos = totalRetardos;
    }

    public long getTotalRetirosAnticipados() {
        return totalRetirosAnticipados;
    }

    public void setTotalRetirosAnticipados(long totalRetirosAnticipados) {
        this.totalRetirosAnticipados = totalRetirosAnticipados;
    }

    public BigDecimal getPorcentajeAsistencia() {
        return porcentajeAsistencia;
    }

    public void setPorcentajeAsistencia(BigDecimal porcentajeAsistencia) {
        this.porcentajeAsistencia = porcentajeAsistencia;
    }

    public BigDecimal getPorcentajeInasistencia() {
        return porcentajeInasistencia;
    }

    public void setPorcentajeInasistencia(BigDecimal porcentajeInasistencia) {
        this.porcentajeInasistencia = porcentajeInasistencia;
    }

    public String getNivelAlerta() {
        return nivelAlerta;
    }

    public void setNivelAlerta(String nivelAlerta) {
        this.nivelAlerta = nivelAlerta;
    }
}
