package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.AsignacionAcademicaRequest;
import com.jackren.sistemaacademico.dto.AsignacionAcademicaResponse;
import com.jackren.sistemaacademico.entity.AsignacionAcademica;
import com.jackren.sistemaacademico.entity.Asignatura;
import com.jackren.sistemaacademico.entity.Grupo;
import com.jackren.sistemaacademico.entity.Profesor;
import org.springframework.stereotype.Component;

@Component
public class AsignacionAcademicaMapper {

    public AsignacionAcademica toEntity(AsignacionAcademicaRequest request, Profesor profesor, Grupo grupo, Asignatura asignatura) {
        if (request == null) {
            return null;
        }

        AsignacionAcademica asignacion = new AsignacionAcademica();
        asignacion.setProfesor(profesor);
        asignacion.setGrupo(grupo);
        asignacion.setAsignatura(asignatura);
        asignacion.setIntensidadHorariaSemanal(request.getIntensidadHorariaSemanal() != null ? request.getIntensidadHorariaSemanal() : 1);
        asignacion.setEsDirectorGrupo(request.getEsDirectorGrupo() != null ? request.getEsDirectorGrupo() : false);
        asignacion.setActivo(true);
        return asignacion;
    }

    public void updateEntityFromRequest(AsignacionAcademica asignacion, AsignacionAcademicaRequest request, Profesor profesor, Grupo grupo, Asignatura asignatura) {
        if (asignacion == null || request == null) {
            return;
        }

        asignacion.setProfesor(profesor);
        asignacion.setGrupo(grupo);
        asignacion.setAsignatura(asignatura);
        if (request.getIntensidadHorariaSemanal() != null) {
            asignacion.setIntensidadHorariaSemanal(request.getIntensidadHorariaSemanal());
        }
        if (request.getEsDirectorGrupo() != null) {
            asignacion.setEsDirectorGrupo(request.getEsDirectorGrupo());
        }
    }

    public AsignacionAcademicaResponse toResponse(AsignacionAcademica asignacion) {
        if (asignacion == null) {
            return null;
        }

        AsignacionAcademicaResponse response = new AsignacionAcademicaResponse();
        response.setId(asignacion.getId());

        if (asignacion.getProfesor() != null) {
            response.setProfesorId(asignacion.getProfesor().getId());
            response.setProfesorNombreCompleto(asignacion.getProfesor().getNombres() + " " + asignacion.getProfesor().getApellidos());
            response.setProfesorDocumento(asignacion.getProfesor().getDocumento());
        }

        if (asignacion.getGrupo() != null) {
            response.setGrupoId(asignacion.getGrupo().getId());
            response.setGrupoNombre(asignacion.getGrupo().getNombre());
            if (asignacion.getGrupo().getGrado() != null) {
                response.setGradoNombre(asignacion.getGrupo().getGrado().getNombre());
            }
            if (asignacion.getGrupo().getAnioLectivo() != null) {
                response.setAnioLectivoId(asignacion.getGrupo().getAnioLectivo().getId());
                response.setAnioLectivo(asignacion.getGrupo().getAnioLectivo().getAnio());
            }
        }

        if (asignacion.getAsignatura() != null) {
            response.setAsignaturaId(asignacion.getAsignatura().getId());
            response.setAsignaturaNombre(asignacion.getAsignatura().getNombre());
            response.setAsignaturaCodigo(asignacion.getAsignatura().getCodigo());
        }

        response.setIntensidadHorariaSemanal(asignacion.getIntensidadHorariaSemanal());
        response.setEsDirectorGrupo(asignacion.getEsDirectorGrupo());
        response.setActivo(asignacion.getActivo());
        response.setFechaCreacion(asignacion.getFechaCreacion());
        response.setFechaActualizacion(asignacion.getFechaActualizacion());

        return response;
    }
}
