package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoTitulacionGraduando;
import com.jackren.sistemaacademico.entity.ModalidadTituloBachiller;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TitulacionGraduandoResponse {

    private Long id;
    private Long matriculaId;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private ModalidadTituloBachiller modalidadTitulo;
    private String especialidad;
    private Integer horasServicioSocial;
    private String soporteServicioSocialUrl;
    private String codigoRegistroIcfes;
    private Boolean documentoIdentidadVerificado;
    private EstadoTitulacionGraduando estadoTitulacion;
    private String numeroActaGeneral;
    private String numeroActaIndividual;
    private String numeroDiploma;
    private String numeroLibro;
    private String numeroFolio;
    private LocalDate fechaGraduacion;
    private String usuarioAutorizadorNombre;
    private LocalDateTime fechaRegistro;

    public TitulacionGraduandoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMatriculaId() {
        return matriculaId;
    }

    public void setMatriculaId(Long matriculaId) {
        this.matriculaId = matriculaId;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
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

    public EstadoTitulacionGraduando getEstadoTitulacion() {
        return estadoTitulacion;
    }

    public void setEstadoTitulacion(EstadoTitulacionGraduando estadoTitulacion) {
        this.estadoTitulacion = estadoTitulacion;
    }

    public String getNumeroActaGeneral() {
        return numeroActaGeneral;
    }

    public void setNumeroActaGeneral(String numeroActaGeneral) {
        this.numeroActaGeneral = numeroActaGeneral;
    }

    public String getNumeroActaIndividual() {
        return numeroActaIndividual;
    }

    public void setNumeroActaIndividual(String numeroActaIndividual) {
        this.numeroActaIndividual = numeroActaIndividual;
    }

    public String getNumeroDiploma() {
        return numeroDiploma;
    }

    public void setNumeroDiploma(String numeroDiploma) {
        this.numeroDiploma = numeroDiploma;
    }

    public String getNumeroLibro() {
        return numeroLibro;
    }

    public void setNumeroLibro(String numeroLibro) {
        this.numeroLibro = numeroLibro;
    }

    public String getNumeroFolio() {
        return numeroFolio;
    }

    public void setNumeroFolio(String numeroFolio) {
        this.numeroFolio = numeroFolio;
    }

    public LocalDate getFechaGraduacion() {
        return fechaGraduacion;
    }

    public void setFechaGraduacion(LocalDate fechaGraduacion) {
        this.fechaGraduacion = fechaGraduacion;
    }

    public String getUsuarioAutorizadorNombre() {
        return usuarioAutorizadorNombre;
    }

    public void setUsuarioAutorizadorNombre(String usuarioAutorizadorNombre) {
        this.usuarioAutorizadorNombre = usuarioAutorizadorNombre;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
