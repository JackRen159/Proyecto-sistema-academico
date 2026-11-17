package com.jackren.sistemaacademico.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "consecutivos_documentos",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_consecutivo_anio_tipo", columnNames = {"anio_lectivo_id", "tipo_documento"})
    },
    indexes = {
        @Index(name = "idx_consecutivos_anio", columnList = "anio_lectivo_id")
    }
)
public class ConsecutivoDocumento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "anio_lectivo_id", nullable = false)
    private AnioLectivo anioLectivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 50)
    private TipoDocumentoAcademico tipoDocumento;

    @Column(length = 20, nullable = false)
    private String prefijo = "";

    @Column(name = "ultimo_numero", nullable = false)
    private Long ultimoNumero = 0L;

    @Column(name = "longitud_relleno", nullable = false)
    private Integer longitudRelleno = 5;

    @Column(name = "texto_pie_pagina", length = 500)
    private String textoPiePagina;

    @CreationTimestamp
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @UpdateTimestamp
    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion;

    public ConsecutivoDocumento() {
    }

    public ConsecutivoDocumento(AnioLectivo anioLectivo,
                                TipoDocumentoAcademico tipoDocumento,
                                String prefijo,
                                Long ultimoNumero,
                                Integer longitudRelleno,
                                String textoPiePagina) {
        this.anioLectivo = anioLectivo;
        this.tipoDocumento = tipoDocumento;
        this.prefijo = prefijo != null ? prefijo : "";
        this.ultimoNumero = ultimoNumero != null ? ultimoNumero : 0L;
        this.longitudRelleno = longitudRelleno != null ? longitudRelleno : 5;
        this.textoPiePagina = textoPiePagina;
    }

    public String incrementarYFormatear(int anio) {
        this.ultimoNumero = (this.ultimoNumero == null ? 0L : this.ultimoNumero) + 1L;
        String formato = "%0" + (this.longitudRelleno != null && this.longitudRelleno > 0 ? this.longitudRelleno : 5) + "d";
        String numeroFormateado = String.format(formato, this.ultimoNumero);
        String pref = this.prefijo != null ? this.prefijo.trim() : "";
        if (!pref.isEmpty() && !pref.endsWith("-")) {
            pref = pref + "-";
        }
        return pref + anio + "-" + numeroFormateado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AnioLectivo getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(AnioLectivo anioLectivo) {
        this.anioLectivo = anioLectivo;
    }

    public TipoDocumentoAcademico getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(TipoDocumentoAcademico tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPrefijo() {
        return prefijo;
    }

    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo != null ? prefijo : "";
    }

    public Long getUltimoNumero() {
        return ultimoNumero;
    }

    public void setUltimoNumero(Long ultimoNumero) {
        this.ultimoNumero = ultimoNumero;
    }

    public Integer getLongitudRelleno() {
        return longitudRelleno;
    }

    public void setLongitudRelleno(Integer longitudRelleno) {
        this.longitudRelleno = longitudRelleno;
    }

    public String getTextoPiePagina() {
        return textoPiePagina;
    }

    public void setTextoPiePagina(String textoPiePagina) {
        this.textoPiePagina = textoPiePagina;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}
