package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.CategoriaEvaluativa;
import com.jackren.sistemaacademico.entity.EstadoActividadEvaluativa;
import com.jackren.sistemaacademico.entity.ModalidadEvaluativa;
import com.jackren.sistemaacademico.entity.TipoComponenteEvaluativo;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ActividadEvaluativaRequest {

    @NotNull(message = "La asignación académica es obligatoria")
    private Long asignacionAcademicaId;

    @NotNull(message = "El periodo académico es obligatorio")
    private Long periodoAcademicoId;

    @NotBlank(message = "El nombre de la actividad evaluativa es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar 255 caracteres")
    private String descripcion;

    @NotNull(message = "El porcentaje es obligatorio")
    @DecimalMin(value = "0.01", message = "El porcentaje debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "El porcentaje no puede superar 100%")
    private BigDecimal porcentaje;

    private LocalDate fechaEvaluacion;

    private CategoriaEvaluativa categoriaEvaluativa = CategoriaEvaluativa.COGNITIVA;

    private TipoComponenteEvaluativo tipoComponente = TipoComponenteEvaluativo.TALLER;

    private ModalidadEvaluativa modalidad = ModalidadEvaluativa.HETEROEVALUACION;

    private EstadoActividadEvaluativa estado = EstadoActividadEvaluativa.PUBLICADA;

    private Boolean esAdaptacionPiar = false;

    private String criteriosAdaptadosPiar;

    public ActividadEvaluativaRequest() {
    }

    public Long getAsignacionAcademicaId() {
        return asignacionAcademicaId;
    }

    public void setAsignacionAcademicaId(Long asignacionAcademicaId) {
        this.asignacionAcademicaId = asignacionAcademicaId;
    }

    public Long getPeriodoAcademicoId() {
        return periodoAcademicoId;
    }

    public void setPeriodoAcademicoId(Long periodoAcademicoId) {
        this.periodoAcademicoId = periodoAcademicoId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(BigDecimal porcentaje) {
        this.porcentaje = porcentaje;
    }

    public LocalDate getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    public void setFechaEvaluacion(LocalDate fechaEvaluacion) {
        this.fechaEvaluacion = fechaEvaluacion;
    }

    public CategoriaEvaluativa getCategoriaEvaluativa() {
        return categoriaEvaluativa;
    }

    public void setCategoriaEvaluativa(CategoriaEvaluativa categoriaEvaluativa) {
        this.categoriaEvaluativa = categoriaEvaluativa;
    }

    public TipoComponenteEvaluativo getTipoComponente() {
        return tipoComponente;
    }

    public void setTipoComponente(TipoComponenteEvaluativo tipoComponente) {
        this.tipoComponente = tipoComponente;
    }

    public ModalidadEvaluativa getModalidad() {
        return modalidad;
    }

    public void setModalidad(ModalidadEvaluativa modalidad) {
        this.modalidad = modalidad;
    }

    public EstadoActividadEvaluativa getEstado() {
        return estado;
    }

    public void setEstado(EstadoActividadEvaluativa estado) {
        this.estado = estado;
    }

    public Boolean getEsAdaptacionPiar() {
        return esAdaptacionPiar;
    }

    public void setEsAdaptacionPiar(Boolean esAdaptacionPiar) {
        this.esAdaptacionPiar = esAdaptacionPiar;
    }

    public String getCriteriosAdaptadosPiar() {
        return criteriosAdaptadosPiar;
    }

    public void setCriteriosAdaptadosPiar(String criteriosAdaptadosPiar) {
        this.criteriosAdaptadosPiar = criteriosAdaptadosPiar;
    }
}
