package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "actas_nivelacion",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_actas_nivelacion_numero", columnNames = {"numero_acta"})
    },
    indexes = {
        @Index(name = "idx_acta_niv_matricula", columnList = "matricula_id"),
        @Index(name = "idx_acta_niv_calif_periodo", columnList = "calificacion_periodo_id"),
        @Index(name = "idx_acta_niv_periodo", columnList = "periodo_academico_id")
    }
)
public class ActaNivelacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_acta", nullable = false, length = 50)
    private String numeroActa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calificacion_periodo_id", nullable = false)
    private CalificacionPeriodo calificacionPeriodo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matricula_id", nullable = false)
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "docente_id")
    private Profesor docente;

    @Column(name = "fecha_sustentacion", nullable = false)
    private LocalDate fechaSustentacion;

    @Column(nullable = false)
    private Boolean asistencia = true;

    @Column(name = "nota_original", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaOriginal;

    @Column(name = "nota_obtenida", precision = 4, scale = 2)
    private BigDecimal notaObtenida;

    @Enumerated(EnumType.STRING)
    @Column(name = "formula_aplicada", nullable = false, length = 30)
    private FormulaRecuperacionSiee formulaAplicada;

    @Column(name = "nota_final_resultante", nullable = false, precision = 4, scale = 2)
    private BigDecimal notaFinalResultante;

    @Enumerated(EnumType.STRING)
    @Column(name = "desempeno_resultante", nullable = false, length = 20)
    private DesempenoNacional desempenoResultante;

    @Column(name = "estrategia_pedagogica", columnDefinition = "TEXT")
    private String estrategiaPedagogica;

    @Column(name = "evidencias_soporte", length = 255)
    private String evidenciasSoporte;

    @Column(length = 255)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ActaNivelacion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public CalificacionPeriodo getCalificacionPeriodo() {
        return calificacionPeriodo;
    }

    public void setCalificacionPeriodo(CalificacionPeriodo calificacionPeriodo) {
        this.calificacionPeriodo = calificacionPeriodo;
    }

    public Matricula getMatricula() {
        return matricula;
    }

    public void setMatricula(Matricula matricula) {
        this.matricula = matricula;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Profesor getDocente() {
        return docente;
    }

    public void setDocente(Profesor docente) {
        this.docente = docente;
    }

    public LocalDate getFechaSustentacion() {
        return fechaSustentacion;
    }

    public void setFechaSustentacion(LocalDate fechaSustentacion) {
        this.fechaSustentacion = fechaSustentacion;
    }

    public Boolean getAsistencia() {
        return asistencia;
    }

    public void setAsistencia(Boolean asistencia) {
        this.asistencia = asistencia;
    }

    public BigDecimal getNotaOriginal() {
        return notaOriginal;
    }

    public void setNotaOriginal(BigDecimal notaOriginal) {
        this.notaOriginal = notaOriginal;
    }

    public BigDecimal getNotaObtenida() {
        return notaObtenida;
    }

    public void setNotaObtenida(BigDecimal notaObtenida) {
        this.notaObtenida = notaObtenida;
    }

    public FormulaRecuperacionSiee getFormulaAplicada() {
        return formulaAplicada;
    }

    public void setFormulaAplicada(FormulaRecuperacionSiee formulaAplicada) {
        this.formulaAplicada = formulaAplicada;
    }

    public BigDecimal getNotaFinalResultante() {
        return notaFinalResultante;
    }

    public void setNotaFinalResultante(BigDecimal notaFinalResultante) {
        this.notaFinalResultante = notaFinalResultante;
    }

    public DesempenoNacional getDesempenoResultante() {
        return desempenoResultante;
    }

    public void setDesempenoResultante(DesempenoNacional desempenoResultante) {
        this.desempenoResultante = desempenoResultante;
    }

    public String getEstrategiaPedagogica() {
        return estrategiaPedagogica;
    }

    public void setEstrategiaPedagogica(String estrategiaPedagogica) {
        this.estrategiaPedagogica = estrategiaPedagogica;
    }

    public String getEvidenciasSoporte() {
        return evidenciasSoporte;
    }

    public void setEvidenciasSoporte(String evidenciasSoporte) {
        this.evidenciasSoporte = evidenciasSoporte;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Usuario getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(Usuario usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
