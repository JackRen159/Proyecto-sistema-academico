package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class AsistenciaMapper {

    public ConfiguracionAlertaAsistencia toEntity(ConfiguracionAlertaAsistenciaRequest request, AnioLectivo anioLectivo) {
        if (request == null) {
            return null;
        }

        ConfiguracionAlertaAsistencia config = new ConfiguracionAlertaAsistencia();
        config.setAnioLectivo(anioLectivo);
        config.setPorcentajeAlertaAmarilla(request.getPorcentajeAlertaAmarilla());
        config.setPorcentajeAlertaRoja(request.getPorcentajeAlertaRoja());
        config.setDescripcion(request.getDescripcion());
        return config;
    }

    public ConfiguracionAlertaAsistenciaResponse toResponse(ConfiguracionAlertaAsistencia config) {
        if (config == null) {
            return null;
        }

        ConfiguracionAlertaAsistenciaResponse response = new ConfiguracionAlertaAsistenciaResponse();
        response.setId(config.getId());
        if (config.getAnioLectivo() != null) {
            response.setAnioLectivoId(config.getAnioLectivo().getId());
            response.setAnioLectivo(config.getAnioLectivo().getAnio());
        }
        response.setPorcentajeAlertaAmarilla(config.getPorcentajeAlertaAmarilla());
        response.setPorcentajeAlertaRoja(config.getPorcentajeAlertaRoja());
        response.setDescripcion(config.getDescripcion());
        response.setFechaCreacion(config.getFechaCreacion());
        response.setFechaActualizacion(config.getFechaActualizacion());
        return response;
    }

    public SesionAsistencia toEntity(SesionAsistenciaRequest request,
                                     AsignacionAcademica asignacion,
                                     Grupo grupo,
                                     PeriodoAcademico periodo,
                                     Usuario usuario) {
        if (request == null) {
            return null;
        }

        SesionAsistencia sesion = new SesionAsistencia();
        sesion.setAsignacionAcademica(asignacion);
        sesion.setGrupo(grupo);
        sesion.setPeriodoAcademico(periodo);
        sesion.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        sesion.setBloqueHorario(request.getBloqueHorario() != null ? request.getBloqueHorario() : 1);
        sesion.setTipoRegistro(request.getTipoRegistro() != null ? request.getTipoRegistro() : TipoRegistroAsistencia.SESION_ASIGNATURA);
        sesion.setTema(request.getTema());
        sesion.setObservaciones(request.getObservaciones());
        sesion.setUsuarioCreador(usuario);
        return sesion;
    }

    public SesionAsistenciaResponse toResponse(SesionAsistencia sesion) {
        if (sesion == null) {
            return null;
        }

        SesionAsistenciaResponse response = new SesionAsistenciaResponse();
        response.setId(sesion.getId());

        if (sesion.getAsignacionAcademica() != null) {
            AsignacionAcademica asig = sesion.getAsignacionAcademica();
            response.setAsignacionAcademicaId(asig.getId());
            if (asig.getProfesor() != null) {
                response.setProfesorNombreCompleto(asig.getProfesor().getNombres() + " " + asig.getProfesor().getApellidos());
            }
            if (asig.getAsignatura() != null) {
                response.setAsignaturaNombre(asig.getAsignatura().getNombre());
            }
        }

        if (sesion.getGrupo() != null) {
            response.setGrupoId(sesion.getGrupo().getId());
            response.setGrupoNombre(sesion.getGrupo().getNombre());
        }

        if (sesion.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(sesion.getPeriodoAcademico().getId());
            response.setPeriodoNombre(sesion.getPeriodoAcademico().getNombre());
            response.setPeriodoNumero(sesion.getPeriodoAcademico().getNumero());
        }

        response.setFecha(sesion.getFecha());
        response.setBloqueHorario(sesion.getBloqueHorario());
        response.setTipoRegistro(sesion.getTipoRegistro());
        response.setTema(sesion.getTema());
        response.setObservaciones(sesion.getObservaciones());
        if (sesion.getUsuarioCreador() != null) {
            response.setUsuarioCreadorId(sesion.getUsuarioCreador().getId());
        }
        response.setFechaCreacion(sesion.getFechaCreacion());
        return response;
    }

    public RegistroAsistencia toEntity(RegistroAsistenciaItemRequest request,
                                       SesionAsistencia sesion,
                                       Matricula matricula) {
        if (request == null) {
            return null;
        }

        RegistroAsistencia registro = new RegistroAsistencia();
        registro.setSesionAsistencia(sesion);
        registro.setMatricula(matricula);
        registro.setEstado(request.getEstado() != null ? request.getEstado() : EstadoAsistencia.PRESENTE);
        registro.setMinutosRetardo(request.getMinutosRetardo() != null ? request.getMinutosRetardo() : 0);
        registro.setObservaciones(request.getObservaciones());
        return registro;
    }

    public RegistroAsistenciaResponse toResponse(RegistroAsistencia registro, JustificacionAsistencia justificacion) {
        if (registro == null) {
            return null;
        }

        RegistroAsistenciaResponse response = new RegistroAsistenciaResponse();
        response.setId(registro.getId());

        if (registro.getSesionAsistencia() != null) {
            response.setSesionAsistenciaId(registro.getSesionAsistencia().getId());
            response.setFechaSesion(registro.getSesionAsistencia().getFecha());
            response.setBloqueHorario(registro.getSesionAsistencia().getBloqueHorario());
        }

        if (registro.getMatricula() != null) {
            Matricula mat = registro.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
                response.setEstudianteDocumento(mat.getEstudiante().getDocumento());
            }
        }

        response.setEstado(registro.getEstado());
        response.setMinutosRetardo(registro.getMinutosRetardo());
        response.setObservaciones(registro.getObservaciones());

        if (justificacion != null) {
            response.setJustificado(true);
            response.setMotivoJustificacion(justificacion.getMotivo());
        } else {
            response.setJustificado(registro.getEstado() == EstadoAsistencia.AUSENTE_JUSTIFICADA);
        }

        response.setFechaCreacion(registro.getFechaCreacion());
        response.setFechaActualizacion(registro.getFechaActualizacion());
        return response;
    }

    public JustificacionAsistenciaResponse toResponse(JustificacionAsistencia justificacion) {
        if (justificacion == null) {
            return null;
        }

        JustificacionAsistenciaResponse response = new JustificacionAsistenciaResponse();
        response.setId(justificacion.getId());
        if (justificacion.getRegistroAsistencia() != null) {
            response.setRegistroAsistenciaId(justificacion.getRegistroAsistencia().getId());
            if (justificacion.getRegistroAsistencia().getMatricula() != null) {
                Matricula mat = justificacion.getRegistroAsistencia().getMatricula();
                response.setMatriculaId(mat.getId());
                response.setNumeroMatricula(mat.getNumeroMatricula());
                if (mat.getEstudiante() != null) {
                    response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
                }
            }
        }
        response.setMotivo(justificacion.getMotivo());
        response.setNumeroRadicadoSoporte(justificacion.getNumeroRadicadoSoporte());
        response.setFechaRadicacion(justificacion.getFechaRadicacion());
        if (justificacion.getUsuarioAutorizador() != null) {
            response.setUsuarioAutorizadorId(justificacion.getUsuarioAutorizador().getId());
        }
        response.setUsername(justificacion.getUsername());
        response.setObservaciones(justificacion.getObservaciones());
        response.setFechaCreacion(justificacion.getFechaCreacion());
        return response;
    }
}
