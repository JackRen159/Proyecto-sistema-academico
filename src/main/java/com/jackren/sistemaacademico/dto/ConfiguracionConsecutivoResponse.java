package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;

import java.time.LocalDateTime;

public class ConfiguracionConsecutivoResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private TipoDocumentoAcademico tipoDocumento;
    private String prefijo;
    private Long ultimoNumero;
    private Integer longitudRelleno;
    private String textoPiePagina;
    private LocalDateTime fechaActualizacion;

    public ConfiguracionConsecutivoResponse() {
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

    public TipoDocumentoAcademico getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoAcademico tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    public Long getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(Long ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }

    public Integer getLongitudRelleno() {
        return longitudRelleno;
    }

    public void setLongitudRelleno(Integer longitudRelleno) {
        this.longitudRelleno = longitudRelleno;
    }

    public String getTextoPiePagina() {
        return textoPiePagina;
    }

    public void setTextoPiePagina(String textoPiePagina) {
        this.textoPiePagina = textoPiePagina;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
