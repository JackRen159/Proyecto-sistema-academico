package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SabanaAsistenciaGrupalResponse {

    private Long grupoId;
    private String grupoNombre;
    private Long periodoId;
    private String periodoNombre;
    private String directorGrupoNombre;
    private List<EstudianteAsistenciaSabanaItem> estudiantes = new ArrayList<>();
    private Integer totalEstudiantes;
    private Integer totalEstudiantesAlertaRoja;
    private Integer totalEstudiantesAlertaAmarilla;

    public SabanaAsistenciaGrupalResponse() {
    }

    public static class EstudianteAsistenciaSabanaItem {
        private Long matriculaId;
        private String numeroMatricula;
        private Long estudianteId;
        private String estudianteNombreCompleto;
        private Long totalSesiones;
        private Long totalPresente;
        private Long totalAusenciasInjustificadas;
        private Long totalAusenciasJustificadas;
        private Long totalRetardos;
        private Long totalEvasiones;
        private BigDecimal porcentajeInasistencia;
        private String nivelAlerta;

        public EstudianteAsistenciaSabanaItem() {
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

    public List<EstudianteAsistenciaSabanaItem> getEstudiantes() {
        return estudiantes;
    }

    public void setEstudiantes(List<EstudianteAsistenciaSabanaItem> estudiantes) {
        this.estudiantes = estudiantes;
    }

    public Integer getTotalEstudiantes() {
        return totalEstudiantes;
    }

    public void setTotalEstudiantes(Integer totalEstudiantes) {
        this.totalEstudiantes = totalEstudiantes;
    }

    public Integer getTotalEstudiantesAlertaRoja() {
        return totalEstudiantesAlertaRoja;
    }

    public void setTotalEstudiantesAlertaRoja(Integer totalEstudiantesAlertaRoja) {
        this.totalEstudiantesAlertaRoja = totalEstudiantesAlertaRoja;
    }

    public Integer getTotalEstudiantesAlertaAmarilla() {
        return totalEstudiantesAlertaAmarilla;
    }

    public void setTotalEstudiantesAlertaAmarilla(Integer totalEstudiantesAlertaAmarilla) {
        this.totalEstudiantesAlertaAmarilla = totalEstudiantesAlertaAmarilla;
    }
}
