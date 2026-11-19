package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "configuracion_siee_versiones",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_siee_version", columnNames = {"numero_version"})
    }
)
public class ConfiguracionSieeVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Column(name = "nombre_version", nullable = false, length = 100)
    private String nombreVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoVersionConfiguracion estado = EstadoVersionConfiguracion.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_vigencia_id")
    private AnioLectivo anioLectivoVigencia;

    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;

    @Column(name = "escala_minima", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaMinima = new BigDecimal("1.00");

    @Column(name = "escala_maxima", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaMaxima = new BigDecimal("5.00");

    @Column(name = "escala_aprobatoria", nullable = false, precision = 4, scale = 2)
    private BigDecimal escalaAprobatoria = new BigDecimal("3.00");

    @Column(nullable = false)
    private Integer decimales = 2;

    @Column(name = "modo_redondeo", nullable = false, length = 30)
    private String modoRedondeo = "HALF_UP";

    @Column(name = "rangos_equivalencia_json", nullable = false, columnDefinition = "TEXT")
    private String rangosEquivalenciaJson;

    @Column(name = "ponderacion_periodos_json", columnDefinition = "TEXT")
    private String ponderacionPeriodosJson;

    @Column(name = "criterios_promocion_json", columnDefinition = "TEXT")
    private String criteriosPromocionJson;

    @Column(name = "umbral_asistencia_reprobacion", precision = 5, scale = 2)
    private BigDecimal umbralAsistenciaReprobacion;

    @Column(name = "plazo_reclamaciones_dias", nullable = false)
    private Integer plazoReclamacionesDias = 3;

    @Column(name = "acta_consejo_academico", length = 100)
    private String actaConsejoAcademico;

    @Column(name = "acta_consejo_directivo", length = 100)
    private String actaConsejoDirectivo;

    @Column(name = "soporte_pdf_url", length = 255)
    private String soportePdfUrl;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_aprobacion")
    private LocalDateTime fechaAprobacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_creador_id")
    private Usuario usuarioCreador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_aprobador_id")
    private Usuario usuarioAprobador;

    public ConfiguracionSieeVersion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public String getNombreVersion() {
        return nombreVersion;
    }

    public void setNombreVersion(String nombreVersion) {
        this.nombreVersion = nombreVersion;
    }

    public EstadoVersionConfiguracion getEstado() {
        return estado;
    }

    public void setEstado(EstadoVersionConfiguracion estado) {
        this.estado = estado;
    }

    public AnioLectivo getAnioLectivoVigencia() {
        return anioLectivoVigencia;
    }

    public void setAnioLectivoVigencia(AnioLectivo anioLectivoVigencia) {
        this.anioLectivoVigencia = anioLectivoVigencia;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public BigDecimal getEscalaMinima() {
        return escalaMinima;
    }

    public void setEscalaMinima(BigDecimal escalaMinima) {
        this.escalaMinima = escalaMinima;
    }

    public BigDecimal getEscalaMaxima() {
        return escalaMaxima;
    }

    public void setEscalaMaxima(BigDecimal escalaMaxima) {
        this.escalaMaxima = escalaMaxima;
    }

    public BigDecimal getEscalaAprobatoria() {
        return escalaAprobatoria;
    }

    public void setEscalaAprobatoria(BigDecimal escalaAprobatoria) {
        this.escalaAprobatoria = escalaAprobatoria;
    }

    public Integer getDecimales() {
        return decimales;
    }

    public void setDecimales(Integer decimales) {
        this.decimales = decimales;
    }

    public String getModoRedondeo() {
        return modoRedondeo;
    }

    public void setModoRedondeo(String modoRedondeo) {
        this.modoRedondeo = modoRedondeo;
    }

    public String getRangosEquivalenciaJson() {
        return rangosEquivalenciaJson;
    }

    public void setRangosEquivalenciaJson(String rangosEquivalenciaJson) {
        this.rangosEquivalenciaJson = rangosEquivalenciaJson;
    }

    public String getPonderacionPeriodosJson() {
        return ponderacionPeriodosJson;
    }

    public void setPonderacionPeriodosJson(String ponderacionPeriodosJson) {
        this.ponderacionPeriodosJson = ponderacionPeriodosJson;
    }

    public String getCriteriosPromocionJson() {
        return criteriosPromocionJson;
    }

    public void setCriteriosPromocionJson(String criteriosPromocionJson) {
        this.criteriosPromocionJson = criteriosPromocionJson;
    }

    public BigDecimal getUmbralAsistenciaReprobacion() {
        return umbralAsistenciaReprobacion;
    }

    public void setUmbralAsistenciaReprobacion(BigDecimal umbralAsistenciaReprobacion) {
        this.umbralAsistenciaReprobacion = umbralAsistenciaReprobacion;
    }

    public Integer getPlazoReclamacionesDias() {
        return plazoReclamacionesDias;
    }

    public void setPlazoReclamacionesDias(Integer plazoReclamacionesDias) {
        this.plazoReclamacionesDias = plazoReclamacionesDias;
    }

    public String getActaConsejoAcademico() {
        return actaConsejoAcademico;
    }

    public void setActaConsejoAcademico(String actaConsejoAcademico) {
        this.actaConsejoAcademico = actaConsejoAcademico;
    }

    public String getActaConsejoDirectivo() {
        return actaConsejoDirectivo;
    }

    public void setActaConsejoDirectivo(String actaConsejoDirectivo) {
        this.actaConsejoDirectivo = actaConsejoDirectivo;
    }

    public String getSoportePdfUrl() {
        return soportePdfUrl;
    }

    public void setSoportePdfUrl(String soportePdfUrl) {
        this.soportePdfUrl = soportePdfUrl;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public Usuario getUsuarioCreador() {
        return usuarioCreador;
    }

    public void setUsuarioCreador(Usuario usuarioCreador) {
        this.usuarioCreador = usuarioCreador;
    }

    public Usuario getUsuarioAprobador() {
        return usuarioAprobador;
    }

    public void setUsuarioAprobador(Usuario usuarioAprobador) {
        this.usuarioAprobador = usuarioAprobador;
    }
}
