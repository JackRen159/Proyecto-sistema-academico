package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "repositorio_documentos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_rep_doc_codigo", columnNames = {"codigo_documento"})
    },
    indexes = {
        @Index(name = "idx_rep_doc_codigo", columnList = "codigo_documento"),
        @Index(name = "idx_rep_doc_est", columnList = "estudiante_id"),
        @Index(name = "idx_rep_doc_tipo", columnList = "tipo_documento"),
        @Index(name = "idx_rep_doc_conf", columnList = "nivel_confidencialidad"),
        @Index(name = "idx_rep_doc_estado", columnList = "estado_ciclo_vida")
    }
)
public class RepositorioDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo_documento", nullable = false, length = 60, unique = true)
    private String codigoDocumento;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_documento", nullable = false, length = 50)
    private String tipoDocumento;

    @Enumerated(EnumType.STRING)
    @Column(name = "nivel_confidencialidad", nullable = false, length = 45)
    private NivelConfidencialidadDocumental nivelConfidencialidad = NivelConfidencialidadDocumental.ACADEMICO_ESTANDAR;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_ciclo_vida", nullable = false, length = 40)
    private EstadoCicloVidaDocumental estadoCicloVida = EstadoCicloVidaDocumental.EN_REVISION;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matricula_id")
    private Matricula matricula;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anio_lectivo_id")
    private AnioLectivo anioLectivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trd_id")
    private TablaRetencionDocumental trd;

    @Column(name = "version_actual", nullable = false)
    private Integer versionActual = 1;

    @Column(name = "hash_sha256_actual", nullable = false, length = 64)
    private String hashSha256Actual;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 255)
    private String rutaAlmacenamiento;

    @Column(name = "nombre_archivo_original", nullable = false, length = 255)
    private String nombreArchivoOriginal;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType = "application/pdf";

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_creador_id", nullable = false)
    private Usuario usuarioCreador;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_anulador_id")
    private Usuario usuarioAnulador;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public RepositorioDocumento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigoDocumento() { return codigoDocumento; }
    public void setCodigoDocumento(String codigoDocumento) { this.codigoDocumento = codigoDocumento; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public NivelConfidencialidadDocumental getNivelConfidencialidad() { return nivelConfidencialidad; }
    public void setNivelConfidencialidad(NivelConfidencialidadDocumental nivelConfidencialidad) { this.nivelConfidencialidad = nivelConfidencialidad; }

    public EstadoCicloVidaDocumental getEstadoCicloVida() { return estadoCicloVida; }
    public void setEstadoCicloVida(EstadoCicloVidaDocumental estadoCicloVida) { this.estadoCicloVida = estadoCicloVida; }

    public Estudiante getEstudiante() { return estudiante; }
    public void setEstudiante(Estudiante estudiante) { this.estudiante = estudiante; }

    public Matricula getMatricula() { return matricula; }
    public void setMatricula(Matricula matricula) { this.matricula = matricula; }

    public AnioLectivo getAnioLectivo() { return anioLectivo; }
    public void setAnioLectivo(AnioLectivo anioLectivo) { this.anioLectivo = anioLectivo; }

    public TablaRetencionDocumental getTrd() { return trd; }
    public void setTrd(TablaRetencionDocumental trd) { this.trd = trd; }

    public Integer getVersionActual() { return versionActual; }
    public void setVersionActual(Integer versionActual) { this.versionActual = versionActual; }

    public String getHashSha256Actual() { return hashSha256Actual; }
    public void setHashSha256Actual(String hashSha256Actual) { this.hashSha256Actual = hashSha256Actual; }

    public String getRutaAlmacenamiento() { return rutaAlmacenamiento; }
    public void setRutaAlmacenamiento(String rutaAlmacenamiento) { this.rutaAlmacenamiento = rutaAlmacenamiento; }

    public String getNombreArchivoOriginal() { return nombreArchivoOriginal; }
    public void setNombreArchivoOriginal(String nombreArchivoOriginal) { this.nombreArchivoOriginal = nombreArchivoOriginal; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public Usuario getUsuarioCreador() { return usuarioCreador; }
    public void setUsuarioCreador(Usuario usuarioCreador) { this.usuarioCreador = usuarioCreador; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getMotivoAnulacion() { return motivoAnulacion; }
    public void setMotivoAnulacion(String motivoAnulacion) { this.motivoAnulacion = motivoAnulacion; }

    public Usuario getUsuarioAnulador() { return usuarioAnulador; }
    public void setUsuarioAnulador(Usuario usuarioAnulador) { this.usuarioAnulador = usuarioAnulador; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
