package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ConsultaCalificacionesEstudianteResponse {

    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Long periodoAcademicoId;
    private String periodoNombre;
    private Integer periodoNumero;
    private String estadoPeriodo;
    private List<AreaConsultaItem> areas = new ArrayList<>();
    private List<ActaNivelacionResponse> nivelaciones = new ArrayList<>();
    private List<ConvalidacionDetalleResponse> convalidaciones = new ArrayList<>();

    public ConsultaCalificacionesEstudianteResponse() {
    }

    public static class AreaConsultaItem {
        private Long areaId;
        private String areaNombre;
        private BigDecimal notaDefinitiva;
        private DesempenoNacional desempeno;
        private Boolean aprobada;
        private List<AsignaturaConsultaItem> asignaturas = new ArrayList<>();

        public AreaConsultaItem() {
        }

        public Long getAreaId() {
            return areaId;
        }

        public void setAreaId(Long areaId) {
            this.areaId = areaId;
        }

        public String getAreaNombre() {
            return areaNombre;
        }

        public void setAreaNombre(String areaNombre) {
            this.areaNombre = areaNombre;
        }

        public BigDecimal getNotaDefinitiva() {
            return notaDefinitiva;
        }

        public void setNotaDefinitiva(BigDecimal notaDefinitiva) {
            this.notaDefinitiva = notaDefinitiva;
        }

        public DesempenoNacional getDesempeno() {
            return desempeno;
        }

        public void setDesempeno(DesempenoNacional desempeno) {
            this.desempeno = desempeno;
        }

        public Boolean getAprobada() {
            return aprobada;
        }

        public void setAprobada(Boolean aprobada) {
            this.aprobada = aprobada;
        }

        public List<AsignaturaConsultaItem> getAsignaturas() {
            return asignaturas;
        }

        public void setAsignaturas(List<AsignaturaConsultaItem> asignaturas) {
            this.asignaturas = asignaturas;
        }
    }

    public static class AsignaturaConsultaItem {
        private Long asignaturaId;
        private String asignaturaNombre;
        private String profesorNombreCompleto;
        private BigDecimal notaDefinitiva;
        private DesempenoNacional desempeno;
        private Boolean aprobada;
        private BigDecimal notaRecuperacion;
        private BigDecimal notaFinal;
        private List<CalificacionActividadResponse> actividades = new ArrayList<>();

        public AsignaturaConsultaItem() {
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

        public String getProfesorNombreCompleto() {
            return profesorNombreCompleto;
        }

        public void setProfesorNombreCompleto(String profesorNombreCompleto) {
            this.profesorNombreCompleto = profesorNombreCompleto;
        }

        public BigDecimal getNotaDefinitiva() {
            return notaDefinitiva;
        }

        public void setNotaDefinitiva(BigDecimal notaDefinitiva) {
            this.notaDefinitiva = notaDefinitiva;
        }

        public DesempenoNacional getDesempeno() {
            return desempeno;
        }

        public void setDesempeno(DesempenoNacional desempeno) {
            this.desempeno = desempeno;
        }

        public Boolean getAprobada() {
            return aprobada;
        }

        public void setAprobada(Boolean aprobada) {
            this.aprobada = aprobada;
        }

        public BigDecimal getNotaRecuperacion() {
            return notaRecuperacion;
        }

        public void setNotaRecuperacion(BigDecimal notaRecuperacion) {
            this.notaRecuperacion = notaRecuperacion;
        }

        public BigDecimal getNotaFinal() {
            return notaFinal;
        }

        public void setNotaFinal(BigDecimal notaFinal) {
            this.notaFinal = notaFinal;
        }

        public List<CalificacionActividadResponse> getActividades() {
            return actividades;
        }

        public void setActividades(List<CalificacionActividadResponse> actividades) {
            this.actividades = actividades;
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

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
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

    public Integer getPeriodoNumero() {
        return periodoNumero;
    }

    public void setPeriodoNumero(Integer periodoNumero) {
        this.periodoNumero = periodoNumero;
    }

    public String getEstadoPeriodo() {
        return estadoPeriodo;
    }

    public void setEstadoPeriodo(String estadoPeriodo) {
        this.estadoPeriodo = estadoPeriodo;
    }

    public List<AreaConsultaItem> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaConsultaItem> areas) {
        this.areas = areas;
    }

    public List<ActaNivelacionResponse> getNivelaciones() {
        return nivelaciones;
    }

    public void setNivelaciones(List<ActaNivelacionResponse> nivelaciones) {
        this.nivelaciones = nivelaciones;
    }

    public List<ConvalidacionDetalleResponse> getConvalidaciones() {
        return convalidaciones;
    }

    public void setConvalidaciones(List<ConvalidacionDetalleResponse> convalidaciones) {
        this.convalidaciones = convalidaciones;
    }
}
