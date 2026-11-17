package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoDocumentoAcademico;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ConfiguracionConsecutivoRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotNull(message = "El tipo de documento es obligatorio")
    private TipoDocumentoAcademico tipoDocumento;

    @Size(max = 20, message = "El prefijo no puede superar 20 caracteres")
    private String prefijo = "";

    @Min(value = 0, message = "El último número no puede ser negativo")
    private Long ultimoNumero = 0L;

    @Min(value = 1, message = "La longitud de relleno debe ser al menos 1")
    @Max(value = 10, message = "La longitud de relleno no puede superar 10")
    private Integer longitudRelleno = 5;

    @Size(max = 500, message = "El texto de pie de página no puede superar 500 caracteres")
    private String textoPiePagina;

    public ConfiguracionConsecutivoRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
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
}
