package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "anios_lectivos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_anios_lectivos_anio", columnNames = {"anio"})
    }
)
public class AnioLectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer anio;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAnioLectivo estado = EstadoAnioLectivo.PLANIFICACION;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_calendario", nullable = false, length = 20)
    private TipoCalendario tipoCalendario = TipoCalendario.CALENDARIO_A;

    @Column(name = "semanas_lectivas", nullable = false)
    private Integer semanasLectivas = 40;

    @Column(name = "numero_acta_aprobacion", length = 50)
    private String numeroActaAprobacion;

    @Column(name = "fecha_acta_aprobacion")
    private LocalDate fechaActaAprobacion;

    @Column(name = "organo_aprobador", length = 40)
    private String organoAprobador;

    @Column(name = "motivo_ajuste_excepcional", length = 500)
    private String motivoAjusteExcepcional;

    @Column(name = "resolucion_rectoral_excepcional", length = 100)
    private String resolucionRectoralExcepcional;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public AnioLectivo() {
    }

    public AnioLectivo(Integer anio, String nombre, LocalDate fechaInicio, LocalDate fechaFin) {
        this.anio = anio;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = EstadoAnioLectivo.PLANIFICACION;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public EstadoAnioLectivo getEstado() {
        return estado;
    }

    public void setEstado(EstadoAnioLectivo estado) {
        this.estado = estado;
    }

    public TipoCalendario getTipoCalendario() {
        return tipoCalendario;
    }

    public void setTipoCalendario(TipoCalendario tipoCalendario) {
        this.tipoCalendario = tipoCalendario;
    }

    public Integer getSemanasLectivas() {
        return semanasLectivas;
    }

    public void setSemanasLectivas(Integer semanasLectivas) {
        this.semanasLectivas = semanasLectivas;
    }

    public String getNumeroActaAprobacion() {
        return numeroActaAprobacion;
    }

    public void setNumeroActaAprobacion(String numeroActaAprobacion) {
        this.numeroActaAprobacion = numeroActaAprobacion;
    }

    public LocalDate getFechaActaAprobacion() {
        return fechaActaAprobacion;
    }

    public void setFechaActaAprobacion(LocalDate fechaActaAprobacion) {
        this.fechaActaAprobacion = fechaActaAprobacion;
    }

    public String getOrganoAprobador() {
        return organoAprobador;
    }

    public void setOrganoAprobador(String organoAprobador) {
        this.organoAprobador = organoAprobador;
    }

    public String getMotivoAjusteExcepcional() {
        return motivoAjusteExcepcional;
    }

    public void setMotivoAjusteExcepcional(String motivoAjusteExcepcional) {
        this.motivoAjusteExcepcional = motivoAjusteExcepcional;
    }

    public String getResolucionRectoralExcepcional() {
        return resolucionRectoralExcepcional;
    }

    public void setResolucionRectoralExcepcional(String resolucionRectoralExcepcional) {
        this.resolucionRectoralExcepcional = resolucionRectoralExcepcional;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
