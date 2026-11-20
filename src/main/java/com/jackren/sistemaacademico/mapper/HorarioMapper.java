package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class HorarioMapper {

    public Aula toEntity(AulaRequest request) {
        if (request == null) {
            return null;
        }
        Aula aula = new Aula();
        aula.setSede(request.getSede() != null ? request.getSede() : "SEDE_PRINCIPAL");
        aula.setCodigo(request.getCodigo());
        aula.setNombre(request.getNombre());
        aula.setTipoEspacio(request.getTipoEspacio() != null ? request.getTipoEspacio() : TipoEspacioFisico.AULA_ESTANDAR);
        aula.setCapacidadMaxima(request.getCapacidadMaxima() != null ? request.getCapacidadMaxima() : 40);
        aula.setEsMultigrupo(Boolean.TRUE.equals(request.getEsMultigrupo()));
        aula.setEstado(request.getEstado() != null ? request.getEstado() : EstadoAula.DISPONIBLE);
        aula.setObservaciones(request.getObservaciones());
        return aula;
    }

    public AulaResponse toResponse(Aula aula) {
        if (aula == null) {
            return null;
        }
        AulaResponse resp = new AulaResponse();
        resp.setId(aula.getId());
        resp.setSede(aula.getSede());
        resp.setCodigo(aula.getCodigo());
        resp.setNombre(aula.getNombre());
        resp.setTipoEspacio(aula.getTipoEspacio());
        resp.setCapacidadMaxima(aula.getCapacidadMaxima());
        resp.setEsMultigrupo(aula.getEsMultigrupo());
        resp.setEstado(aula.getEstado());
        resp.setObservaciones(aula.getObservaciones());
        return resp;
    }

    public PlantillaFranjaHoraria toEntity(PlantillaFranjaHorariaRequest request, AnioLectivo anio) {
        if (request == null) {
            return null;
        }
        PlantillaFranjaHoraria franja = new PlantillaFranjaHoraria();
        franja.setAnioLectivo(anio);
        franja.setJornada(request.getJornada());
        franja.setNumeroBloque(request.getNumeroBloque());
        franja.setHoraInicio(request.getHoraInicio());
        franja.setHoraFin(request.getHoraFin());
        franja.setTipoFranja(request.getTipoFranja() != null ? request.getTipoFranja() : TipoFranjaHoraria.BLOQUE_LECTIVO);
        franja.setDescripcion(request.getDescripcion());
        return franja;
    }

    public PlantillaFranjaHorariaResponse toResponse(PlantillaFranjaHoraria franja) {
        if (franja == null) {
            return null;
        }
        PlantillaFranjaHorariaResponse resp = new PlantillaFranjaHorariaResponse();
        resp.setId(franja.getId());
        if (franja.getAnioLectivo() != null) {
            resp.setAnioLectivoId(franja.getAnioLectivo().getId());
            resp.setAnioLectivo(franja.getAnioLectivo().getAnio());
        }
        resp.setJornada(franja.getJornada());
        resp.setNumeroBloque(franja.getNumeroBloque());
        resp.setHoraInicio(franja.getHoraInicio());
        resp.setHoraFin(franja.getHoraFin());
        resp.setTipoFranja(franja.getTipoFranja());
        resp.setDescripcion(franja.getDescripcion());
        return resp;
    }

    public HorarioBloqueResponse toResponse(HorarioBloque b) {
        if (b == null) {
            return null;
        }
        HorarioBloqueResponse resp = new HorarioBloqueResponse();
        resp.setId(b.getId());
        if (b.getHorarioVersion() != null) {
            resp.setHorarioVersionId(b.getHorarioVersion().getId());
        }
        if (b.getAsignacionAcademica() != null) {
            AsignacionAcademica asig = b.getAsignacionAcademica();
            resp.setAsignacionAcademicaId(asig.getId());
            if (asig.getProfesor() != null) {
                resp.setProfesorId(asig.getProfesor().getId());
                resp.setProfesorNombreCompleto(asig.getProfesor().getNombres() + " " + asig.getProfesor().getApellidos());
            }
            if (asig.getAsignatura() != null) {
                resp.setAsignaturaId(asig.getAsignatura().getId());
                resp.setAsignaturaNombre(asig.getAsignatura().getNombre());
            }
        }
        if (b.getAula() != null) {
            resp.setAulaId(b.getAula().getId());
            resp.setAulaCodigo(b.getAula().getCodigo());
            resp.setAulaNombre(b.getAula().getNombre());
        }
        resp.setDiaSemana(b.getDiaSemana());
        if (b.getFranjaHoraria() != null) {
            resp.setFranjaHorariaId(b.getFranjaHoraria().getId());
            resp.setNumeroBloque(b.getFranjaHoraria().getNumeroBloque());
            resp.setHoraInicio(b.getFranjaHoraria().getHoraInicio());
            resp.setHoraFin(b.getFranjaHoraria().getHoraFin());
            resp.setTipoFranja(b.getFranjaHoraria().getTipoFranja());
        }
        return resp;
    }

    public HorarioGrupoVersionResponse toResponse(HorarioGrupoVersion v) {
        if (v == null) {
            return null;
        }
        HorarioGrupoVersionResponse resp = new HorarioGrupoVersionResponse();
        resp.setId(v.getId());
        if (v.getGrupo() != null) {
            resp.setGrupoId(v.getGrupo().getId());
            resp.setGrupoNombre(v.getGrupo().getNombre());
        }
        resp.setNumeroVersion(v.getNumeroVersion());
        resp.setEstado(v.getEstado());
        resp.setFechaInicioVigencia(v.getFechaInicioVigencia());
        resp.setFechaFinVigencia(v.getFechaFinVigencia());
        if (v.getUsuarioPublicador() != null) {
            resp.setUsuarioPublicadorNombre(v.getUsuarioPublicador().getUsername());
        }
        resp.setFechaPublicacion(v.getFechaPublicacion());
        if (v.getBloques() != null) {
            for (HorarioBloque b : v.getBloques()) {
                resp.getBloques().add(toResponse(b));
            }
        }
        return resp;
    }

    public NovedadHorarioResponse toResponse(NovedadHorario n) {
        if (n == null) {
            return null;
        }
        NovedadHorarioResponse resp = new NovedadHorarioResponse();
        resp.setId(n.getId());
        if (n.getHorarioBloque() != null) {
            resp.setHorarioBloqueId(n.getHorarioBloque().getId());
        }
        resp.setTipoNovedad(n.getTipoNovedad());
        if (n.getAulaReemplazo() != null) {
            resp.setAulaReemplazoId(n.getAulaReemplazo().getId());
            resp.setAulaReemplazoCodigo(n.getAulaReemplazo().getCodigo());
        }
        if (n.getDocenteReemplazo() != null) {
            resp.setDocenteReemplazoId(n.getDocenteReemplazo().getId());
            resp.setDocenteReemplazoNombre(n.getDocenteReemplazo().getNombres() + " " + n.getDocenteReemplazo().getApellidos());
        }
        resp.setFechaInicio(n.getFechaInicio());
        resp.setFechaFin(n.getFechaFin());
        resp.setMotivo(n.getMotivo());
        if (n.getUsuarioAutorizador() != null) {
            resp.setUsuarioAutorizadorNombre(n.getUsuarioAutorizador().getUsername());
        }
        resp.setFechaRegistro(n.getFechaRegistro());
        return resp;
    }
}
