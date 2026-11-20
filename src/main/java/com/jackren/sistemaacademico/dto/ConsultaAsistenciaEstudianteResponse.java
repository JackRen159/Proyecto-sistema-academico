package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAsistencia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultaAsistenciaEstudianteResponse {

    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private Long totalSesiones;
    private Long totalPresente;
    private Long totalAusenciasInjustificadas;
    private Long totalAusenciasJustificadas;
    private Long totalRetardos;
    private Long totalEvasiones;
    private BigDecimal porcentajeAsistencia;
    private BigDecimal porcentajeInasistencia;
    private String nivelAlerta;
    private List<SesionAsistenciaItem> sesiones = new ArrayList<>();
    private List<SolicitudExcusaAsistenciaResponse> excusas = new ArrayList<>();

    public ConsultaAsistenciaEstudianteResponse() {
    }

    public static class SesionAsistenciaItem {
        private Long sesionId;
        private LocalDate fecha;
        private Integer bloqueHorario;
        private String asignaturaNombre;
        private EstadoAsistencia estado;
        private Integer minutosRetardo;
        private String observaciones;

        public SesionAsistenciaItem() {
        }

        public Long getSesionId() {
            return sesionId;
        }

        public void setSesionId(Long sesionId) {
            this.sesionId = sesionId;
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

        public String getAsignaturaNombre() {
            return asignaturaNombre;
        }

        public void setAsignaturaNombre(String asignaturaNombre) {
            this.asignaturaNombre = asignaturaNombre;
        }

        public EstadoAsistencia getEstado() {
            return estado;
        }

        public void setEstado(EstadoAsistencia estado) {
            this.estado = estado;
        }

        public Integer getMinutosRetardo() {
            return minutosRetardo;
        }

        public void setMinutosRetardo(Integer minutosRetardo) {
            this.minutosRetardo = minutosRetardo;
        }

        public String getObservaciones() {
            return observaciones;
        }

        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }
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

    public Long getTotalSesiones() {
        return totalSesiones;
    }

    public void setTotalSesiones(Long totalSesiones) {
        this.totalSesiones = totalSesiones;
    }

    public Long getTotalPresente() {
        return totalPresente;
    }

    public void setTotalPresente(Long totalPresente) {
        this.totalPresente = totalPresente;
    }

    public Long getTotalAusenciasInjustificadas() {
        return totalAusenciasInjustificadas;
    }

    public void setTotalAusenciasInjustificadas(Long totalAusenciasInjustificadas) {
        this.totalAusenciasInjustificadas = totalAusenciasInjustificadas;
    }

    public Long getTotalAusenciasJustificadas() {
        return totalAusenciasJustificadas;
    }

    public void setTotalAusenciasJustificadas(Long totalAusenciasJustificadas) {
        this.totalAusenciasJustificadas = totalAusenciasJustificadas;
    }

    public Long getTotalRetardos() {
        return totalRetardos;
    }

    public void setTotalRetardos(Long totalRetardos) {
        this.totalRetardos = totalRetardos;
    }

    public Long getTotalEvasiones() {
        return totalEvasiones;
    }

    public void setTotalEvasiones(Long totalEvasiones) {
        this.totalEvasiones = totalEvasiones;
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

    public List<SesionAsistenciaItem> getSesiones() {
        return sesiones;
    }

    public void setSesiones(List<SesionAsistenciaItem> sesiones) {
        this.sesiones = sesiones;
    }

    public List<SolicitudExcusaAsistenciaResponse> getExcusas() {
        return excusas;
    }

    public void setExcusas(List<SolicitudExcusaAsistenciaResponse> excusas) {
        this.excusas = excusas;
    }
}
