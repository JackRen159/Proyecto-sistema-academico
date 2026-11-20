package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CierrePromocionMapper {

    public ObservacionBoletinGrupoResponse toResponse(ObservacionBoletinGrupo obs) {
        if (obs == null) {
            return null;
        }
        ObservacionBoletinGrupoResponse resp = new ObservacionBoletinGrupoResponse();
        resp.setId(obs.getId());
        if (obs.getMatricula() != null) {
            resp.setMatriculaId(obs.getMatricula().getId());
            resp.setNumeroMatricula(obs.getMatricula().getNumeroMatricula());
            if (obs.getMatricula().getEstudiante() != null) {
                resp.setEstudianteId(obs.getMatricula().getEstudiante().getId());
                resp.setEstudianteNombreCompleto(obs.getMatricula().getEstudiante().getNombres() + " " + obs.getMatricula().getEstudiante().getApellidos());
            }
        }
        if (obs.getPeriodoAcademico() != null) {
            resp.setPeriodoId(obs.getPeriodoAcademico().getId());
            resp.setPeriodoNombre(obs.getPeriodoAcademico().getNombre());
        }
        if (obs.getDocenteDirector() != null) {
            resp.setDocenteDirectorId(obs.getDocenteDirector().getId());
            resp.setDocenteDirectorNombre(obs.getDocenteDirector().getNombres() + " " + obs.getDocenteDirector().getApellidos());
        }
        resp.setObservacionFormativa(obs.getObservacionFormativa());
        resp.setRecomendacionesPedagogicas(obs.getRecomendacionesPedagogicas());
        resp.setFechaRegistro(obs.getFechaRegistro());
        return resp;
    }

    public BoletinPublicadoResponse toResponse(BoletinPublicadoVersion b) {
        if (b == null) {
            return null;
        }
        BoletinPublicadoResponse resp = new BoletinPublicadoResponse();
        resp.setId(b.getId());
        if (b.getMatricula() != null) {
            resp.setMatriculaId(b.getMatricula().getId());
            resp.setNumeroMatricula(b.getMatricula().getNumeroMatricula());
            if (b.getMatricula().getEstudiante() != null) {
                resp.setEstudianteId(b.getMatricula().getEstudiante().getId());
                resp.setEstudianteNombreCompleto(b.getMatricula().getEstudiante().getNombres() + " " + b.getMatricula().getEstudiante().getApellidos());
            }
        }
        if (b.getPeriodoAcademico() != null) {
            resp.setPeriodoId(b.getPeriodoAcademico().getId());
            resp.setPeriodoNombre(b.getPeriodoAcademico().getNombre());
        }
        resp.setNumeroVersion(b.getNumeroVersion());
        resp.setEstadoBoletin(b.getEstadoBoletin());
        resp.setDatosJson(b.getDatosJson());
        if (b.getUsuarioPublicador() != null) {
            resp.setUsuarioPublicadorNombre(b.getUsuarioPublicador().getUsername());
        }
        resp.setFechaPublicacion(b.getFechaPublicacion());
        resp.setFechaReapertura(b.getFechaReapertura());
        resp.setMotivoReapertura(b.getMotivoReapertura());
        return resp;
    }

    public ActaComisionPeriodicaResponse toResponse(ActaComisionEvaluacionPeriodica acta) {
        if (acta == null) {
            return null;
        }
        ActaComisionPeriodicaResponse resp = new ActaComisionPeriodicaResponse();
        resp.setId(acta.getId());
        if (acta.getPeriodoAcademico() != null) {
            resp.setPeriodoId(acta.getPeriodoAcademico().getId());
            resp.setPeriodoNombre(acta.getPeriodoAcademico().getNombre());
        }
        if (acta.getGrado() != null) {
            resp.setGradoId(acta.getGrado().getId());
            resp.setGradoNombre(acta.getGrado().getNombre());
        }
        resp.setNumeroActa(acta.getNumeroActa());
        resp.setFechaSesion(acta.getFechaSesion());
        resp.setIntegrantesNominativosJson(acta.getIntegrantesNominativosJson());
        resp.setConclusionesGenerales(acta.getConclusionesGenerales());
        resp.setCompromisosPedagogicosJson(acta.getCompromisosPedagogicosJson());
        if (acta.getUsuarioSecretario() != null) {
            resp.setUsuarioSecretarioNombre(acta.getUsuarioSecretario().getUsername());
        }
        resp.setFechaRegistro(acta.getFechaRegistro());
        return resp;
    }

    public PromocionEstudianteResponse toResponse(PromocionEstudianteAnual p) {
        if (p == null) {
            return null;
        }
        PromocionEstudianteResponse resp = new PromocionEstudianteResponse();
        resp.setId(p.getId());
        if (p.getActaPromocion() != null) {
            resp.setActaPromocionId(p.getActaPromocion().getId());
            resp.setNumeroActa(p.getActaPromocion().getNumeroActa());
        }
        if (p.getMatricula() != null) {
            resp.setMatriculaId(p.getMatricula().getId());
            resp.setNumeroMatricula(p.getMatricula().getNumeroMatricula());
            if (p.getMatricula().getEstudiante() != null) {
                resp.setEstudianteId(p.getMatricula().getEstudiante().getId());
                resp.setEstudianteNombreCompleto(p.getMatricula().getEstudiante().getNombres() + " " + p.getMatricula().getEstudiante().getApellidos());
            }
        }
        resp.setEstadoPromocion(p.getEstadoPromocion());
        resp.setPromedioGeneral(p.getPromedioGeneral());
        resp.setAreasReprobadas(p.getAreasReprobadas());
        resp.setAsignaturasReprobadas(p.getAsignaturasReprobadas());
        resp.setPorcentajeInasistenciaAnual(p.getPorcentajeInasistenciaAnual());
        if (p.getGradoDestino() != null) {
            resp.setGradoDestinoId(p.getGradoDestino().getId());
            resp.setGradoDestinoNombre(p.getGradoDestino().getNombre());
        }
        resp.setMotivoDecision(p.getMotivoDecision());
        resp.setFechaRegistro(p.getFechaRegistro());
        return resp;
    }

    public ActaPromocionAnualResponse toResponse(ActaPromocionAnual acta) {
        if (acta == null) {
            return null;
        }
        ActaPromocionAnualResponse resp = new ActaPromocionAnualResponse();
        resp.setId(acta.getId());
        if (acta.getAnioLectivo() != null) {
            resp.setAnioLectivoId(acta.getAnioLectivo().getId());
            resp.setAnioLectivo(acta.getAnioLectivo().getAnio());
        }
        if (acta.getGrado() != null) {
            resp.setGradoId(acta.getGrado().getId());
            resp.setGradoNombre(acta.getGrado().getNombre());
        }
        if (acta.getGrupo() != null) {
            resp.setGrupoId(acta.getGrupo().getId());
            resp.setGrupoNombre(acta.getGrupo().getNombre());
        }
        resp.setNumeroActa(acta.getNumeroActa());
        resp.setTipoActa(acta.getTipoActa());
        resp.setFechaActa(acta.getFechaActa());
        resp.setIntegrantesNominativosJson(acta.getIntegrantesNominativosJson());
        resp.setSoporteActaUrl(acta.getSoporteActaUrl());
        resp.setObservaciones(acta.getObservaciones());
        if (acta.getUsuarioRegistro() != null) {
            resp.setUsuarioRegistroNombre(acta.getUsuarioRegistro().getUsername());
        }
        resp.setFechaRegistro(acta.getFechaRegistro());
        if (acta.getDecisionesEstudiantes() != null) {
            for (PromocionEstudianteAnual p : acta.getDecisionesEstudiantes()) {
                resp.getDecisiones().add(toResponse(p));
            }
        }
        return resp;
    }

    public TitulacionGraduandoResponse toResponse(TitulacionGraduandoMedia t) {
        if (t == null) {
            return null;
        }
        TitulacionGraduandoResponse resp = new TitulacionGraduandoResponse();
        resp.setId(t.getId());
        if (t.getMatricula() != null) {
            resp.setMatriculaId(t.getMatricula().getId());
            resp.setNumeroMatricula(t.getMatricula().getNumeroMatricula());
            if (t.getMatricula().getEstudiante() != null) {
                resp.setEstudianteId(t.getMatricula().getEstudiante().getId());
                resp.setEstudianteNombreCompleto(t.getMatricula().getEstudiante().getNombres() + " " + t.getMatricula().getEstudiante().getApellidos());
                resp.setEstudianteDocumento(t.getMatricula().getEstudiante().getDocumento());
            }
        }
        resp.setModalidadTitulo(t.getModalidadTitulo());
        resp.setEspecialidad(t.getEspecialidad());
        resp.setHorasServicioSocial(t.getHorasServicioSocial());
        resp.setSoporteServicioSocialUrl(t.getSoporteServicioSocialUrl());
        resp.setCodigoRegistroIcfes(t.getCodigoRegistroIcfes());
        resp.setDocumentoIdentidadVerificado(t.getDocumentoIdentidadVerificado());
        resp.setEstadoTitulacion(t.getEstadoTitulacion());
        resp.setNumeroActaGeneral(t.getNumeroActaGeneral());
        resp.setNumeroActaIndividual(t.getNumeroActaIndividual());
        resp.setNumeroDiploma(t.getNumeroDiploma());
        resp.setNumeroLibro(t.getNumeroLibro());
        resp.setNumeroFolio(t.getNumeroFolio());
        resp.setFechaGraduacion(t.getFechaGraduacion());
        if (t.getUsuarioAutorizador() != null) {
            resp.setUsuarioAutorizadorNombre(t.getUsuarioAutorizador().getUsername());
        }
        resp.setFechaRegistro(t.getFechaRegistro());
        return resp;
    }
}
