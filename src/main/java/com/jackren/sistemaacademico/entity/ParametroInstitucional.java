package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "parametros_institucionales",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_parametro_clave", columnNames = {"clave"})
    }
)
public class ParametroInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String clave;

    @Column(nullable = false, length = 500)
    private String valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_dato", nullable = false, length = 20)
    private TipoDatoParametro tipoDato = TipoDatoParametro.STRING;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(nullable = false, length = 50)
    private String categoria = "GENERAL";

    @Column(name = "limite_minimo_numerico", precision = 10, scale = 2)
    private BigDecimal limiteMinimoNumerico;

    @Column(name = "limite_maximo_numerico", precision = 10, scale = 2)
    private BigDecimal limiteMaximoNumerico;

    @Column(name = "editable_por_usuario", nullable = false)
    private Boolean editablePorUsuario = true;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public ParametroInstitucional() {
    }

    public ParametroInstitucional(String clave, String valor, TipoDatoParametro tipoDato, String descripcion, String categoria) {
        this.clave = clave;
        this.valor = valor;
        this.tipoDato = tipoDato != null ? tipoDato : TipoDatoParametro.STRING;
        this.descripcion = descripcion;
        this.categoria = categoria != null ? categoria : "GENERAL";
        this.editablePorUsuario = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public TipoDatoParametro getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(TipoDatoParametro tipoDato) {
        this.tipoDato = tipoDato;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getLimiteMinimoNumerico() {
        return limiteMinimoNumerico;
    }

    public void setLimiteMinimoNumerico(BigDecimal limiteMinimoNumerico) {
        this.limiteMinimoNumerico = limiteMinimoNumerico;
    }

    public BigDecimal getLimiteMaximoNumerico() {
        return limiteMaximoNumerico;
    }

    public void setLimiteMaximoNumerico(BigDecimal limiteMaximoNumerico) {
        this.limiteMaximoNumerico = limiteMaximoNumerico;
    }

    public Boolean getEditablePorUsuario() {
        return editablePorUsuario;
    }

    public void setEditablePorUsuario(Boolean editablePorUsuario) {
        this.editablePorUsuario = editablePorUsuario;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
