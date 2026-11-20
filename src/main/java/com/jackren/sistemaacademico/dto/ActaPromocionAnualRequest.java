package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActaPromocion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public class ActaPromocionAnualRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "El grado es obligatorio")
    private Long gradoId;

    private Long grupoId;

    @NotBlank(message = "El número de acta es obligatorio")
    @Size(max = 50, message = "El número de acta no puede superar 50 caracteres")
    private String numeroActa;

    private TipoActaPromocion tipoActa = TipoActaPromocion.ORDINARIA_COMISION;

    private LocalDate fechaActa;

    @NotEmpty(message = "Debe registrar al menos un integrante nominativo")
    private List<String> integrantesNominativos;

    private String soporteActaUrl;

    private String observaciones;

    @NotEmpty(message = "Debe incluir las decisiones de promoción de los estudiantes")
    @Valid
    private List<DecisionPromocionEstudianteItemRequest> decisionesEstudiantes;

    public ActaPromocionAnualRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public TipoActaPromocion getTipoActa() {
        return tipoActa;
    }

    public void setTipoActa(TipoActaPromocion tipoActa) {
        this.tipoActa = tipoActa;
    }

    public LocalDate getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(LocalDate fechaActa) {
        this.fechaActa = fechaActa;
    }

    public List<String> getIntegrantesNominativos() {
        return integrantesNominativos;
    }

    public void setIntegrantesNominativos(List<String> integrantesNominativos) {
        this.integrantesNominativos = integrantesNominativos;
    }

    public String getSoporteActaUrl() {
        return soporteActaUrl;
    }

    public void setSoporteActaUrl(String soporteActaUrl) {
        this.soporteActaUrl = soporteActaUrl;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public List<DecisionPromocionEstudianteItemRequest> getDecisionesEstudiantes() {
        return decisionesEstudiantes;
    }

    public void setDecisionesEstudiantes(List<DecisionPromocionEstudianteItemRequest> decisionesEstudiantes) {
        this.decisionesEstudiantes = decisionesEstudiantes;
    }
}
