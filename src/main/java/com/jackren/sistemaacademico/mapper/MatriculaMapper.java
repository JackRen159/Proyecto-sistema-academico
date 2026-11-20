package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.HistorialNovedadResponse;
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
        matricula.setFechaEfectivaInicio(request.getFechaEfectivaInicio() != null ? request.getFechaEfectivaInicio() : matricula.getFechaMatricula());
        matricula.setInstitucionProcedencia(request.getInstitucionProcedencia());
        matricula.setSieeVersionId(request.getSieeVersionId());
        matricula.setManualConvivenciaVersionId(request.getManualConvivenciaVersionId());
        matricula.setFechaAceptacionReglamentos(request.getFechaAceptacionReglamentos());
        matricula.setEsRepitente(Boolean.TRUE.equals(request.getEsRepitente()));
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
        response.setFechaEfectivaInicio(matricula.getFechaEfectivaInicio());
        response.setFechaEfectivaRetiro(matricula.getFechaEfectivaRetiro());
        response.setInstitucionProcedencia(matricula.getInstitucionProcedencia());

        if (matricula.getAcudientePrincipal() != null) {
            response.setAcudientePrincipalId(matricula.getAcudientePrincipal().getId());
            response.setAcudientePrincipalNombre(matricula.getAcudientePrincipal().getNombres() + " " + matricula.getAcudientePrincipal().getApellidos());
            response.setAcudientePrincipalDocumento(matricula.getAcudientePrincipal().getDocumento());
        }

        response.setSieeVersionId(matricula.getSieeVersionId());
        response.setManualConvivenciaVersionId(matricula.getManualConvivenciaVersionId());
        response.setActaConsejoDirectivo(matricula.getActaConsejoDirectivo());
        response.setFechaActaConsejoDirectivo(matricula.getFechaActaConsejoDirectivo());
        response.setEsRepitente(matricula.getEsRepitente());
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

    public HistorialNovedadResponse toNovedadResponse(HistorialNovedadMatricula n) {
        if (n == null) {
            return null;
        }

        HistorialNovedadResponse response = new HistorialNovedadResponse();
        response.setId(n.getId());

        if (n.getMatricula() != null) {
            response.setMatriculaId(n.getMatricula().getId());
            response.setNumeroMatricula(n.getMatricula().getNumeroMatricula());
        }

        response.setTipoNovedad(n.getTipoNovedad());

        if (n.getGrupoOrigen() != null) {
            response.setGrupoOrigenId(n.getGrupoOrigen().getId());
            response.setGrupoOrigenNombre(n.getGrupoOrigen().getNombre());
        }

        if (n.getGrupoDestino() != null) {
            response.setGrupoDestinoId(n.getGrupoDestino().getId());
            response.setGrupoDestinoNombre(n.getGrupoDestino().getNombre());
        }

        if (n.getGradoOrigen() != null) {
            response.setGradoOrigenId(n.getGradoOrigen().getId());
            response.setGradoOrigenNombre(n.getGradoOrigen().getNombre());
        }

        if (n.getGradoDestino() != null) {
            response.setGradoDestinoId(n.getGradoDestino().getId());
            response.setGradoDestinoNombre(n.getGradoDestino().getNombre());
        }

        response.setFechaEfectiva(n.getFechaEfectiva());
        response.setMotivo(n.getMotivo());
        response.setSoporteDocumento(n.getSoporteDocumento());
        response.setNumeroActaResolucion(n.getNumeroActaResolucion());
        response.setFechaActaResolucion(n.getFechaActaResolucion());
        response.setSolicitante(n.getSolicitante());

        if (n.getUsuario() != null) {
            response.setUsuarioId(n.getUsuario().getId());
            response.setUsername(n.getUsuario().getUsername());
        }

        response.setFechaCreacion(n.getFechaCreacion());
        return response;
    }
}
