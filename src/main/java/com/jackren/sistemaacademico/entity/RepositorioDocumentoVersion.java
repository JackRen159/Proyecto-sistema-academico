package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "repositorio_documentos_versiones",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_rep_doc_version", columnNames = {"documento_id", "numero_version"})
    },
    indexes = {
        @Index(name = "idx_rep_ver_doc", columnList = "documento_id")
    }
)
public class RepositorioDocumentoVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "documento_id", nullable = false)
    private RepositorioDocumento documento;

    @Column(name = "numero_version", nullable = false)
    private Integer numeroVersion;

    @Column(name = "hash_sha256", nullable = false, length = 64)
    private String hashSha256;

    @Column(name = "ruta_almacenamiento", nullable = false, length = 255)
    private String rutaAlmacenamiento;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "mime_type", nullable = false, length = 100)
    private String mimeType;

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @Column(name = "motivo_version", nullable = false, columnDefinition = "TEXT")
    private String motivoVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_autor_id", nullable = false)
    private Usuario usuarioAutor;

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    public RepositorioDocumentoVersion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public RepositorioDocumento getDocumento() { return documento; }
    public void setDocumento(RepositorioDocumento documento) { this.documento = documento; }

    public Integer getNumeroVersion() { return numeroVersion; }
    public void setNumeroVersion(Integer numeroVersion) { this.numeroVersion = numeroVersion; }

    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String hashSha256) { this.hashSha256 = hashSha256; }

    public String getRutaAlmacenamiento() { return rutaAlmacenamiento; }
    public void setRutaAlmacenamiento(String rutaAlmacenamiento) { this.rutaAlmacenamiento = rutaAlmacenamiento; }

    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }

    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }

    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long tamanoBytes) { this.tamanoBytes = tamanoBytes; }

    public String getMotivoVersion() { return motivoVersion; }
    public void setMotivoVersion(String motivoVersion) { this.motivoVersion = motivoVersion; }

    public Usuario getUsuarioAutor() { return usuarioAutor; }
    public void setUsuarioAutor(Usuario usuarioAutor) { this.usuarioAutor = usuarioAutor; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
