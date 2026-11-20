package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoMatricula;
import com.jackren.sistemaacademico.entity.Jornada;
import com.jackren.sistemaacademico.entity.TipoMatricula;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MatriculaResponse {

    private Long id;
    private String numeroMatricula;
    private Long estudianteId;
    private String estudianteNombreCompleto;
    private String estudianteDocumento;
    private Long grupoId;
    private String grupoNombre;
    private Long gradoId;
    private String gradoCodigo;
    private String gradoNombre;
    private Jornada jornada;
    private Long anioLectivoId;
    private Integer anioLectivo;
    private EstadoMatricula estado;
    private TipoMatricula tipoMatricula;
    private String folio;
    private LocalDate fechaMatricula;
    private LocalDate fechaEfectivaInicio;
    private LocalDate fechaEfectivaRetiro;
    private String institucionProcedencia;
    private Long acudientePrincipalId;
    private String acudientePrincipalNombre;
    private String acudientePrincipalDocumento;
    private Long sieeVersionId;
    private Long manualConvivenciaVersionId;
    private String actaConsejoDirectivo;
    private LocalDate fechaActaConsejoDirectivo;
    private Boolean esRepitente;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public MatriculaResponse() {
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

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public String getEstudianteDocumento() {
        return estudianteDocumento;
    }

    public void setEstudianteDocumento(String estudianteDocumento) {
        this.estudianteDocumento = estudianteDocumento;
    }

    public Long getGrupoId() {
        return grupoId;
    }

    public void setGrupoId(Long grupoId) {
        this.grupoId = grupoId;
    }

    public String getGrupoNombre() {
        return grupoNombre;
    }

    public void setGrupoNombre(String grupoNombre) {
        this.grupoNombre = grupoNombre;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public void setGradoId(Long gradoId) {
        this.gradoId = gradoId;
    }

    public String getGradoCodigo() {
        return gradoCodigo;
    }

    public void setGradoCodigo(String gradoCodigo) {
        this.gradoCodigo = gradoCodigo;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public void setGradoNombre(String gradoNombre) {
        this.gradoNombre = gradoNombre;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public Integer getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(Integer anioLectivo) {
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

    public Long getAcudientePrincipalId() {
        return acudientePrincipalId;
    }

    public void setAcudientePrincipalId(Long acudientePrincipalId) {
        this.acudientePrincipalId = acudientePrincipalId;
    }

    public String getAcudientePrincipalNombre() {
        return acudientePrincipalNombre;
    }

    public void setAcudientePrincipalNombre(String acudientePrincipalNombre) {
        this.acudientePrincipalNombre = acudientePrincipalNombre;
    }

    public String getAcudientePrincipalDocumento() {
        return acudientePrincipalDocumento;
    }

    public void setAcudientePrincipalDocumento(String acudientePrincipalDocumento) {
        this.acudientePrincipalDocumento = acudientePrincipalDocumento;
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

    public Boolean getEsRepitente() {
        return esRepitente;
    }

    public void setEsRepitente(Boolean esRepitente) {
        this.esRepitente = esRepitente;
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
