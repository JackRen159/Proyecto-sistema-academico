package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "actas_promocion_anual",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_acta_promocion_numero", columnNames = {"numero_acta"})
    },
    indexes = {
        @Index(name = "idx_acta_prom_anio", columnList = "anio_lectivo_id"),
        @Index(name = "idx_acta_prom_grado", columnList = "grado_id")
    }
)
public class ActaPromocionAnual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grado_id", nullable = false)
    private Grado grado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    @Column(name = "numero_acta", nullable = false, length = 50)
    private String numeroActa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_acta", nullable = false, length = 40)
    private TipoActaPromocion tipoActa = TipoActaPromocion.ORDINARIA_COMISION;

    @Column(name = "fecha_acta", nullable = false)
    private LocalDate fechaActa = LocalDate.now();

    @Column(name = "integrantes_nominativos_json", nullable = false, columnDefinition = "TEXT")
    private String integrantesNominativosJson;

    @Column(name = "soporte_acta_url", length = 255)
    private String soporteActaUrl;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_registro_id")
    private Usuario usuarioRegistro;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "actaPromocion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PromocionEstudianteAnual> decisionesEstudiantes = new ArrayList<>();

    public ActaPromocionAnual() {
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

    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public String getNumeroActa() {
        return numeroActa;
    }

    public void setNumeroActa(String numeroActa) {
        this.numeroActa = numeroActa;
    }

    public TipoActaPromocion getTipoActa() {
        return tipoActa;
    }

    public void setTipoActa(TipoActaPromocion tipoActa) {
        this.tipoActa = tipoActa;
    }

    public LocalDate getFechaActa() {
        return fechaActa;
    }

    public void setFechaActa(LocalDate fechaActa) {
        this.fechaActa = fechaActa;
    }

    public String getIntegrantesNominativosJson() {
        return integrantesNominativosJson;
    }

    public void setIntegrantesNominativosJson(String integrantesNominativosJson) {
        this.integrantesNominativosJson = integrantesNominativosJson;
    }

    public String getSoporteActaUrl() {
        return soporteActaUrl;
    }

    public void setSoporteActaUrl(String soporteActaUrl) {
        this.soporteActaUrl = soporteActaUrl;
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

    public List<PromocionEstudianteAnual> getDecisionesEstudiantes() {
        return decisionesEstudiantes;
    }

    public void setDecisionesEstudiantes(List<PromocionEstudianteAnual> decisionesEstudiantes) {
        this.decisionesEstudiantes = decisionesEstudiantes;
    }

    public void addDecision(PromocionEstudianteAnual decision) {
        decisionesEstudiantes.add(decision);
        decision.setActaPromocion(this);
    }
}
