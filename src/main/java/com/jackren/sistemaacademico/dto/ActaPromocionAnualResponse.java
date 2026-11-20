package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoActaPromocion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ActaPromocionAnualResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private Long gradoId;
    private String gradoNombre;
    private Long grupoId;
    private String grupoNombre;
    private String numeroActa;
    private TipoActaPromocion tipoActa;
    private LocalDate fechaActa;
    private String integrantesNominativosJson;
    private String soporteActaUrl;
    private String observaciones;
    private String usuarioRegistroNombre;
    private LocalDateTime fechaRegistro;
    private List<PromocionEstudianteResponse> decisiones = new ArrayList<>();

    public ActaPromocionAnualResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
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

    public String getIntegrantesNominativosJson() {
        return integrantesNominativosJson;
    }

    public void setIntegrantesNominativosJson(String integrantesNominativosJson) {
        this.integrantesNominativosJson = integrantesNominativosJson;
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

    public String getUsuarioRegistroNombre() {
        return usuarioRegistroNombre;
    }

    public void setUsuarioRegistroNombre(String usuarioRegistroNombre) {
        this.usuarioRegistroNombre = usuarioRegistroNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public List<PromocionEstudianteResponse> getDecisiones() {
        return decisiones;
    }

    public void setDecisiones(List<PromocionEstudianteResponse> decisiones) {
        this.decisiones = decisiones;
    }
}
