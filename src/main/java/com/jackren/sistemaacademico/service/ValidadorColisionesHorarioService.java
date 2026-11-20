package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.HorarioBloqueRequest;
import com.jackren.sistemaacademico.dto.ValidacionHorarioResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ValidadorColisionesHorarioService {

    private final HorarioBloqueRepository bloqueRepository;
    private final AsignacionAcademicaRepository asignacionRepository;
    private final AulaRepository aulaRepository;
    private final PlantillaFranjaHorariaRepository franjaRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final MatriculaRepository matriculaRepository;

    public ValidadorColisionesHorarioService(HorarioBloqueRepository bloqueRepository,
                                             AsignacionAcademicaRepository asignacionRepository,
                                             AulaRepository aulaRepository,
                                             PlantillaFranjaHorariaRepository franjaRepository,
                                             GradoAsignaturaRepository gradoAsignaturaRepository,
                                             MatriculaRepository matriculaRepository) {
        this.bloqueRepository = bloqueRepository;
        this.asignacionRepository = asignacionRepository;
        this.aulaRepository = aulaRepository;
        this.franjaRepository = franjaRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.matriculaRepository = matriculaRepository;
    }

    /**
     * Valida la regla de Triple No-Colisión (RN-HOR-01) para un bloque antes de asignarlo.
     */
    public void validarBloque(HorarioBloqueRequest request, Long horarioVersionId, Long bloqueIdIgnorar) {
        AsignacionAcademica asignacion = asignacionRepository.findById(request.getAsignacionAcademicaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación académica no encontrada con ID " + request.getAsignacionAcademicaId()));

        if (!Boolean.TRUE.equals(asignacion.getActivo())) {
            throw new ConflictoNegocioException("La asignación académica seleccionada está inactiva (UC-PROV-20 E4)");
        }

        Aula aula = aulaRepository.findById(request.getAulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Aula no encontrada con ID " + request.getAulaId()));

        // UC-PROV-20 E3: Estado del aula y capacidad física (RN-HOR-05)
        if (aula.getEstado() != EstadoAula.DISPONIBLE) {
            throw new ConflictoNegocioException("El aula " + aula.getCodigo() + " se encuentra en estado " +
                    aula.getEstado() + " y no puede ser programada (UC-PROV-20 E3)");
        }

        long matriculados = matriculaRepository.countByGrupoIdAndEstado(asignacion.getGrupo().getId(), EstadoMatricula.MATRICULADA);
        if (matriculados > aula.getCapacidadMaxima()) {
            throw new ConflictoNegocioException("La capacidad máxima del aula " + aula.getCodigo() +
                    " (" + aula.getCapacidadMaxima() + ") es insuficiente para los " + matriculados +
                    " estudiantes matriculados en el grupo " + asignacion.getGrupo().getNombre() + " (RN-HOR-05, UC-PROV-20 E3)");
        }

        PlantillaFranjaHoraria franja = franjaRepository.findById(request.getFranjaHorariaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Franja horaria no encontrada con ID " + request.getFranjaHorariaId()));

        if (franja.getTipoFranja() != TipoFranjaHoraria.BLOQUE_LECTIVO) {
            throw new ConflictoNegocioException("No se pueden programar clases en franjas de tipo " +
                    franja.getTipoFranja() + " (descansos pedagógicos) (RN-HOR-04)");
        }

        Long ignoreId = bloqueIdIgnorar != null ? bloqueIdIgnorar : -1L;

        // 1. Colisión de Grupo: no dos clases en el mismo día y franja en esta versión
        if (horarioVersionId != null) {
            bloqueRepository.findByHorarioVersionIdAndDiaSemanaAndFranjaHorariaId(horarioVersionId, request.getDiaSemana(), franja.getId())
                    .ifPresent(existente -> {
                        if (!existente.getId().equals(ignoreId)) {
                            throw new ConflictoNegocioException("Conflicto de Grupo: el curso " + asignacion.getGrupo().getNombre() +
                                    " ya tiene asignada la materia " + existente.getAsignacionAcademica().getAsignatura().getNombre() +
                                    " en el día " + request.getDiaSemana() + " bloque " + franja.getNumeroBloque() + " (RN-HOR-01)");
                        }
                    });
        }

        // 2. Colisión de Docente: no dos clases al mismo docente en versiones PUBLICADO_VIGENTE
        if (asignacion.getProfesor() != null) {
            List<HorarioBloque> colisionesDocente = bloqueRepository.findColisionesDocente(
                    asignacion.getProfesor().getId(), request.getDiaSemana(), franja.getId(),
                    EstadoHorarioVersion.PUBLICADO_VIGENTE, ignoreId
            );
            if (!colisionesDocente.isEmpty()) {
                HorarioBloque c = colisionesDocente.get(0);
                throw new ConflictoNegocioException("Conflicto de Docente: el profesor " + asignacion.getProfesor().getNombres() + " " +
                        asignacion.getProfesor().getApellidos() + " ya tiene clase asignada con el grupo " +
                        c.getAsignacionAcademica().getGrupo().getNombre() + " en el día " + request.getDiaSemana() +
                        " bloque " + franja.getNumeroBloque() + " (RN-HOR-01, UC-PROV-20 E1)");
            }
        }

        // 3. Colisión de Aula: no dos grupos en la misma aula (salvo esMultigrupo)
        if (!Boolean.TRUE.equals(aula.getEsMultigrupo())) {
            List<HorarioBloque> colisionesAula = bloqueRepository.findColisionesAula(
                    aula.getId(), request.getDiaSemana(), franja.getId(),
                    EstadoHorarioVersion.PUBLICADO_VIGENTE, ignoreId
            );
            if (!colisionesAula.isEmpty()) {
                HorarioBloque c = colisionesAula.get(0);
                throw new ConflictoNegocioException("Conflicto de Aula: el espacio " + aula.getCodigo() + " (" + aula.getNombre() +
                        ") ya está ocupado por el grupo " + c.getAsignacionAcademica().getGrupo().getNombre() +
                        " en el día " + request.getDiaSemana() + " bloque " + franja.getNumeroBloque() + " (RN-HOR-01, UC-PROV-20 E1)");
            }
        }
    }

    /**
     * Valida el cumplimiento exacto de la Intensidad Horaria Semanal (IHS) de cada asignatura (RN-HOR-02)
     */
    public ValidacionHorarioResponse validarHorarioCompleto(HorarioGrupoVersion version) {
        Grupo grupo = version.getGrupo();
        Long anioId = grupo.getAnioLectivo() != null ? grupo.getAnioLectivo().getId() : 1L;

        List<GradoAsignatura> planEstudios = gradoAsignaturaRepository
                .findByGradoIdAndAnioLectivoIdAndActivoTrue(grupo.getGrado().getId(), anioId);

        if (planEstudios.isEmpty()) {
            planEstudios = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(grupo.getGrado().getId());
        }

        ValidacionHorarioResponse respuesta = new ValidacionHorarioResponse(true);
        List<String> errores = new ArrayList<>();

        for (GradoAsignatura ga : planEstudios) {
            long bloquesProgramados = bloqueRepository.countBloquesAsignadosAsignatura(version.getId(), ga.getAsignatura().getId());
            int ihsRequerida = ga.getIntensidadHoraria() != null ? ga.getIntensidadHoraria() : 1;

            ValidacionHorarioResponse.DetalleCumplimientoIhs detalle = new ValidacionHorarioResponse.DetalleCumplimientoIhs(
                    ga.getAsignatura().getId(),
                    ga.getAsignatura().getNombre(),
                    ihsRequerida,
                    bloquesProgramados
            );
            respuesta.getCumplimientoIhs().add(detalle);

            if (!detalle.getCumple()) {
                respuesta.setValido(false);
                errores.add("La asignatura " + ga.getAsignatura().getNombre() + " requiere " + ihsRequerida +
                        " horas semanales según el Plan de Estudios, pero tiene " + bloquesProgramados + " programadas (RN-HOR-02)");
            }
        }

        respuesta.setErroresColision(errores);
        return respuesta;
    }
}
