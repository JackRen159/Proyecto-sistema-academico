package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "estudiante_acudiente",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_estudiante_acudiente",
        columnNames = {"estudiante_id", "acudiente_id"}
    )
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

    @CreationTimestamp
    @Column(name = "fecha_vinculacion", updatable = false)
    private LocalDateTime fechaVinculacion;

    public EstudianteAcudiente() {
    }

    public Long getId() {
        return id;
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

    public LocalDateTime getFechaVinculacion() {
        return fechaVinculacion;
    }
}

