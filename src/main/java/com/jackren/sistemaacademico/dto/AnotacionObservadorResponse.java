package com.jackren.sistemaacademico.dto;

import com.jackren.sistemaacademico.entity.TipoAnotacionObservador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record AnotacionObservadorResponse(
    Long id,
    Long estudianteId,
    String nombreEstudiante,
    Long periodoAcademicoId,
    Long autorUsuarioId,
    String nombreAutor,
    TipoAnotacionObservador tipoAnotacion,
    LocalDate fechaHecho,
    LocalTime horaHecho,
    String lugarHecho,
    String descripcionFactica,
    String accionesPedagogicas,
    Boolean esZonaComun,
    Boolean requiereActivacionRuta,
    Long casoConvivenciaId,
    String versionLibreEstudiante,
    LocalDateTime fechaVersionEstudiante,
    Boolean enteradoAcudiente,
    LocalDateTime fechaEnteradoAcudiente,
    Boolean anotacionFirmada,
    LocalDateTime fechaRegistro,
    List<AdendaObservadorResponse> adendas
) {}
