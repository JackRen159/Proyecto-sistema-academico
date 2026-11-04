package com.jackren.sistemaacademico.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "estudiantes")
public class Estudiante extends Persona {

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    public Estudiante() {
        super();
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }
}
