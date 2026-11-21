package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
    name = "anotaciones_observador",
    indexes = {
        @Index(name = "idx_obs_estudiante", columnList = "estudiante_id"),
        @Index(name = "idx_obs_periodo", columnList = "periodo_academico_id"),
        @Index(name = "idx_obs_autor", columnList = "autor_usuario_id"),
        @Index(name = "idx_obs_tipo", columnList = "tipo_anotacion")
    }
)
public class AnotacionObservador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "periodo_academico_id")
    private PeriodoAcademico periodoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autor_usuario_id", nullable = false)
    private Usuario autorUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_anotacion", nullable = false, length = 45)
    private TipoAnotacionObservador tipoAnotacion;

    @Column(name = "fecha_hecho", nullable = false)
    private LocalDate fechaHecho;

    @Column(name = "hora_hecho")
    private LocalTime horaHecho;

    @Column(name = "lugar_hecho", nullable = false, length = 150)
    private String lugarHecho;

    @Column(name = "descripcion_factica", nullable = false, columnDefinition = "TEXT")
    private String descripcionFactica;

    @Column(name = "acciones_pedagogicas", columnDefinition = "TEXT")
    private String accionesPedagogicas;

    @Column(name = "es_zona_comun", nullable = false)
    private Boolean esZonaComun = false;

    @Column(name = "requiere_activacion_ruta", nullable = false)
    private Boolean requiereActivacionRuta = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caso_convivencia_id")
    private CasoConvivencia casoConvivencia;

    @Column(name = "version_libre_estudiante", columnDefinition = "TEXT")
    private String versionLibreEstudiante;

    @Column(name = "fecha_version_estudiante")
    private LocalDateTime fechaVersionEstudiante;

    @Column(name = "enterado_acudiente", nullable = false)
    private Boolean enteradoAcudiente = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acudiente_usuario_id")
    private Usuario acudienteUsuario;

    @Column(name = "fecha_enterado_acudiente")
    private LocalDateTime fechaEnteradoAcudiente;

    @Column(name = "anotacion_firmada", nullable = false)
    private Boolean anotacionFirmada = true;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public AnotacionObservador() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public PeriodoAcademico getPeriodoAcademico() { return periodoAcademico; }
    public void setPeriodoAcademico(PeriodoAcademico periodoAcademico) { this.periodoAcademico = periodoAcademico; }

    public Usuario getAutorUsuario() { return autorUsuario; }
    public void setAutorUsuario(Usuario autorUsuario) { this.autorUsuario = autorUsuario; }

    public TipoAnotacionObservador getTipoAnotacion() { return tipoAnotacion; }
    public void setTipoAnotacion(TipoAnotacionObservador tipoAnotacion) { this.tipoAnotacion = tipoAnotacion; }

    public LocalDate getFechaHecho() { return fechaHecho; }
    public void setFechaHecho(LocalDate fechaHecho) { this.fechaHecho = fechaHecho; }

    public LocalTime getHoraHecho() { return horaHecho; }
    public void setHoraHecho(LocalTime horaHecho) { this.horaHecho = horaHecho; }

    public String getLugarHecho() { return lugarHecho; }
    public void setLugarHecho(String lugarHecho) { this.lugarHecho = lugarHecho; }

    public String getDescripcionFactica() { return descripcionFactica; }
    public void setDescripcionFactica(String descripcionFactica) { this.descripcionFactica = descripcionFactica; }

    public String getAccionesPedagogicas() { return accionesPedagogicas; }
    public void setAccionesPedagogicas(String accionesPedagogicas) { this.accionesPedagogicas = accionesPedagogicas; }

    public Boolean getEsZonaComun() { return esZonaComun; }
    public void setEsZonaComun(Boolean esZonaComun) { this.esZonaComun = esZonaComun; }

    public Boolean getRequiereActivacionRuta() { return requiereActivacionRuta; }
    public void setRequiereActivacionRuta(Boolean requiereActivacionRuta) { this.requiereActivacionRuta = requiereActivacionRuta; }

    public CasoConvivencia getCasoConvivencia() { return casoConvivencia; }
    public void setCasoConvivencia(CasoConvivencia casoConvivencia) { this.casoConvivencia = casoConvivencia; }

    public String getVersionLibreEstudiante() { return versionLibreEstudiante; }
    public void setVersionLibreEstudiante(String versionLibreEstudiante) { this.versionLibreEstudiante = versionLibreEstudiante; }

    public LocalDateTime getFechaVersionEstudiante() { return fechaVersionEstudiante; }
    public void setFechaVersionEstudiante(LocalDateTime fechaVersionEstudiante) { this.fechaVersionEstudiante = fechaVersionEstudiante; }

    public Boolean getEnteradoAcudiente() { return enteradoAcudiente; }
    public void setEnteradoAcudiente(Boolean enteradoAcudiente) { this.enteradoAcudiente = enteradoAcudiente; }

    public Usuario getAcudienteUsuario() { return acudienteUsuario; }
    public void setAcudienteUsuario(Usuario acudienteUsuario) { this.acudienteUsuario = acudienteUsuario; }

    public LocalDateTime getFechaEnteradoAcudiente() { return fechaEnteradoAcudiente; }
    public void setFechaEnteradoAcudiente(LocalDateTime fechaEnteradoAcudiente) { this.fechaEnteradoAcudiente = fechaEnteradoAcudiente; }

    public Boolean getAnotacionFirmada() { return anotacionFirmada; }
    public void setAnotacionFirmada(Boolean anotacionFirmada) { this.anotacionFirmada = anotacionFirmada; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
