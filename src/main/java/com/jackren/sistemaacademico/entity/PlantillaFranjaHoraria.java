package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
@Table(
    name = "plantillas_franja_horaria",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_franja_anio_jornada_bloque", columnNames = {"anio_lectivo_id", "jornada", "numero_bloque"})
    },
    indexes = {
        @Index(name = "idx_franjas_anio_jornada", columnList = "anio_lectivo_id, jornada")
    }
)
public class PlantillaFranjaHoraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Jornada jornada;

    @Column(name = "numero_bloque", nullable = false)
    private Integer numeroBloque;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_franja", nullable = false, length = 30)
    private TipoFranjaHoraria tipoFranja = TipoFranjaHoraria.BLOQUE_LECTIVO;

    @Column(length = 100)
    private String descripcion;

    public PlantillaFranjaHoraria() {
    }

    public PlantillaFranjaHoraria(AnioLectivo anioLectivo,
                                  Jornada jornada,
                                  Integer numeroBloque,
                                  LocalTime horaInicio,
                                  LocalTime horaFin,
                                  TipoFranjaHoraria tipoFranja,
                                  String descripcion) {
        this.anioLectivo = anioLectivo;
        this.jornada = jornada;
        this.numeroBloque = numeroBloque;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.tipoFranja = tipoFranja != null ? tipoFranja : TipoFranjaHoraria.BLOQUE_LECTIVO;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Integer getNumeroBloque() {
        return numeroBloque;
    }

    public void setNumeroBloque(Integer numeroBloque) {
        this.numeroBloque = numeroBloque;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public TipoFranjaHoraria getTipoFranja() {
        return tipoFranja;
    }

    public void setTipoFranja(TipoFranjaHoraria tipoFranja) {
        this.tipoFranja = tipoFranja;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
