package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "estudiantes",
    indexes = {
        @Index(name = "idx_estudiantes_tipo_doc", columnList = "tipo_documento"),
        @Index(name = "idx_estudiantes_estado_exp", columnList = "estado_expediente")
    }
)
public class Estudiante extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento = TipoDocumento.TI;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SexoDocumental sexo = SexoDocumental.NO_ESPECIFICADO;

    @Column(nullable = false, length = 100)
    private String eps = "Sin asegurar / En trámite";

    @Column(name = "grupo_sanguineo_rh", nullable = false, length = 10)
    private String grupoSanguineoRh = "O+";

    @Column(name = "tiene_discapacidad_piar", nullable = false)
    private Boolean tieneDiscapacidadPiar = false;

    @Column(name = "detalles_salud_emergencia", columnDefinition = "TEXT")
    private String detallesSaludEmergencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_expediente", nullable = false, length = 30)
    private EstadoExpediente estadoExpediente = EstadoExpediente.LISTO_PARA_MATRICULA;

    public Estudiante() {
        super();
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento != null ? tipoDocumento : TipoDocumento.TI;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public SexoDocumental getSexo() {
        return sexo;
    }

    public void setSexo(SexoDocumental sexo) {
        this.sexo = sexo != null ? sexo : SexoDocumental.NO_ESPECIFICADO;
    }

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps != null && !eps.isBlank() ? eps : "Sin asegurar / En trámite";
    }

    public String getGrupoSanguineoRh() {
        return grupoSanguineoRh;
    }

    public void setGrupoSanguineoRh(String grupoSanguineoRh) {
        this.grupoSanguineoRh = grupoSanguineoRh != null && !grupoSanguineoRh.isBlank() ? grupoSanguineoRh : "O+";
    }

    public Boolean getTieneDiscapacidadPiar() {
        return tieneDiscapacidadPiar;
    }

    public void setTieneDiscapacidadPiar(Boolean tieneDiscapacidadPiar) {
        this.tieneDiscapacidadPiar = tieneDiscapacidadPiar != null ? tieneDiscapacidadPiar : false;
    }

    public String getDetallesSaludEmergencia() {
        return detallesSaludEmergencia;
    }

    public void setDetallesSaludEmergencia(String detallesSaludEmergencia) {
        this.detallesSaludEmergencia = detallesSaludEmergencia;
    }

    public EstadoExpediente getEstadoExpediente() {
        return estadoExpediente;
    }

    public void setEstadoExpediente(EstadoExpediente estadoExpediente) {
        this.estadoExpediente = estadoExpediente != null ? estadoExpediente : EstadoExpediente.LISTO_PARA_MATRICULA;
    }
}
