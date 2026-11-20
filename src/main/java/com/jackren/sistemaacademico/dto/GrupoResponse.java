package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.EstadoGrupo;
import com.jackren.sistemaacademico.entity.Jornada;

import java.time.LocalDateTime;

public class GrupoResponse {

    private Long id;
    private Long anioLectivoId;
    private Integer anioLectivoAnio;
    private Long gradoId;
    private String gradoCodigo;
    private String gradoNombre;
    private String nombre;
    private Jornada jornada;
    private Integer cupoMaximo;
    private String sede;
    private String aulaBase;
    private EstadoGrupo estado;
    private Integer matriculadosActuales;
    private Integer cuposDisponibles;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public GrupoResponse(Long id, Long anioLectivoId, Integer anioLectivoAnio, Long gradoId, String gradoCodigo,
                         String gradoNombre, String nombre, Jornada jornada, Integer cupoMaximo,
                         String sede, String aulaBase, EstadoGrupo estado, Integer matriculadosActuales, Integer cuposDisponibles,
                         Boolean activo, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.id = id;
        this.anioLectivoId = anioLectivoId;
        this.anioLectivoAnio = anioLectivoAnio;
        this.gradoId = gradoId;
        this.gradoCodigo = gradoCodigo;
        this.gradoNombre = gradoNombre;
        this.nombre = nombre;
        this.jornada = jornada;
        this.cupoMaximo = cupoMaximo;
        this.sede = sede != null ? sede : "SEDE_PRINCIPAL";
        this.aulaBase = aulaBase;
        this.estado = estado != null ? estado : EstadoGrupo.ACTIVO;
        this.matriculadosActuales = matriculadosActuales != null ? matriculadosActuales : 0;
        this.cuposDisponibles = cuposDisponibles != null ? cuposDisponibles : Math.max(0, (cupoMaximo != null ? cupoMaximo : 0) - this.matriculadosActuales);
        this.activo = activo;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    public GrupoResponse(Long id, Long anioLectivoId, Integer anioLectivoAnio, Long gradoId, String gradoCodigo,
                         String gradoNombre, String nombre, Jornada jornada, Integer cupoMaximo, Boolean activo,
                         LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this(id, anioLectivoId, anioLectivoAnio, gradoId, gradoCodigo, gradoNombre, nombre, jornada, cupoMaximo,
                "SEDE_PRINCIPAL", null, EstadoGrupo.ACTIVO, 0, cupoMaximo, activo, fechaCreacion, fechaActualizacion);
    }

    public Long getId() {
        return id;
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public Integer getAnioLectivoAnio() {
        return anioLectivoAnio;
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

    public String getNombre() {
        return nombre;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public String getSede() {
        return sede;
    }

    public String getAulaBase() {
        return aulaBase;
    }

    public EstadoGrupo getEstado() {
        return estado;
    }

    public Integer getMatriculadosActuales() {
        return matriculadosActuales;
    }

    public Integer getCuposDisponibles() {
        return cuposDisponibles;
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
