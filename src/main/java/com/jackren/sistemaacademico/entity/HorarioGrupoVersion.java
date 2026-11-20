package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "horarios_grupo_version",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_horario_grupo_version", columnNames = {"grupo_id", "numero_version"})
    },
    indexes = {
        @Index(name = "idx_horario_version_grupo", columnList = "grupo_id"),
        @Index(name = "idx_horario_version_estado", columnList = "estado"),
        @Index(name = "idx_horario_version_vigencia", columnList = "fecha_inicio_vigencia, fecha_fin_vigencia")
    }
)
public class HorarioGrupoVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion = 1;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 25)
    private EstadoHorarioVersion estado = EstadoHorarioVersion.BORRADOR;

    @Column(name = "fecha_inicio_vigencia")
    private LocalDate fechaInicioVigencia;

    @Column(name = "fecha_fin_vigencia")
    private LocalDate fechaFinVigencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_publicador_id")
    private Usuario usuarioPublicador;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Version
    @Column(nullable = false)
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @OneToMany(mappedBy = "horarioVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HorarioBloque> bloques = new ArrayList<>();

    public HorarioGrupoVersion() {
    }

    public HorarioGrupoVersion(Grupo grupo, Integer numeroVersion) {
        this.grupo = grupo;
        this.numeroVersion = numeroVersion != null ? numeroVersion : 1;
        this.estado = EstadoHorarioVersion.BORRADOR;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public Integer getNumeroVersion() {
        return numeroVersion;
    }

    public void setNumeroVersion(Integer numeroVersion) {
        this.numeroVersion = numeroVersion;
    }

    public EstadoHorarioVersion getEstado() {
        return estado;
    }

    public void setEstado(EstadoHorarioVersion estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicioVigencia() {
        return fechaInicioVigencia;
    }

    public void setFechaInicioVigencia(LocalDate fechaInicioVigencia) {
        this.fechaInicioVigencia = fechaInicioVigencia;
    }

    public LocalDate getFechaFinVigencia() {
        return fechaFinVigencia;
    }

    public void setFechaFinVigencia(LocalDate fechaFinVigencia) {
        this.fechaFinVigencia = fechaFinVigencia;
    }

    public Usuario getUsuarioPublicador() {
        return usuarioPublicador;
    }

    public void setUsuarioPublicador(Usuario usuarioPublicador) {
        this.usuarioPublicador = usuarioPublicador;
    }

    public LocalDateTime getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(LocalDateTime fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
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

    public List<HorarioBloque> getBloques() {
        return bloques;
    }

    public void setBloques(List<HorarioBloque> bloques) {
        this.bloques = bloques;
    }

    public void addBloque(HorarioBloque bloque) {
        bloques.add(bloque);
        bloque.setHorarioVersion(this);
    }
}
