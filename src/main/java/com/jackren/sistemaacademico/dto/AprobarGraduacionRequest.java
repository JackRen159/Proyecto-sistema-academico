package com.jackren.sistemaacademico.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AprobarGraduacionRequest {

    @NotNull(message = "El año lectivo es obligatorio")
    private Long anioLectivoId;

    @NotBlank(message = "El número de acta general es obligatorio")
    @Size(max = 50, message = "El número de acta no puede superar 50 caracteres")
    private String numeroActaGeneral;

    private LocalDate fechaGraduacion;

    @Size(max = 20, message = "El libro no puede superar 20 caracteres")
    private String numeroLibro = "01";

    @Size(max = 20, message = "El folio no puede superar 20 caracteres")
    private String folioInicial = "001";

    public AprobarGraduacionRequest() {
    }

    public Long getAnioLectivoId() {
        return anioLectivoId;
    }

    public void setAnioLectivoId(Long anioLectivoId) {
        this.anioLectivoId = anioLectivoId;
    }

    public String getNumeroActaGeneral() {
        return numeroActaGeneral;
    }

    public void setNumeroActaGeneral(String numeroActaGeneral) {
        this.numeroActaGeneral = numeroActaGeneral;
    }

    public LocalDate getFechaGraduacion() {
        return fechaGraduacion;
    }

    public void setFechaGraduacion(LocalDate fechaGraduacion) {
        this.fechaGraduacion = fechaGraduacion;
    }

    public String getNumeroLibro() {
        return numeroLibro;
    }

    public void setNumeroLibro(String numeroLibro) {
        this.numeroLibro = numeroLibro;
    }

    public String getFolioInicial() {
        return folioInicial;
    }

    public void setFolioInicial(String folioInicial) {
        this.folioInicial = folioInicial;
    }
}
