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
                                        PeriodoAcademico periodo,
                                        ConfiguracionSieeVersion sieeVersion) {
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
        if (request.getCategoriaEvaluativa() != null) {
            actividad.setCategoriaEvaluativa(request.getCategoriaEvaluativa());
        }
        if (request.getTipoComponente() != null) {
            actividad.setTipoComponente(request.getTipoComponente());
        }
        if (request.getModalidad() != null) {
            actividad.setModalidad(request.getModalidad());
        }
        if (request.getEstado() != null) {
            actividad.setEstado(request.getEstado());
        }
        actividad.setSieeVersion(sieeVersion);
        actividad.setEsAdaptacionPiar(Boolean.TRUE.equals(request.getEsAdaptacionPiar()));
        actividad.setCriteriosAdaptadosPiar(request.getCriteriosAdaptadosPiar());
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
        response.setCategoriaEvaluativa(actividad.getCategoriaEvaluativa());
        response.setTipoComponente(actividad.getTipoComponente());
        response.setModalidad(actividad.getModalidad());
        response.setEstado(actividad.getEstado());
        if (actividad.getSieeVersion() != null) {
            response.setSieeVersionId(actividad.getSieeVersion().getId());
        }
        response.setEsAdaptacionPiar(actividad.getEsAdaptacionPiar());
        response.setCriteriosAdaptadosPiar(actividad.getCriteriosAdaptadosPiar());
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
        calificacion.setEstado(request.getEstado() != null ? request.getEstado() : EstadoCalificacionActividad.CALIFICADA);
        calificacion.setFechaSupletoria(request.getFechaSupletoria());
        calificacion.setValorCualitativo(request.getValorCualitativo());
        calificacion.setObservacionDescriptiva(request.getObservacionDescriptiva());
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
        response.setEstado(calificacion.getEstado());
        response.setFechaSupletoria(calificacion.getFechaSupletoria());
        response.setValorCualitativo(calificacion.getValorCualitativo());
        response.setObservacionDescriptiva(calificacion.getObservacionDescriptiva());

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
        response.setOrigen(calificacion.getOrigen());
        response.setObservaciones(calificacion.getObservaciones());
        response.setFechaCalculo(calificacion.getFechaCalculo());
        response.setFechaActualizacion(calificacion.getFechaActualizacion());
        return response;
    }

    public CalificacionAreaPeriodoResponse toResponse(CalificacionAreaPeriodo califArea) {
        if (califArea == null) {
            return null;
        }

        CalificacionAreaPeriodoResponse response = new CalificacionAreaPeriodoResponse();
        response.setId(califArea.getId());

        if (califArea.getMatricula() != null) {
            Matricula mat = califArea.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
                response.setEstudianteDocumento(mat.getEstudiante().getDocumento());
            }
        }

        if (califArea.getArea() != null) {
            response.setAreaId(califArea.getArea().getId());
            response.setAreaNombre(califArea.getArea().getNombre());
        }

        if (califArea.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(califArea.getPeriodoAcademico().getId());
            response.setPeriodoNombre(califArea.getPeriodoAcademico().getNombre());
            response.setPeriodoNumero(califArea.getPeriodoAcademico().getNumero());
        }

        response.setNotaDefinitiva(califArea.getNotaDefinitiva());
        response.setDesempeno(califArea.getDesempeno());
        response.setAprobada(califArea.getAprobada());
        response.setOrigen(califArea.getOrigen());
        response.setFechaCalculo(califArea.getFechaCalculo());
        return response;
    }

    public ActaNivelacionResponse toResponse(ActaNivelacion acta) {
        if (acta == null) {
            return null;
        }

        ActaNivelacionResponse response = new ActaNivelacionResponse();
        response.setId(acta.getId());
        response.setNumeroActa(acta.getNumeroActa());

        if (acta.getCalificacionPeriodo() != null) {
            response.setCalificacionPeriodoId(acta.getCalificacionPeriodo().getId());
        }

        if (acta.getMatricula() != null) {
            Matricula mat = acta.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
            }
        }

        if (acta.getAsignatura() != null) {
            response.setAsignaturaId(acta.getAsignatura().getId());
            response.setAsignaturaNombre(acta.getAsignatura().getNombre());
        }

        if (acta.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(acta.getPeriodoAcademico().getId());
            response.setPeriodoNombre(acta.getPeriodoAcademico().getNombre());
        }

        if (acta.getDocente() != null) {
            response.setDocenteId(acta.getDocente().getId());
            response.setDocenteNombreCompleto(acta.getDocente().getNombres() + " " + acta.getDocente().getApellidos());
        }

        response.setFechaSustentacion(acta.getFechaSustentacion());
        response.setAsistencia(acta.getAsistencia());
        response.setNotaOriginal(acta.getNotaOriginal());
        response.setNotaObtenida(acta.getNotaObtenida());
        response.setFormulaAplicada(acta.getFormulaAplicada());
        response.setNotaFinalResultante(acta.getNotaFinalResultante());
        response.setDesempenoResultante(acta.getDesempenoResultante());
        response.setEstrategiaPedagogica(acta.getEstrategiaPedagogica());
        response.setEvidenciasSoporte(acta.getEvidenciasSoporte());
        response.setObservaciones(acta.getObservaciones());
        response.setFechaRegistro(acta.getFechaRegistro());
        return response;
    }

    public ReclamacionCalificacionResponse toResponse(ReclamacionCalificacion rec) {
        if (rec == null) {
            return null;
        }

        ReclamacionCalificacionResponse response = new ReclamacionCalificacionResponse();
        response.setId(rec.getId());
        response.setNumeroRadicado(rec.getNumeroRadicado());
        response.setTipoCalificacion(rec.getTipoCalificacion());
        response.setReferenciaId(rec.getReferenciaId());

        if (rec.getMatricula() != null) {
            Matricula mat = rec.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
            }
        }

        if (rec.getSolicitanteUsuario() != null) {
            response.setSolicitanteUsuarioId(rec.getSolicitanteUsuario().getId());
        }

        response.setRolSolicitante(rec.getRolSolicitante());
        response.setMotivoReclamacion(rec.getMotivoReclamacion());
        response.setSoporteUrl(rec.getSoporteUrl());
        response.setEstado(rec.getEstado());
        response.setFechaRadicacion(rec.getFechaRadicacion());
        response.setFechaLimiteRespuesta(rec.getFechaLimiteRespuesta());
        response.setDecisionPrimeraInstancia(rec.getDecisionPrimeraInstancia());
        response.setMotivoPrimeraInstancia(rec.getMotivoPrimeraInstancia());
        response.setFechaDecisionPrimeraInstancia(rec.getFechaDecisionPrimeraInstancia());

        if (rec.getDocenteResolutor() != null) {
            response.setDocenteResolutorNombre(rec.getDocenteResolutor().getUsername());
        }

        response.setDecisionSegundaInstancia(rec.getDecisionSegundaInstancia());
        response.setMotivoSegundaInstancia(rec.getMotivoSegundaInstancia());
        response.setFechaDecisionSegundaInstancia(rec.getFechaDecisionSegundaInstancia());

        if (rec.getCoordinadorResolutor() != null) {
            response.setCoordinadorResolutorNombre(rec.getCoordinadorResolutor().getUsername());
        }

        response.setNuevaNotaAprobada(rec.getNuevaNotaAprobada());
        return response;
    }

    public ConvalidacionCalificacionResponse toResponse(ConvalidacionCalificacion conv) {
        if (conv == null) {
            return null;
        }

        ConvalidacionCalificacionResponse response = new ConvalidacionCalificacionResponse();
        response.setId(conv.getId());
        response.setNumeroActa(conv.getNumeroActa());

        if (conv.getMatricula() != null) {
            Matricula mat = conv.getMatricula();
            response.setMatriculaId(mat.getId());
            response.setNumeroMatricula(mat.getNumeroMatricula());
            if (mat.getEstudiante() != null) {
                response.setEstudianteId(mat.getEstudiante().getId());
                response.setEstudianteNombreCompleto(mat.getEstudiante().getNombres() + " " + mat.getEstudiante().getApellidos());
            }
        }

        if (conv.getPeriodoAcademico() != null) {
            response.setPeriodoAcademicoId(conv.getPeriodoAcademico().getId());
            response.setPeriodoNombre(conv.getPeriodoAcademico().getNombre());
        }

        response.setTipoOrigen(conv.getTipoOrigen());
        response.setInstitucionOrigen(conv.getInstitucionOrigen());
        response.setActaSoporteUrl(conv.getActaSoporteUrl());
        response.setObservaciones(conv.getObservaciones());

        if (conv.getCoordinador() != null) {
            response.setCoordinadorNombre(conv.getCoordinador().getUsername());
        }

        response.setFechaConvalidacion(conv.getFechaConvalidacion());
        response.setFechaCreacion(conv.getFechaCreacion());

        if (conv.getDetalles() != null) {
            for (ConvalidacionCalificacionDetalle det : conv.getDetalles()) {
                ConvalidacionDetalleResponse detResp = new ConvalidacionDetalleResponse();
                detResp.setId(det.getId());
                if (det.getAsignatura() != null) {
                    detResp.setAsignaturaId(det.getAsignatura().getId());
                    detResp.setAsignaturaNombre(det.getAsignatura().getNombre());
                }
                detResp.setNotaConvalidada(det.getNotaConvalidada());
                detResp.setDesempeno(det.getDesempeno());
                detResp.setObservaciones(det.getObservaciones());
                response.getDetalles().add(detResp);
            }
        }

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
