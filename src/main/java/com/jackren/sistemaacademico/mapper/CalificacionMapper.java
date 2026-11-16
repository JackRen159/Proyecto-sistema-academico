package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class CalificacionMapper {

    public ConfiguracionEscalaSiee toEntity(ConfiguracionEscalaSieeRequest request, AnioLectivo anioLectivo) {
        if (request == null) {
            return null;
        }

        ConfiguracionEscalaSiee escala = new ConfiguracionEscalaSiee();
        escala.setAnioLectivo(anioLectivo);
        escala.setNotaMinima(request.getNotaMinima());
        escala.setNotaMaxima(request.getNotaMaxima());
        escala.setNotaAprobatoria(request.getNotaAprobatoria());
        escala.setEscalaBasicoMin(request.getEscalaBasicoMin());
        escala.setEscalaAltoMin(request.getEscalaAltoMin());
        escala.setEscalaSuperiorMin(request.getEscalaSuperiorMin());
        escala.setDecimalesRedondeo(request.getDecimalesRedondeo() != null ? request.getDecimalesRedondeo() : 1);
        escala.setDescripcion(request.getDescripcion());
        return escala;
    }

    public ConfiguracionEscalaSieeResponse toResponse(ConfiguracionEscalaSiee escala) {
        if (escala == null) {
            return null;
        }

        ConfiguracionEscalaSieeResponse response = new ConfiguracionEscalaSieeResponse();
        response.setId(escala.getId());
        if (escala.getAnioLectivo() != null) {
            response.setAnioLectivoId(escala.getAnioLectivo().getId());
            response.setAnioLectivo(escala.getAnioLectivo().getAnio());
        }
        response.setNotaMinima(escala.getNotaMinima());
        response.setNotaMaxima(escala.getNotaMaxima());
        response.setNotaAprobatoria(escala.getNotaAprobatoria());
        response.setEscalaBasicoMin(escala.getEscalaBasicoMin());
        response.setEscalaAltoMin(escala.getEscalaAltoMin());
        response.setEscalaSuperiorMin(escala.getEscalaSuperiorMin());
        response.setDecimalesRedondeo(escala.getDecimalesRedondeo());
        response.setDescripcion(escala.getDescripcion());
        response.setFechaCreacion(escala.getFechaCreacion());
        response.setFechaActualizacion(escala.getFechaActualizacion());
        return response;
    }

    public ActividadEvaluativa toEntity(ActividadEvaluativaRequest request,
                                        AsignacionAcademica asignacion,
                                        PeriodoAcademico periodo) {
        if (request == null) {
            return null;
        }

        ActividadEvaluativa actividad = new ActividadEvaluativa();
        actividad.setAsignacionAcademica(asignacion);
        actividad.setPeriodoAcademico(periodo);
        actividad.setNombre(request.getNombre());
        actividad.setDescripcion(request.getDescripcion());
        actividad.setPorcentaje(request.getPorcentaje());
        actividad.setFechaEvaluacion(request.getFechaEvaluacion() != null ? request.getFechaEvaluacion() : LocalDate.now());
        actividad.setActivo(true);
        return actividad;
    }

    public ActividadEvaluativaResponse toResponse(ActividadEvaluativa actividad) {
        if (actividad == null) {
            return null;
        }

        ActividadEvaluativaResponse response = new ActividadEvaluativaResponse();
        response.setId(actividad.getId());

        if (actividad.getAsignacionAcademica() != null) {
            AsignacionAcademica asig = actividad.getAsignacionAcademica();
            response.setAsignacionAcademicaId(asig.getId());
            if (asig.getProfesor() != null) {
                response.setProfesorNombreCompleto(asig.getProfesor().getNombres() + " " + asig.getProfesor().getApellidos());
            }
            if (asig.getGrupo() != null) {
                response.setGrupoId(asig.getGrupo().getId());
                response.setGrupoNombre(asig.getGrupo().getNombre());
            }
            if (asig.getAsignatura() != null) {
                response.setAsignaturaId(asig.getAsignatura().getId());
                response.setAsignaturaNombre(asig.getAsignatura().getNombre());
            }
        }

        if (actividad.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(actividad.getPeriodoAcademico().getId());
            response.setPeriodoNombre(actividad.getPeriodoAcademico().getNombre());
            response.setPeriodoNumero(actividad.getPeriodoAcademico().getNumero());
        }

        response.setNombre(actividad.getNombre());
        response.setDescripcion(actividad.getDescripcion());
        response.setPorcentaje(actividad.getPorcentaje());
        response.setFechaEvaluacion(actividad.getFechaEvaluacion());
        response.setActivo(actividad.getActivo());
        response.setFechaCreacion(actividad.getFechaCreacion());
        return response;
    }

    public CalificacionActividad toEntity(RegistroCalificacionRequest request,
                                          ActividadEvaluativa actividad,
                                          Matricula matricula) {
        if (request == null) {
            return null;
        }

        CalificacionActividad calificacion = new CalificacionActividad();
        calificacion.setActividad(actividad);
        calificacion.setMatricula(matricula);
        calificacion.setValor(request.getValor());
        calificacion.setObservaciones(request.getObservaciones());
        return calificacion;
    }

    public CalificacionActividadResponse toResponse(CalificacionActividad calificacion, ConfiguracionEscalaSiee escala) {
        if (calificacion == null) {
            return null;
        }

        CalificacionActividadResponse response = new CalificacionActividadResponse();
        response.setId(calificacion.getId());

        if (calificacion.getActividad() != null) {
            response.setActividadId(calificacion.getActividad().getId());
            response.setActividadNombre(calificacion.getActividad().getNombre());
            response.setActividadPorcentaje(calificacion.getActividad().getPorcentaje());
        }

        if (calificacion.getMatricula() != null) {
            Matricula mat = calificacion.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
                response.setEstudianteDocumento(mat.getEstudiante().getDocumento());
            }
        }

        response.setValor(calificacion.getValor());
        if (escala != null && calificacion.getValor() != null) {
            response.setDesempeno(escala.homologarDesempeno(calificacion.getValor()));
        }
        response.setObservaciones(calificacion.getObservaciones());
        response.setFechaRegistro(calificacion.getFechaRegistro());
        response.setFechaActualizacion(calificacion.getFechaActualizacion());
        return response;
    }

    public CalificacionPeriodoResponse toResponse(CalificacionPeriodo calificacion) {
        if (calificacion == null) {
            return null;
        }

        CalificacionPeriodoResponse response = new CalificacionPeriodoResponse();
        response.setId(calificacion.getId());

        if (calificacion.getMatricula() != null) {
            Matricula mat = calificacion.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
                response.setEstudianteDocumento(mat.getEstudiante().getDocumento());
            }
        }

        if (calificacion.getAsignatura() != null) {
            response.setAsignaturaId(calificacion.getAsignatura().getId());
            response.setAsignaturaNombre(calificacion.getAsignatura().getNombre());
        }

        if (calificacion.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(calificacion.getPeriodoAcademico().getId());
            response.setPeriodoNombre(calificacion.getPeriodoAcademico().getNombre());
            response.setPeriodoNumero(calificacion.getPeriodoAcademico().getNumero());
        }

        response.setNotaDefinitiva(calificacion.getNotaDefinitiva());
        response.setDesempeno(calificacion.getDesempeno());
        response.setAprobada(calificacion.getAprobada());
        response.setNotaRecuperacion(calificacion.getNotaRecuperacion());
        response.setNotaFinal(calificacion.getNotaFinal());
        response.setObservaciones(calificacion.getObservaciones());
        response.setFechaCalculo(calificacion.getFechaCalculo());
        response.setFechaActualizacion(calificacion.getFechaActualizacion());
        return response;
    }

    public AuditoriaCalificacionResponse toResponse(AuditoriaCalificacion auditoria) {
        if (auditoria == null) {
            return null;
        }

        AuditoriaCalificacionResponse response = new AuditoriaCalificacionResponse();
        response.setId(auditoria.getId());
        response.setTipoEntidad(auditoria.getTipoEntidad());
        response.setEntidadId(auditoria.getEntidadId());

        if (auditoria.getMatricula() != null) {
            Matricula mat = auditoria.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
            }
        }

        response.setValorAnterior(auditoria.getValorAnterior());
        response.setValorNuevo(auditoria.getValorNuevo());
        response.setMotivo(auditoria.getMotivo());
        if (auditoria.getUsuario() != null) {
            response.setUsuarioId(auditoria.getUsuario().getId());
        }
        response.setUsername(auditoria.getUsername());
        response.setFechaAuditoria(auditoria.getFechaAuditoria());
        return response;
    }
}
