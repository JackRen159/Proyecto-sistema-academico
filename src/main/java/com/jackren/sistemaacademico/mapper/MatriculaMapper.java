package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.HistorialTrasladoResponse;
import com.jackren.sistemaacademico.dto.MatriculaRequest;
import com.jackren.sistemaacademico.dto.MatriculaResponse;
import com.jackren.sistemaacademico.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class MatriculaMapper {

    public Matricula toEntity(MatriculaRequest request, Estudiante estudiante, Grupo grupo, String numeroMatricula) {
        if (request == null) {
            return null;
        }

        Matricula matricula = new Matricula();
        matricula.setNumeroMatricula(numeroMatricula);
        matricula.setEstudiante(estudiante);
        matricula.setGrupo(grupo);
        matricula.setAnioLectivo(grupo != null ? grupo.getAnioLectivo() : null);
        matricula.setEstado(EstadoMatricula.MATRICULADA);
        matricula.setTipoMatricula(request.getTipoMatricula() != null ? request.getTipoMatricula() : TipoMatricula.REGULAR);
        matricula.setFolio(request.getFolio());
        matricula.setFechaMatricula(request.getFechaMatricula() != null ? request.getFechaMatricula() : LocalDate.now());
        matricula.setObservaciones(request.getObservaciones());
        matricula.setActivo(true);
        return matricula;
    }

    public MatriculaResponse toResponse(Matricula matricula) {
        if (matricula == null) {
            return null;
        }

        MatriculaResponse response = new MatriculaResponse();
        response.setId(matricula.getId());
        response.setNumeroMatricula(matricula.getNumeroMatricula());

        if (matricula.getEstudiante() != null) {
            response.setEstudianteId(matricula.getEstudiante().getId());
            response.setEstudianteNombreCompleto(matricula.getEstudiante().getNombres() + " " + matricula.getEstudiante().getApellidos());
            response.setEstudianteDocumento(matricula.getEstudiante().getDocumento());
        }

        if (matricula.getGrupo() != null) {
            response.setGrupoId(matricula.getGrupo().getId());
            response.setGrupoNombre(matricula.getGrupo().getNombre());
            response.setJornada(matricula.getGrupo().getJornada());

            if (matricula.getGrupo().getGrado() != null) {
                response.setGradoId(matricula.getGrupo().getGrado().getId());
                response.setGradoCodigo(matricula.getGrupo().getGrado().getCodigo());
                response.setGradoNombre(matricula.getGrupo().getGrado().getNombre());
            }
        }

        if (matricula.getAnioLectivo() != null) {
            response.setAnioLectivoId(matricula.getAnioLectivo().getId());
            response.setAnioLectivo(matricula.getAnioLectivo().getAnio());
        }

        response.setEstado(matricula.getEstado());
        response.setTipoMatricula(matricula.getTipoMatricula());
        response.setFolio(matricula.getFolio());
        response.setFechaMatricula(matricula.getFechaMatricula());
        response.setObservaciones(matricula.getObservaciones());
        response.setActivo(matricula.getActivo());
        response.setFechaCreacion(matricula.getFechaCreacion());
        response.setFechaActualizacion(matricula.getFechaActualizacion());

        return response;
    }

    public HistorialTrasladoResponse toHistorialResponse(HistorialTrasladoMatricula historial) {
        if (historial == null) {
            return null;
        }

        HistorialTrasladoResponse response = new HistorialTrasladoResponse();
        response.setId(historial.getId());

        if (historial.getMatricula() != null) {
            response.setMatriculaId(historial.getMatricula().getId());
            response.setNumeroMatricula(historial.getMatricula().getNumeroMatricula());
        }

        if (historial.getGrupoOrigen() != null) {
            response.setGrupoOrigenId(historial.getGrupoOrigen().getId());
            response.setGrupoOrigenNombre(historial.getGrupoOrigen().getNombre());
        }

        if (historial.getGrupoDestino() != null) {
            response.setGrupoDestinoId(historial.getGrupoDestino().getId());
            response.setGrupoDestinoNombre(historial.getGrupoDestino().getNombre());
        }

        response.setMotivo(historial.getMotivo());

        if (historial.getUsuario() != null) {
            response.setUsuarioId(historial.getUsuario().getId());
            response.setUsername(historial.getUsuario().getUsername());
        }

        response.setFechaTraslado(historial.getFechaTraslado());
        return response;
    }
}
