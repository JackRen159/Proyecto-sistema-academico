package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "estudiante_acudiente",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_estudiante_acudiente",
        columnNames = {"estudiante_id", "acudiente_id"}
    ),
    indexes = {
        @Index(name = "idx_est_acud_estado", columnList = "estado_vinculo")
    }
)
public class EstudianteAcudiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(optional = false)
    @JoinColumn(name = "acudiente_id", nullable = false)
    private Acudiente acudiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Parentesco parentesco;

    @Column(name = "es_principal", nullable = false)
    private Boolean esPrincipal = false;

    @Column(name = "convive_con_estudiante", nullable = false)
    private Boolean conviveConEstudiante = false;

    @Column(name = "autorizado_retiro", nullable = false)
    private Boolean autorizadoRetiro = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_vinculo", nullable = false, length = 30)
    private EstadoVinculo estadoVinculo = EstadoVinculo.VERIFICADO;

    @Column(name = "tiene_custodia", nullable = false)
    private Boolean tieneCustodia = true;

    @Column(name = "representacion_legal", nullable = false)
    private Boolean representacionLegal = true;

    @Column(name = "prioridad_emergencia", nullable = false)
    private Integer prioridadEmergencia = 1;

    @Column(name = "motivo_bloqueo_legal", length = 500)
    private String motivoBloqueoLegal;

    @CreationTimestamp
    @Column(name = "fecha_vinculacion", updatable = false)
    private LocalDateTime fechaVinculacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public EstudianteAcudiente() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Acudiente getAcudiente() {
        return acudiente;
    }

    public void setAcudiente(Acudiente acudiente) {
        this.acudiente = acudiente;
    }

    public Parentesco getParentesco() {
        return parentesco;
    }

    public void setParentesco(Parentesco parentesco) {
        this.parentesco = parentesco;
    }

    public Boolean getEsPrincipal() {
        return esPrincipal;
    }

    public void setEsPrincipal(Boolean esPrincipal) {
        this.esPrincipal = esPrincipal;
    }

    public Boolean getConviveConEstudiante() {
        return conviveConEstudiante;
    }

    public void setConviveConEstudiante(Boolean conviveConEstudiante) {
        this.conviveConEstudiante = conviveConEstudiante;
    }

    public Boolean getAutorizadoRetiro() {
        return autorizadoRetiro;
    }

    public void setAutorizadoRetiro(Boolean autorizadoRetiro) {
        this.autorizadoRetiro = autorizadoRetiro;
    }

    public EstadoVinculo getEstadoVinculo() {
        return estadoVinculo;
    }

    public void setEstadoVinculo(EstadoVinculo estadoVinculo) {
        this.estadoVinculo = estadoVinculo != null ? estadoVinculo : EstadoVinculo.VERIFICADO;
    }

    public Boolean getTieneCustodia() {
        return tieneCustodia;
    }

    public void setTieneCustodia(Boolean tieneCustodia) {
        this.tieneCustodia = tieneCustodia != null ? tieneCustodia : true;
    }

    public Boolean getRepresentacionLegal() {
        return representacionLegal;
    }

    public void setRepresentacionLegal(Boolean representacionLegal) {
        this.representacionLegal = representacionLegal != null ? representacionLegal : true;
    }

    public Integer getPrioridadEmergencia() {
        return prioridadEmergencia;
    }

    public void setPrioridadEmergencia(Integer prioridadEmergencia) {
        this.prioridadEmergencia = prioridadEmergencia != null ? prioridadEmergencia : 1;
    }

    public String getMotivoBloqueoLegal() {
        return motivoBloqueoLegal;
    }

    public void setMotivoBloqueoLegal(String motivoBloqueoLegal) {
        this.motivoBloqueoLegal = motivoBloqueoLegal;
    }

    public LocalDateTime getFechaVinculacion() {
        return fechaVinculacion;
    }

    public void setFechaVinculacion(LocalDateTime fechaVinculacion) {
        this.fechaVinculacion = fechaVinculacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
