package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoHorarioVersion;
import com.jackren.sistemaacademico.entity.Jornada;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ConsultaHorarioEstudianteResponse {

    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private Long grupoId;
    private String grupoNombre;
    private String sede;
    private Jornada jornada;
    private Integer numeroVersion;
    private EstadoHorarioVersion estado;
    private LocalDate fechaInicioVigencia;
    private List<HorarioBloqueResponse> bloques = new ArrayList<>();
    private List<DiaNoLectivoResponse> diasNoLectivosSemana = new ArrayList<>();

    public ConsultaHorarioEstudianteResponse() {
    }

    public static class DiaNoLectivoResponse {
        private LocalDate fecha;
        private String motivo;

        public DiaNoLectivoResponse() {
        }

        public DiaNoLectivoResponse(LocalDate fecha, String motivo) {
            this.fecha = fecha;
            this.motivo = motivo;
        }

        public LocalDate getFecha() {
            return fecha;
        }

        public void setFecha(LocalDate fecha) {
            this.fecha = fecha;
        }

        public String getMotivo() {
            return motivo;
        }

        public void setMotivo(String motivo) {
            this.motivo = motivo;
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

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoHorarioVersion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorarioVersion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public List<HorarioBloqueResponse> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioBloqueResponse> bloques) {
        this.bloques = bloques;
    }

    public List<DiaNoLectivoResponse> getDiasNoLectivosSemana() {
        return diasNoLectivosSemana;
    }

    public void setDiasNoLectivosSemana(List<DiaNoLectivoResponse> diasNoLectivosSemana) {
        this.diasNoLectivosSemana = diasNoLectivosSemana;
    }
}
