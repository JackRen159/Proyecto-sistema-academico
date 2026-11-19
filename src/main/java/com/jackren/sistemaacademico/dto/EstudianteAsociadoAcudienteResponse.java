package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoVinculo;
import com.jackren.sistemaacademico.entity.Parentesco;
import com.jackren.sistemaacademico.entity.TipoDocumento;

public class EstudianteAsociadoAcudienteResponse {

    private Long estudianteId;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String gradoActual;
    private String grupoActual;
    private String jornada;
    private String sede;
    private String estadoMatricula;
    private Parentesco parentesco;
    private Boolean esPrincipal;
    private EstadoVinculo estadoVinculo;
    private Boolean autorizadoRetiro;

    public EstudianteAsociadoAcudienteResponse() {
    }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(TipoDocumento tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getGradoActual() { return gradoActual; }
    public void setGradoActual(String gradoActual) { this.gradoActual = gradoActual; }
    public String getGrupoActual() { return grupoActual; }
    public void setGrupoActual(String grupoActual) { this.grupoActual = grupoActual; }
    public String getJornada() { return jornada; }
    public void setJornada(String jornada) { this.jornada = jornada; }
    public String getSede() { return sede; }
    public void setSede(String sede) { this.sede = sede; }
    public String getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(String estadoMatricula) { this.estadoMatricula = estadoMatricula; }
    public Parentesco getParentesco() { return parentesco; }
    public void setParentesco(Parentesco parentesco) { this.parentesco = parentesco; }
    public Boolean getEsPrincipal() { return esPrincipal; }
    public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
    public EstadoVinculo getEstadoVinculo() { return estadoVinculo; }
    public void setEstadoVinculo(EstadoVinculo estadoVinculo) { this.estadoVinculo = estadoVinculo; }
    public Boolean getAutorizadoRetiro() { return autorizadoRetiro; }
    public void setAutorizadoRetiro(Boolean autorizadoRetiro) { this.autorizadoRetiro = autorizadoRetiro; }
}
