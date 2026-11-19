package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoExpediente;
import com.jackren.sistemaacademico.entity.TipoDocumento;

import java.time.LocalDate;
import java.util.List;

public class FichaEscolarEstudianteResponse {

    private Long id;
    private TipoDocumento tipoDocumento;
    private String documento;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private LocalDate fechaNacimiento;
    private Integer edadCalculada;
    private String correo;
    private String telefono;
    private String direccion;
    private String eps;
    private String grupoSanguineoRh;
    private Boolean tieneDiscapacidadPiar;
    private String detallesSaludEmergencia;
    private EstadoExpediente estadoExpediente;
    private String estadoMatricula;
    private String cursoActual;
    private List<ContactoUrgenciaDTO> contactosEmergencia;

    public FichaEscolarEstudianteResponse() {
    }

    public static class ContactoUrgenciaDTO {
        private String nombreCompleto;
        private String parentesco;
        private String telefono;
        private Boolean esPrincipal;
        private Boolean autorizadoRetiro;
        private Integer prioridad;

        public ContactoUrgenciaDTO() {}

        public ContactoUrgenciaDTO(String nombreCompleto, String parentesco, String telefono, Boolean esPrincipal, Boolean autorizadoRetiro, Integer prioridad) {
            this.nombreCompleto = nombreCompleto;
            this.parentesco = parentesco;
            this.telefono = telefono;
            this.esPrincipal = esPrincipal;
            this.autorizadoRetiro = autorizadoRetiro;
            this.prioridad = prioridad;
        }

        public String getNombreCompleto() { return nombreCompleto; }
        public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
        public String getParentesco() { return parentesco; }
        public void setParentesco(String parentesco) { this.parentesco = parentesco; }
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        public Boolean getEsPrincipal() { return esPrincipal; }
        public void setEsPrincipal(Boolean esPrincipal) { this.esPrincipal = esPrincipal; }
        public Boolean getAutorizadoRetiro() { return autorizadoRetiro; }
        public void setAutorizadoRetiro(Boolean autorizadoRetiro) { this.autorizadoRetiro = autorizadoRetiro; }
        public Integer getPrioridad() { return prioridad; }
        public void setPrioridad(Integer prioridad) { this.prioridad = prioridad; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public Integer getEdadCalculada() { return edadCalculada; }
    public void setEdadCalculada(Integer edadCalculada) { this.edadCalculada = edadCalculada; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public String getEps() { return eps; }
    public void setEps(String eps) { this.eps = eps; }
    public String getGrupoSanguineoRh() { return grupoSanguineoRh; }
    public void setGrupoSanguineoRh(String grupoSanguineoRh) { this.grupoSanguineoRh = grupoSanguineoRh; }
    public Boolean getTieneDiscapacidadPiar() { return tieneDiscapacidadPiar; }
    public void setTieneDiscapacidadPiar(Boolean tieneDiscapacidadPiar) { this.tieneDiscapacidadPiar = tieneDiscapacidadPiar; }
    public String getDetallesSaludEmergencia() { return detallesSaludEmergencia; }
    public void setDetallesSaludEmergencia(String detallesSaludEmergencia) { this.detallesSaludEmergencia = detallesSaludEmergencia; }
    public EstadoExpediente getEstadoExpediente() { return estadoExpediente; }
    public void setEstadoExpediente(EstadoExpediente estadoExpediente) { this.estadoExpediente = estadoExpediente; }
    public String getEstadoMatricula() { return estadoMatricula; }
    public void setEstadoMatricula(String estadoMatricula) { this.estadoMatricula = estadoMatricula; }
    public String getCursoActual() { return cursoActual; }
    public void setCursoActual(String cursoActual) { this.cursoActual = cursoActual; }
    public List<ContactoUrgenciaDTO> getContactosEmergencia() { return contactosEmergencia; }
    public void setContactosEmergencia(List<ContactoUrgenciaDTO> contactosEmergencia) { this.contactosEmergencia = contactosEmergencia; }
}
