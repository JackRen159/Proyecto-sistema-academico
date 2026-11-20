package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "matriculas")
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_matricula", nullable = false, unique = true, length = 30)
    private String numeroMatricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoMatricula estado = EstadoMatricula.MATRICULADA;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_matricula", nullable = false, length = 25)
    private TipoMatricula tipoMatricula = TipoMatricula.REGULAR;

    @Column(name = "fecha_efectiva_inicio")
    private LocalDate fechaEfectivaInicio;

    @Column(name = "fecha_efectiva_retiro")
    private LocalDate fechaEfectivaRetiro;

    @Column(name = "institucion_procedencia", length = 150)
    private String institucionProcedencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acudiente_principal_id")
    private Acudiente acudientePrincipal;

    @Column(name = "siee_version_id")
    private Long sieeVersionId;

    @Column(name = "manual_convivencia_version_id")
    private Long manualConvivenciaVersionId;

    @Column(name = "fecha_aceptacion_reglamentos")
    private LocalDateTime fechaAceptacionReglamentos;

    @Column(name = "acta_consejo_directivo", length = 50)
    private String actaConsejoDirectivo;

    @Column(name = "fecha_acta_consejo_directivo")
    private LocalDate fechaActaConsejoDirectivo;

    @Column(name = "consentimiento_acudiente", nullable = false)
    private Boolean consentimientoAcudiente = false;

    @Column(name = "es_repitente", nullable = false)
    private Boolean esRepitente = false;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @Column(length = 30)
    private String folio;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula = LocalDate.now();

    @Column(length = 255)
    private String observaciones;

    @Column(nullable = false)
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public Matricula() {
    }

    public Matricula(String numeroMatricula, Estudiante estudiante, Grupo grupo, AnioLectivo anioLectivo) {
        this.numeroMatricula = numeroMatricula;
        this.estudiante = estudiante;
        this.grupo = grupo;
        this.anioLectivo = anioLectivo;
        this.estado = EstadoMatricula.MATRICULADA;
        this.tipoMatricula = TipoMatricula.REGULAR;
        this.fechaMatricula = LocalDate.now();
        this.activo = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroMatricula() {
        return numeroMatricula;
    }

    public void setNumeroMatricula(String numeroMatricula) {
        this.numeroMatricula = numeroMatricula;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public EstadoMatricula getEstado() {
        return estado;
    }

    public void setEstado(EstadoMatricula estado) {
        this.estado = estado;
    }

    public TipoMatricula getTipoMatricula() {
        return tipoMatricula;
    }

    public void setTipoMatricula(TipoMatricula tipoMatricula) {
        this.tipoMatricula = tipoMatricula;
    }

    public LocalDate getFechaEfectivaInicio() {
        return fechaEfectivaInicio;
    }

    public void setFechaEfectivaInicio(LocalDate fechaEfectivaInicio) {
        this.fechaEfectivaInicio = fechaEfectivaInicio;
    }

    public LocalDate getFechaEfectivaRetiro() {
        return fechaEfectivaRetiro;
    }

    public void setFechaEfectivaRetiro(LocalDate fechaEfectivaRetiro) {
        this.fechaEfectivaRetiro = fechaEfectivaRetiro;
    }

    public String getInstitucionProcedencia() {
        return institucionProcedencia;
    }

    public void setInstitucionProcedencia(String institucionProcedencia) {
        this.institucionProcedencia = institucionProcedencia;
    }

    public Acudiente getAcudientePrincipal() {
        return acudientePrincipal;
    }

    public void setAcudientePrincipal(Acudiente acudientePrincipal) {
        this.acudientePrincipal = acudientePrincipal;
    }

    public Long getSieeVersionId() {
        return sieeVersionId;
    }

    public void setSieeVersionId(Long sieeVersionId) {
        this.sieeVersionId = sieeVersionId;
    }

    public Long getManualConvivenciaVersionId() {
        return manualConvivenciaVersionId;
    }

    public void setManualConvivenciaVersionId(Long manualConvivenciaVersionId) {
        this.manualConvivenciaVersionId = manualConvivenciaVersionId;
    }

    public LocalDateTime getFechaAceptacionReglamentos() {
        return fechaAceptacionReglamentos;
    }

    public void setFechaAceptacionReglamentos(LocalDateTime fechaAceptacionReglamentos) {
        this.fechaAceptacionReglamentos = fechaAceptacionReglamentos;
    }

    public String getActaConsejoDirectivo() {
        return actaConsejoDirectivo;
    }

    public void setActaConsejoDirectivo(String actaConsejoDirectivo) {
        this.actaConsejoDirectivo = actaConsejoDirectivo;
    }

    public LocalDate getFechaActaConsejoDirectivo() {
        return fechaActaConsejoDirectivo;
    }

    public void setFechaActaConsejoDirectivo(LocalDate fechaActaConsejoDirectivo) {
        this.fechaActaConsejoDirectivo = fechaActaConsejoDirectivo;
    }

    public Boolean getConsentimientoAcudiente() {
        return consentimientoAcudiente;
    }

    public void setConsentimientoAcudiente(Boolean consentimientoAcudiente) {
        this.consentimientoAcudiente = consentimientoAcudiente;
    }

    public Boolean getEsRepitente() {
        return esRepitente;
    }

    public void setEsRepitente(Boolean esRepitente) {
        this.esRepitente = esRepitente;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getFolio() {
        return folio;
    }

    public void setFolio(String folio) {
        this.folio = folio;
    }

    public LocalDate getFechaMatricula() {
        return fechaMatricula;
    }

    public void setFechaMatricula(LocalDate fechaMatricula) {
        this.fechaMatricula = fechaMatricula;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
