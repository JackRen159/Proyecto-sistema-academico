package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "actas_comision_evaluacion_periodica",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_acta_comision_numero", columnNames = {"numero_acta"})
    },
    indexes = {
        @Index(name = "idx_acta_com_per", columnList = "periodo_academico_id"),
        @Index(name = "idx_acta_com_grado", columnList = "grado_id")
    }
)
public class ActaComisionEvaluacionPeriodica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_academico_id", nullable = false)
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @Column(name = "numero_acta", nullable = false, length = 50)
    private String numeroActa;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDate fechaSesion;

    @Column(name = "integrantes_nominativos_json", nullable = false, columnDefinition = "TEXT")
    private String integrantesNominativosJson;

    @Column(name = "conclusiones_generales", columnDefinition = "TEXT")
    private String conclusionesGenerales;

    @Column(name = "compromisos_pedagogicos_json", columnDefinition = "TEXT")
    private String compromisosPedagogicosJson;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_secretario_id")
    private Usuario usuarioSecretario;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    public ActaComisionEvaluacionPeriodica() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public LocalDate getFechaSesion() {
        return fechaSesion;
    }

    public void setFechaSesion(LocalDate fechaSesion) {
        this.fechaSesion = fechaSesion;
    }

    public String getIntegrantesNominativosJson() {
        return integrantesNominativosJson;
    }

    public void setIntegrantesNominativosJson(String integrantesNominativosJson) {
        this.integrantesNominativosJson = integrantesNominativosJson;
    }

    public String getConclusionesGenerales() {
        return conclusionesGenerales;
    }

    public void setConclusionesGenerales(String conclusionesGenerales) {
        this.conclusionesGenerales = conclusionesGenerales;
    }

    public String getCompromisosPedagogicosJson() {
        return compromisosPedagogicosJson;
    }

    public void setCompromisosPedagogicosJson(String compromisosPedagogicosJson) {
        this.compromisosPedagogicosJson = compromisosPedagogicosJson;
    }

    public Usuario getUsuarioSecretario() {
        return usuarioSecretario;
    }

    public void setUsuarioSecretario(Usuario usuarioSecretario) {
        this.usuarioSecretario = usuarioSecretario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
