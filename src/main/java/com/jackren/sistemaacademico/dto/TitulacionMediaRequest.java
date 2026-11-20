package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.ModalidadTituloBachiller;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TitulacionMediaRequest {

    @NotNull(message = "La matrícula es obligatoria")
    private Long matriculaId;

    private ModalidadTituloBachiller modalidadTitulo = ModalidadTituloBachiller.BACHILLER_ACADEMICO;

    @Size(max = 100, message = "La especialidad no puede superar 100 caracteres")
    private String especialidad;

    @NotNull(message = "Las horas de servicio social son obligatorias")
    @Min(value = 0, message = "Las horas no pueden ser negativas")
    private Integer horasServicioSocial = 0;

    @Size(max = 255, message = "La URL del soporte no puede superar 255 caracteres")
    private String soporteServicioSocialUrl;

    @Size(max = 50, message = "El código ICFES no puede superar 50 caracteres")
    private String codigoRegistroIcfes;

    private Boolean documentoIdentidadVerificado = true;

    public TitulacionMediaRequest() {
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public ModalidadTituloBachiller getModalidadTitulo() {
        return modalidadTitulo;
    }

    public void setModalidadTitulo(ModalidadTituloBachiller modalidadTitulo) {
        this.modalidadTitulo = modalidadTitulo;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public Integer getHorasServicioSocial() {
        return horasServicioSocial;
    }

    public void setHorasServicioSocial(Integer horasServicioSocial) {
        this.horasServicioSocial = horasServicioSocial;
    }

    public String getSoporteServicioSocialUrl() {
        return soporteServicioSocialUrl;
    }

    public void setSoporteServicioSocialUrl(String soporteServicioSocialUrl) {
        this.soporteServicioSocialUrl = soporteServicioSocialUrl;
    }

    public String getCodigoRegistroIcfes() {
        return codigoRegistroIcfes;
    }

    public void setCodigoRegistroIcfes(String codigoRegistroIcfes) {
        this.codigoRegistroIcfes = codigoRegistroIcfes;
    }

    public Boolean getDocumentoIdentidadVerificado() {
        return documentoIdentidadVerificado;
    }

    public void setDocumentoIdentidadVerificado(Boolean documentoIdentidadVerificado) {
        this.documentoIdentidadVerificado = documentoIdentidadVerificado;
    }
}
