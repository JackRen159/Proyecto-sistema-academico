package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "horarios_bloques",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_bloque_version_dia_franja", columnNames = {"horario_version_id", "dia_semana", "franja_horaria_id"})
    },
    indexes = {
        @Index(name = "idx_bloques_version", columnList = "horario_version_id"),
        @Index(name = "idx_bloques_asignacion", columnList = "asignacion_academica_id"),
        @Index(name = "idx_bloques_aula", columnList = "aula_id"),
        @Index(name = "idx_bloques_dia_franja", columnList = "dia_semana, franja_horaria_id")
    }
)
public class HorarioBloque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "horario_version_id", nullable = false)
    private HorarioGrupoVersion horarioVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignacion_academica_id", nullable = false)
    private AsignacionAcademica asignacionAcademica;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 15)
    private DiaSemana diaSemana;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "franja_horaria_id", nullable = false)
    private PlantillaFranjaHoraria franjaHoraria;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @OneToMany(mappedBy = "horarioBloque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NovedadHorario> novedades = new ArrayList<>();

    public HorarioBloque() {
    }

    public HorarioBloque(HorarioGrupoVersion horarioVersion,
                         AsignacionAcademica asignacionAcademica,
                         Aula aula,
                         DiaSemana diaSemana,
                         PlantillaFranjaHoraria franjaHoraria) {
        this.horarioVersion = horarioVersion;
        this.asignacionAcademica = asignacionAcademica;
        this.aula = aula;
        this.diaSemana = diaSemana;
        this.franjaHoraria = franjaHoraria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HorarioGrupoVersion getHorarioVersion() {
        return horarioVersion;
    }

    public void setHorarioVersion(HorarioGrupoVersion horarioVersion) {
        this.horarioVersion = horarioVersion;
    }

    public AsignacionAcademica getAsignacionAcademica() {
        return asignacionAcademica;
    }

    public void setAsignacionAcademica(AsignacionAcademica asignacionAcademica) {
        this.asignacionAcademica = asignacionAcademica;
    }

    public Aula getAula() {
        return aula;
    }

    public void setAula(Aula aula) {
        this.aula = aula;
    }

    public DiaSemana getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(DiaSemana diaSemana) {
        this.diaSemana = diaSemana;
    }

    public PlantillaFranjaHoraria getFranjaHoraria() {
        return franjaHoraria;
    }

    public void setFranjaHoraria(PlantillaFranjaHoraria franjaHoraria) {
        this.franjaHoraria = franjaHoraria;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public List<NovedadHorario> getNovedades() {
        return novedades;
    }

    public void setNovedades(List<NovedadHorario> novedades) {
        this.novedades = novedades;
    }

    public void addNovedad(NovedadHorario novedad) {
        novedades.add(novedad);
        novedad.setHorarioBloque(this);
    }
}
