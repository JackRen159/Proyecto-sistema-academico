package com.jackren.sistemaacademico.dto;

import java.math.BigDecimal;
import java.util.List;

public class SimulacionSieeResponse {

    private BigDecimal notaPromedio;
    private BigDecimal notaFinal;
    private String desempenoNacional;
    private Boolean aprobado;
    private Integer decimales;
    private String modoRedondeo;
    private List<String> detallesCalculo;

    public SimulacionSieeResponse() {
    }

    public BigDecimal getNotaPromedio() {
        return notaPromedio;
    }

    public void setNotaPromedio(BigDecimal notaPromedio) {
        this.notaPromedio = notaPromedio;
    }

    public BigDecimal getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(BigDecimal notaFinal) {
        this.notaFinal = notaFinal;
    }

    public String getDesempenoNacional() {
        return desempenoNacional;
    }

    public void setDesempenoNacional(String desempenoNacional) {
        this.desempenoNacional = desempenoNacional;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public Integer getDecimales() {
        return decimales;
    }

    public void setDecimales(Integer decimales) {
        this.decimales = decimales;
    }

    public String getModoRedondeo() {
        return modoRedondeo;
    }

    public void setModoRedondeo(String modoRedondeo) {
        this.modoRedondeo = modoRedondeo;
    }

    public List<String> getDetallesCalculo() {
        return detallesCalculo;
    }

    public void setDetallesCalculo(List<String> detallesCalculo) {
        this.detallesCalculo = detallesCalculo;
    }
}
