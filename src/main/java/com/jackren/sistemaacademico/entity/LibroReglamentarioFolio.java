package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "libros_reglamentarios_folios",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_libro_folio", columnNames = {"anio_lectivo_id", "tipo_libro", "numero_tomo", "numero_folio"})
    },
    indexes = {
        @Index(name = "idx_libro_folio_anio_tipo", columnList = "anio_lectivo_id, tipo_libro")
    }
)
public class LibroReglamentarioFolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_libro", nullable = false, length = 45)
    private TipoLibroReglamentario tipoLibro;

    @Column(name = "numero_tomo", nullable = false)
    private Integer numeroTomo = 1;

    @Column(name = "numero_folio", nullable = false)
    private Integer numeroFolio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @Column(name = "codigo_documento_asociado", length = 60)
    private String codigoDocumentoAsociado;

    @Column(name = "resumen_registro_json", nullable = false, columnDefinition = "TEXT")
    private String resumenRegistroJson;

    @Column(name = "fecha_asiento", nullable = false)
    private LocalDate fechaAsiento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "secretario_usuario_id", nullable = false)
    private Usuario secretarioUsuario;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public LibroReglamentarioFolio() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AnioLectivo getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(AnioLectivo anioLectivo) { this.anioLectivo = anioLectivo; }

    public TipoLibroReglamentario getTipoLibro() { return tipoLibro; }
    public void setTipoLibro(TipoLibroReglamentario tipoLibro) { this.tipoLibro = tipoLibro; }

    public Integer getNumeroTomo() { return numeroTomo; }
    public void setNumeroTomo(Integer numeroTomo) { this.numeroTomo = numeroTomo; }

    public Integer getNumeroFolio() { return numeroFolio; }
    public void setNumeroFolio(Integer numeroFolio) { this.numeroFolio = numeroFolio; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public String getCodigoDocumentoAsociado() { return codigoDocumentoAsociado; }
    public void setCodigoDocumentoAsociado(String codigoDocumentoAsociado) { this.codigoDocumentoAsociado = codigoDocumentoAsociado; }

    public String getResumenRegistroJson() { return resumenRegistroJson; }
    public void setResumenRegistroJson(String resumenRegistroJson) { this.resumenRegistroJson = resumenRegistroJson; }

    public LocalDate getFechaAsiento() { return fechaAsiento; }
    public void setFechaAsiento(LocalDate fechaAsiento) { this.fechaAsiento = fechaAsiento; }

    public Usuario getSecretarioUsuario() { return secretarioUsuario; }
    public void setSecretarioUsuario(Usuario secretarioUsuario) { this.secretarioUsuario = secretarioUsuario; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
