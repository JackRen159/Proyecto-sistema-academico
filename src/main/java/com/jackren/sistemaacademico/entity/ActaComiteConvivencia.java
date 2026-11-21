package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(
    name = "actas_comite_convivencia",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_acta_comite_num", columnNames = {"numero_acta"})
    },
    indexes = {
        @Index(name = "idx_acta_comite_anio", columnList = "anio_lectivo_id")
    }
)
public class ActaComiteConvivencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Column(name = "numero_acta", nullable = false, length = 60, unique = true)
    private String numeroActa;

    @Column(name = "fecha_sesion", nullable = false)
    private LocalDate fechaSesion;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "lugar_sesion", nullable = false, length = 150)
    private String lugarSesion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_sesion", nullable = false, length = 25)
    private TipoSesionComiteConvivencia tipoSesion = TipoSesionComiteConvivencia.ORDINARIA;

    @Column(name = "integrantes_asistentes_json", nullable = false, columnDefinition = "TEXT")
    private String integrantesAsistentesJson;

    @Column(name = "impedimentos_declarados_json", columnDefinition = "TEXT")
    private String impedimentosDeclaradosJson;

    @Column(name = "casos_analizados_json", nullable = false, columnDefinition = "TEXT")
    private String casosAnalizadosJson;

    @Column(name = "acuerdos_compromisos_json", nullable = false, columnDefinition = "TEXT")
    private String acuerdosCompromisosJson;

    @Column(name = "acciones_prevencion_promocion", columnDefinition = "TEXT")
    private String accionesPrevencionPromocion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_secretario_id", nullable = false)
    private Usuario usuarioSecretario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_presidente_id", nullable = false)
    private Usuario usuarioPresidente;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public ActaComiteConvivencia() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AnioLectivo getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(AnioLectivo anioLectivo) { this.anioLectivo = anioLectivo; }

    public String getNumeroActa() { return numeroActa; }
    public void setNumeroActa(String numeroActa) { this.numeroActa = numeroActa; }

    public LocalDate getFechaSesion() { return fechaSesion; }
    public void setFechaSesion(LocalDate fechaSesion) { this.fechaSesion = fechaSesion; }

    public LocalTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalTime horaFin) { this.horaFin = horaFin; }

    public String getLugarSesion() { return lugarSesion; }
    public void setLugarSesion(String lugarSesion) { this.lugarSesion = lugarSesion; }

    public TipoSesionComiteConvivencia getTipoSesion() { return tipoSesion; }
    public void setTipoSesion(TipoSesionComiteConvivencia tipoSesion) { this.tipoSesion = tipoSesion; }

    public String getIntegrantesAsistentesJson() { return integrantesAsistentesJson; }
    public void setIntegrantesAsistentesJson(String integrantesAsistentesJson) { this.integrantesAsistentesJson = integrantesAsistentesJson; }

    public String getImpedimentosDeclaradosJson() { return impedimentosDeclaradosJson; }
    public void setImpedimentosDeclaradosJson(String impedimentosDeclaradosJson) { this.impedimentosDeclaradosJson = impedimentosDeclaradosJson; }

    public String getCasosAnalizadosJson() { return casosAnalizadosJson; }
    public void setCasosAnalizadosJson(String casosAnalizadosJson) { this.casosAnalizadosJson = casosAnalizadosJson; }

    public String getAcuerdosCompromisosJson() { return acuerdosCompromisosJson; }
    public void setAcuerdosCompromisosJson(String acuerdosCompromisosJson) { this.acuerdosCompromisosJson = acuerdosCompromisosJson; }

    public String getAccionesPrevencionPromocion() { return accionesPrevencionPromocion; }
    public void setAccionesPrevencionPromocion(String accionesPrevencionPromocion) { this.accionesPrevencionPromocion = accionesPrevencionPromocion; }

    public Usuario getUsuarioSecretario() { return usuarioSecretario; }
    public void setUsuarioSecretario(Usuario usuarioSecretario) { this.usuarioSecretario = usuarioSecretario; }

    public Usuario getUsuarioPresidente() { return usuarioPresidente; }
    public void setUsuarioPresidente(Usuario usuarioPresidente) { this.usuarioPresidente = usuarioPresidente; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
