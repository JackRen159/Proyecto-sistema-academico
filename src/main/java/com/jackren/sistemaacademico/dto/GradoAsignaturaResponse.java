package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoComponenteCurricular;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GradoAsignaturaResponse {

    private Long id;
    private Long gradoId;
    private String gradoCodigo;
    private String gradoNombre;
    private Long asignaturaId;
    private String asignaturaCodigo;
    private String asignaturaNombre;
    private Long anioLectivoId;
    private Integer anioLectivoAnio;
    private Integer intensidadHoraria;
    private Boolean esOptativa;
    private BigDecimal pesoPorcentualEnArea;
    private TipoComponenteCurricular tipoComponente;
    private Boolean computaPromocion;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public GradoAsignaturaResponse(Long id, Long gradoId, String gradoCodigo, String gradoNombre,
                                  Long asignaturaId, String asignaturaCodigo, String asignaturaNombre,
                                  Long anioLectivoId, Integer anioLectivoAnio,
                                  Integer intensidadHoraria, Boolean esOptativa,
                                  BigDecimal pesoPorcentualEnArea, TipoComponenteCurricular tipoComponente,
                                  Boolean computaPromocion, Boolean activo,
                                  LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.gradoId = gradoId;
        this.gradoCodigo = gradoCodigo;
        this.gradoNombre = gradoNombre;
        this.asignaturaId = asignaturaId;
        this.asignaturaCodigo = asignaturaCodigo;
        this.asignaturaNombre = asignaturaNombre;
        this.anioLectivoId = anioLectivoId;
        this.anioLectivoAnio = anioLectivoAnio;
        this.intensidadHoraria = intensidadHoraria;
        this.esOptativa = esOptativa;
        this.pesoPorcentualEnArea = pesoPorcentualEnArea != null ? pesoPorcentualEnArea : new BigDecimal("100.00");
        this.tipoComponente = tipoComponente != null ? tipoComponente : TipoComponenteCurricular.ASIGNATURA;
        this.computaPromocion = computaPromocion != null ? computaPromocion : true;
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public GradoAsignaturaResponse(Long id, Long gradoId, String gradoCodigo, String gradoNombre,
                                  Long asignaturaId, String asignaturaCodigo, String asignaturaNombre,
                                  Integer intensidadHoraria, Boolean esOptativa, Boolean activo,
                                  LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this(id, gradoId, gradoCodigo, gradoNombre, asignaturaId, asignaturaCodigo, asignaturaNombre,
                null, null, intensidadHoraria, esOptativa, new BigDecimal("100.00"), TipoComponenteCurricular.ASIGNATURA,
                true, activo, fechaCreacion, fechaActualizacion);
    }

    public Long getId() {
        return id;
    }

    public Long getGradoId() {
        return gradoId;
    }

    public String getGradoCodigo() {
        return gradoCodigo;
    }

    public String getGradoNombre() {
        return gradoNombre;
    }

    public Long getAsignaturaId() {
        return asignaturaId;
    }

    public String getAsignaturaCodigo() {
        return asignaturaCodigo;
    }

    public String getAsignaturaNombre() {
        return asignaturaNombre;
    }

    public Integer getIntensidadHoraria() {
        return intensidadHoraria;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public Integer getAnioLectivoAnio() {
        return anioLectivoAnio;
    }

    public BigDecimal getPesoPorcentualEnArea() {
        return pesoPorcentualEnArea;
    }

    public TipoComponenteCurricular getTipoComponente() {
        return tipoComponente;
    }

    public Boolean getComputaPromocion() {
        return computaPromocion;
    }

    public Boolean getEsOptativa() {
        return esOptativa;
    }

    public Boolean getActivo() {
        return activo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
