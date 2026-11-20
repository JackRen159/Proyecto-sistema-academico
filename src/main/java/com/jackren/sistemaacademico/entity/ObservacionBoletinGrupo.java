package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "observaciones_boletin_grupo",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_obs_boletin_mat_periodo", columnNames = {"matricula_id", "periodo_academico_id"})
    },
    indexes = {
        @Index(name = "idx_obs_boletin_mat", columnList = "matricula_id"),
        @Index(name = "idx_obs_boletin_per", columnList = "periodo_academico_id")
    }
)
public class ObservacionBoletinGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_director_id")
    private Profesor docenteDirector;

    @Column(name = "observacion_formativa", nullable = false, columnDefinition = "TEXT")
    private String observacionFormativa;

    @Column(name = "recomendaciones_pedagogicas", columnDefinition = "TEXT")
    private String recomendacionesPedagogicas;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ObservacionBoletinGrupo() {
    }

    public ObservacionBoletinGrupo(Matricula matricula,
                                   PeriodoAcademico periodoAcademico,
                                   Profesor docenteDirector,
                                   String observacionFormativa,
                                   String recomendacionesPedagogicas) {
        this.matricula = matricula;
        this.periodoAcademico = periodoAcademico;
        this.docenteDirector = docenteDirector;
        this.observacionFormativa = observacionFormativa;
        this.recomendacionesPedagogicas = recomendacionesPedagogicas;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Profesor getDocenteDirector() {
        return docenteDirector;
    }

    public void setDocenteDirector(Profesor docenteDirector) {
        this.docenteDirector = docenteDirector;
    }

    public String getObservacionFormativa() {
        return observacionFormativa;
    }

    public void setObservacionFormativa(String observacionFormativa) {
        this.observacionFormativa = observacionFormativa;
    }

    public String getRecomendacionesPedagogicas() {
        return recomendacionesPedagogicas;
    }

    public void setRecomendacionesPedagogicas(String recomendacionesPedagogicas) {
        this.recomendacionesPedagogicas = recomendacionesPedagogicas;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
