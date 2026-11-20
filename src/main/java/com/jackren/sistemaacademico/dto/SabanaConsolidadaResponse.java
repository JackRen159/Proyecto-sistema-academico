package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.DesempenoNacional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SabanaConsolidadaResponse {

    private Long grupoId;
    private String grupoNombre;
    private Long periodoId;
    private String periodoNombre;
    private String directorGrupoNombre;
    private List<EstudianteSabanaItem> estudiantes = new ArrayList<>();
    private Integer totalEstudiantes;
    private Integer totalEstudiantesEnRiesgo;

    public SabanaConsolidadaResponse() {
    }

    public static class EstudianteSabanaItem {
        private Long matriculaId;
        private String numeroMatricula;
        private Long estudianteId;
        private String estudianteNombreCompleto;
        private List<AreaSabanaItem> areas = new ArrayList<>();
        private BigDecimal promedioGeneral;
        private Integer asignaturasReprobadas;
        private Boolean enRiesgoAcademico;

        public EstudianteSabanaItem() {
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

        public List<AreaSabanaItem> getAreas() {
            return areas;
        }

        public void setAreas(List<AreaSabanaItem> areas) {
            this.areas = areas;
        }

        public BigDecimal getPromedioGeneral() {
            return promedioGeneral;
        }

        public void setPromedioGeneral(BigDecimal promedioGeneral) {
            this.promedioGeneral = promedioGeneral;
        }

        public Integer getAsignaturasReprobadas() {
            return asignaturasReprobadas;
        }

        public void setAsignaturasReprobadas(Integer asignaturasReprobadas) {
            this.asignaturasReprobadas = asignaturasReprobadas;
        }

        public Boolean getEnRiesgoAcademico() {
            return enRiesgoAcademico;
        }

        public void setEnRiesgoAcademico(Boolean enRiesgoAcademico) {
            this.enRiesgoAcademico = enRiesgoAcademico;
        }
    }

    public static class AreaSabanaItem {
        private Long areaId;
        private String areaNombre;
        private BigDecimal notaDefinitiva;
        private DesempenoNacional desempeno;
        private Boolean aprobada;
        private List<AsignaturaSabanaItem> asignaturas = new ArrayList<>();

        public AreaSabanaItem() {
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

        public List<AsignaturaSabanaItem> getAsignaturas() {
            return asignaturas;
        }

        public void setAsignaturas(List<AsignaturaSabanaItem> asignaturas) {
            this.asignaturas = asignaturas;
        }
    }

    public static class AsignaturaSabanaItem {
        private Long asignaturaId;
        private String asignaturaNombre;
        private BigDecimal notaDefinitiva;
        private DesempenoNacional desempeno;
        private Boolean aprobada;
        private BigDecimal notaRecuperacion;
        private BigDecimal notaFinal;

        public AsignaturaSabanaItem() {
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

    public String getDirectorGrupoNombre() {
        return directorGrupoNombre;
    }

    public void setDirectorGrupoNombre(String directorGrupoNombre) {
        this.directorGrupoNombre = directorGrupoNombre;
    }

    public List<EstudianteSabanaItem> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteSabanaItem> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public Integer getTotalEstudiantes() {
        return totalEstudiantes;
    }

    public void setTotalEstudiantes(Integer totalEstudiantes) {
        this.totalEstudiantes = totalEstudiantes;
    }

    public Integer getTotalEstudiantesEnRiesgo() {
        return totalEstudiantesEnRiesgo;
    }

    public void setTotalEstudiantesEnRiesgo(Integer totalEstudiantesEnRiesgo) {
        this.totalEstudiantesEnRiesgo = totalEstudiantesEnRiesgo;
    }
}
