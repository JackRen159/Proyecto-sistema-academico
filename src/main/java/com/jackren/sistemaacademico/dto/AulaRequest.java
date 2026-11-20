package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoAula;
import com.jackren.sistemaacademico.entity.TipoEspacioFisico;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AulaRequest {

    @NotBlank(message = "La sede es obligatoria")
    @Size(max = 50, message = "La sede no puede superar 50 caracteres")
    private String sede = "SEDE_PRINCIPAL";

    @NotBlank(message = "El código del aula es obligatorio")
    @Size(max = 20, message = "El código no puede superar 20 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del aula es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @NotNull(message = "El tipo de espacio físico es obligatorio")
    private TipoEspacioFisico tipoEspacio = TipoEspacioFisico.AULA_ESTANDAR;

    @NotNull(message = "La capacidad máxima es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es de 1 estudiante")
    private Integer capacidadMaxima = 40;

    private Boolean esMultigrupo = false;

    private EstadoAula estado = EstadoAula.DISPONIBLE;

    @Size(max = 255, message = "Las observaciones no pueden superar 255 caracteres")
    private String observaciones;

    public AulaRequest() {
    }

    public String getSede() {
        return sede;
    }

    public void setSede(String sede) {
        this.sede = sede;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoEspacioFisico getTipoEspacio() {
        return tipoEspacio;
    }

    public void setTipoEspacio(TipoEspacioFisico tipoEspacio) {
        this.tipoEspacio = tipoEspacio;
    }

    public Integer getCapacidadMaxima() {
        return capacidadMaxima;
    }

    public void setCapacidadMaxima(Integer capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
    }

    public Boolean getEsMultigrupo() {
        return esMultigrupo;
    }

    public void setEsMultigrupo(Boolean esMultigrupo) {
        this.esMultigrupo = esMultigrupo;
    }

    public EstadoAula getEstado() {
        return estado;
    }

    public void setEstado(EstadoAula estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
