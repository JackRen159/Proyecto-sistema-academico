package com.jackren.sistemaacademico.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidacionHorarioResponse {

    private Boolean valido;
    private List<String> erroresColision = new ArrayList<>();
    private List<DetalleCumplimientoIhs> cumplimientoIhs = new ArrayList<>();

    public ValidacionHorarioResponse() {
    }

    public ValidacionHorarioResponse(Boolean valido) {
        this.valido = valido;
    }

    public static class DetalleCumplimientoIhs {
        private Long asignaturaId;
        private String asignaturaNombre;
        private Integer horasRequeridasIhs;
        private Long horasProgramadas;
        private Boolean cumple;

        public DetalleCumplimientoIhs() {
        }

        public DetalleCumplimientoIhs(Long asignaturaId, String asignaturaNombre, Integer horasRequeridasIhs, Long horasProgramadas) {
            this.asignaturaId = asignaturaId;
            this.asignaturaNombre = asignaturaNombre;
            this.horasRequeridasIhs = horasRequeridasIhs;
            this.horasProgramadas = horasProgramadas;
            this.cumple = horasRequeridasIhs != null && horasRequeridasIhs.longValue() == horasProgramadas;
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

        public Integer getHorasRequeridasIhs() {
            return horasRequeridasIhs;
        }

        public void setHorasRequeridasIhs(Integer horasRequeridasIhs) {
            this.horasRequeridasIhs = horasRequeridasIhs;
        }

        public Long getHorasProgramadas() {
            return horasProgramadas;
        }

        public void setHorasProgramadas(Long horasProgramadas) {
            this.horasProgramadas = horasProgramadas;
        }

        public Boolean getCumple() {
            return cumple;
        }

        public void setCumple(Boolean cumple) {
            this.cumple = cumple;
        }
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public List<String> getErroresColision() {
        return erroresColision;
    }

    public void setErroresColision(List<String> erroresColision) {
        this.erroresColision = erroresColision;
    }

    public List<DetalleCumplimientoIhs> getCumplimientoIhs() {
        return cumplimientoIhs;
    }

    public void setCumplimientoIhs(List<DetalleCumplimientoIhs> cumplimientoIhs) {
        this.cumplimientoIhs = cumplimientoIhs;
    }
}
