package com.jackren.sistemaacademico.dto;

import java.util.List;
import java.util.Map;

public class DiferenciaVersionResponse {

    private Integer versionA;
    private Integer versionB;
    private String tipoDocumento;
    private List<CampoDiferencia> diferencias;

    public DiferenciaVersionResponse() {
    }

    public DiferenciaVersionResponse(Integer versionA, Integer versionB, String tipoDocumento, List<CampoDiferencia> diferencias) {
        this.versionA = versionA;
        this.versionB = versionB;
        this.tipoDocumento = tipoDocumento;
        this.diferencias = diferencias;
    }

    public Integer getVersionA() {
        return versionA;
    }

    public void setVersionA(Integer versionA) {
        this.versionA = versionA;
    }

    public Integer getVersionB() {
        return versionB;
    }

    public void setVersionB(Integer versionB) {
        this.versionB = versionB;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public List<CampoDiferencia> getDiferencias() {
        return diferencias;
    }

    public void setDiferencias(List<CampoDiferencia> diferencias) {
        this.diferencias = diferencias;
    }

    public static class CampoDiferencia {
        private String campo;
        private Object valorVersionA;
        private Object valorVersionB;

        public CampoDiferencia() {
        }

        public CampoDiferencia(String campo, Object valorVersionA, Object valorVersionB) {
            this.campo = campo;
            this.valorVersionA = valorVersionA;
            this.valorVersionB = valorVersionB;
        }

        public String getCampo() {
            return campo;
        }

        public void setCampo(String campo) {
            this.campo = campo;
        }

        public Object getValorVersionA() {
            return valorVersionA;
        }

        public void setValorVersionA(Object valorVersionA) {
            this.valorVersionA = valorVersionA;
        }

        public Object getValorVersionB() {
            return valorVersionB;
        }

        public void setValorVersionB(Object valorVersionB) {
            this.valorVersionB = valorVersionB;
        }
    }
}
