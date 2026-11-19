package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(
    name = "acudientes",
    indexes = {
        @Index(name = "idx_acudientes_tipo_doc", columnList = "tipo_documento")
    }
)
@AttributeOverride(
    name = "telefono",
    column = @Column(name = "telefono", nullable = false, length = 20)
)
public class Acudiente extends Persona {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento = TipoDocumento.CC;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "telefono_alternativo", length = 20)
    private String telefonoAlternativo;

    @Column(nullable = false, length = 80)
    private String ocupacion;

    public Acudiente() {
        super();
    }

    public TipoDocumento getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumento tipoDocumento) {
        this.tipoDocumento = tipoDocumento != null ? tipoDocumento : TipoDocumento.CC;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefonoAlternativo() {
        return telefonoAlternativo;
    }

    public void setTelefonoAlternativo(String telefonoAlternativo) {
        this.telefonoAlternativo = telefonoAlternativo;
    }

    public String getOcupacion() {
        return ocupacion;
    }

    public void setOcupacion(String ocupacion) {
        this.ocupacion = ocupacion;
    }
}
