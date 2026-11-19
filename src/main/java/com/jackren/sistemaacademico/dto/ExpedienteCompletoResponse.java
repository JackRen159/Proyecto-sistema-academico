package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoExpediente;
import com.jackren.sistemaacademico.entity.TipoDocumento;

public class ExpedienteCompletoResponse {

    private Long estudianteId;
    private String estudianteDocumento;
    private TipoDocumento estudianteTipoDocumento;
    private String estudianteNombreCompleto;
    private EstadoExpediente estadoExpediente;
    private Long acudientePrincipalId;
    private String acudientePrincipalDocumento;
    private String acudientePrincipalNombreCompleto;
    private Boolean autorizacionDatosMenorRegistrada;
    private String mensaje;

    public ExpedienteCompletoResponse() {
    }

    public Long getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Long estudianteId) { this.estudianteId = estudianteId; }
    public String getEstudianteDocumento() { return estudianteDocumento; }
    public void setEstudianteDocumento(String estudianteDocumento) { this.estudianteDocumento = estudianteDocumento; }
    public TipoDocumento getEstudianteTipoDocumento() { return estudianteTipoDocumento; }
    public void setEstudianteTipoDocumento(TipoDocumento estudianteTipoDocumento) { this.estudianteTipoDocumento = estudianteTipoDocumento; }
    public String getEstudianteNombreCompleto() { return estudianteNombreCompleto; }
    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) { this.estudianteNombreCompleto = estudianteNombreCompleto; }
    public EstadoExpediente getEstadoExpediente() { return estadoExpediente; }
    public void setEstadoExpediente(EstadoExpediente estadoExpediente) { this.estadoExpediente = estadoExpediente; }
    public Long getAcudientePrincipalId() { return acudientePrincipalId; }
    public void setAcudientePrincipalId(Long acudientePrincipalId) { this.acudientePrincipalId = acudientePrincipalId; }
    public String getAcudientePrincipalDocumento() { return acudientePrincipalDocumento; }
    public void setAcudientePrincipalDocumento(String acudientePrincipalDocumento) { this.acudientePrincipalDocumento = acudientePrincipalDocumento; }
    public String getAcudientePrincipalNombreCompleto() { return acudientePrincipalNombreCompleto; }
    public void setAcudientePrincipalNombreCompleto(String acudientePrincipalNombreCompleto) { this.acudientePrincipalNombreCompleto = acudientePrincipalNombreCompleto; }
    public Boolean getAutorizacionDatosMenorRegistrada() { return autorizacionDatosMenorRegistrada; }
    public void setAutorizacionDatosMenorRegistrada(Boolean autorizacionDatosMenorRegistrada) { this.autorizacionDatosMenorRegistrada = autorizacionDatosMenorRegistrada; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
