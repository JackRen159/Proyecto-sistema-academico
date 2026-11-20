package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GradoAsignaturaRequest;
import com.jackren.sistemaacademico.dto.GradoAsignaturaResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.GradoAsignaturaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class GradoAsignaturaService {

    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final GradoRepository gradoRepository;
    private final AsignaturaRepository asignaturaRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final AsignacionAcademicaRepository asignacionAcademicaRepository;
    private final GrupoRepository grupoRepository;
    private final GradoAsignaturaMapper gradoAsignaturaMapper;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public GradoAsignaturaService(GradoAsignaturaRepository gradoAsignaturaRepository,
                                  GradoRepository gradoRepository,
                                  AsignaturaRepository asignaturaRepository,
                                  AnioLectivoRepository anioLectivoRepository,
                                  AsignacionAcademicaRepository asignacionAcademicaRepository,
                                  GrupoRepository grupoRepository,
                                  GradoAsignaturaMapper gradoAsignaturaMapper,
                                  AuditoriaGlobalService auditoriaGlobalService) {
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.gradoRepository = gradoRepository;
        this.asignaturaRepository = asignaturaRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.asignacionAcademicaRepository = asignacionAcademicaRepository;
        this.grupoRepository = grupoRepository;
        this.gradoAsignaturaMapper = gradoAsignaturaMapper;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public List<GradoAsignaturaResponse> listarPorGrado(Long gradoId) {
        if (!gradoRepository.existsById(gradoId)) {
            throw new RecursoNoEncontradoException("No se encontró el grado con ID: " + gradoId);
        }

        return gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId)
                .stream()
                .map(gradoAsignaturaMapper::toResponse)
                .toList();
    }

    public List<GradoAsignaturaResponse> listarPorGradoYAnio(Long gradoId, Long anioLectivoId) {
        if (!gradoRepository.existsById(gradoId)) {
            throw new RecursoNoEncontradoException("No se encontró el grado con ID: " + gradoId);
        }
        if (anioLectivoId != null && !anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        List<GradoAsignatura> lista;
        if (anioLectivoId != null) {
            lista = gradoAsignaturaRepository.findByGradoIdAndAnioLectivoIdAndActivoTrue(gradoId, anioLectivoId);
            if (lista.isEmpty()) {
                lista = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);
            }
        } else {
            lista = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);
        }

        return lista.stream()
                .map(gradoAsignaturaMapper::toResponse)
                .toList();
    }

    @Transactional
    public GradoAsignaturaResponse asignar(GradoAsignaturaRequest request) {
        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        Asignatura asignatura = asignaturaRepository.findById(request.getAsignaturaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignatura con ID: " + request.getAsignaturaId()));

        AnioLectivo anioLectivo = null;
        if (request.getAnioLectivoId() != null) {
            anioLectivo = anioLectivoRepository.findById(request.getAnioLectivoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + request.getAnioLectivoId()));
        }

        validarPonderacionAsignatura(grado.getId(), request.getAnioLectivoId(), asignatura, request.getPesoPorcentualEnArea(), null);

        AnioLectivo finalAnio = anioLectivo;
        return gradoAsignaturaRepository.findByGradoIdAndAsignaturaId(grado.getId(), asignatura.getId())
                .map(existente -> {
                    if (Boolean.TRUE.equals(existente.getActivo())) {
                        throw new ConflictoNegocioException("La asignatura ya se encuentra asignada a este grado en el plan de estudios.");
                    }
                    existente.setActivo(true);
                    existente.setIntensidadHoraria(request.getIntensidadHoraria());
                    existente.setEsOptativa(request.getEsOptativa() != null ? request.getEsOptativa() : false);
                    if (request.getPesoPorcentualEnArea() != null) {
                        existente.setPesoPorcentualEnArea(request.getPesoPorcentualEnArea());
                    }
                    if (request.getTipoComponente() != null) {
                        existente.setTipoComponente(request.getTipoComponente());
                    }
                    if (request.getComputaPromocion() != null) {
                        existente.setComputaPromocion(request.getComputaPromocion());
                    }
                    if (finalAnio != null) {
                        existente.setAnioLectivo(finalAnio);
                    }
                    GradoAsignatura guardado = gradoAsignaturaRepository.save(existente);

                    if (auditoriaGlobalService != null) {
                        auditoriaGlobalService.registrarEvento(
                                "ASIGNACION_PLAN_ESTUDIOS",
                                "GradoAsignatura",
                                guardado.getId().toString(),
                                null,
                                "Asignatura " + asignatura.getNombre() + " a Grado " + grado.getNombre() + " (IHS: " + guardado.getIntensidadHoraria() + ")",
                                "Reactivación y asignación de plan de estudios",
                                "EXITOSO"
                        );
                    }

                    return gradoAsignaturaMapper.toResponse(guardado);
                })
                .orElseGet(() -> {
                    GradoAsignatura nueva = gradoAsignaturaMapper.toEntity(request, grado, asignatura);
                    if (finalAnio != null) {
                        nueva.setAnioLectivo(finalAnio);
                    }
                    GradoAsignatura guardada = gradoAsignaturaRepository.save(nueva);

                    if (auditoriaGlobalService != null) {
                        auditoriaGlobalService.registrarEvento(
                                "ASIGNACION_PLAN_ESTUDIOS",
                                "GradoAsignatura",
                                guardada.getId().toString(),
                                null,
                                "Asignatura " + asignatura.getNombre() + " a Grado " + grado.getNombre() + " (IHS: " + guardada.getIntensidadHoraria() + ")",
                                "Inclusión en el plan de estudios",
                                "EXITOSO"
                        );
                    }

                    return gradoAsignaturaMapper.toResponse(guardada);
                });
    }

    @Transactional
    public GradoAsignaturaResponse actualizar(Long id, Integer intensidadHoraria, Boolean esOptativa, BigDecimal pesoPorcentualEnArea) {
        GradoAsignatura ga = gradoAsignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignación de plan de estudios con ID: " + id));

        if (intensidadHoraria != null) {
            if (intensidadHoraria <= 0) {
                throw new IllegalArgumentException("La intensidad horaria debe ser mayor a 0 (IHS > 0)");
            }
            ga.setIntensidadHoraria(intensidadHoraria);
        }

        if (esOptativa != null) {
            ga.setEsOptativa(esOptativa);
        }

        if (pesoPorcentualEnArea != null) {
            if (pesoPorcentualEnArea.compareTo(BigDecimal.ZERO) <= 0 || pesoPorcentualEnArea.compareTo(new BigDecimal("100.00")) > 0) {
                throw new IllegalArgumentException("El peso porcentual en área debe ser mayor a 0 y menor o igual a 100.00");
            }
            Long anioId = ga.getAnioLectivo() != null ? ga.getAnioLectivo().getId() : null;
            validarPonderacionAsignatura(ga.getGrado().getId(), anioId, ga.getAsignatura(), pesoPorcentualEnArea, id);
            ga.setPesoPorcentualEnArea(pesoPorcentualEnArea);
        }

        GradoAsignatura actualizada = gradoAsignaturaRepository.save(ga);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_PLAN_ESTUDIOS",
                    "GradoAsignatura",
                    actualizada.getId().toString(),
                    null,
                    "Actualización IHS: " + actualizada.getIntensidadHoraria() + ", Peso %: " + actualizada.getPesoPorcentualEnArea(),
                    "Ajuste de parámetros curriculares",
                    "EXITOSO"
            );
        }

        return gradoAsignaturaMapper.toResponse(actualizada);
    }

    @Transactional
    public GradoAsignaturaResponse actualizar(Long id, Integer intensidadHoraria, Boolean esOptativa) {
        return actualizar(id, intensidadHoraria, esOptativa, null);
    }

    @Transactional
    public void desactivar(Long id) {
        GradoAsignatura ga = gradoAsignaturaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la asignación de plan de estudios con ID: " + id));

        // Validar protección contra retiro destructivo con registros asociados (UC-PROV-07 E2)
        Long gradoId = ga.getGrado().getId();
        Long asignaturaId = ga.getAsignatura().getId();
        List<Grupo> gruposDelGrado = grupoRepository.findAll().stream()
                .filter(g -> g.getGrado().getId().equals(gradoId) && Boolean.TRUE.equals(g.getActivo()))
                .toList();

        for (Grupo g : gruposDelGrado) {
            boolean tieneAsignacion = asignacionAcademicaRepository.existsByGrupoIdAndAsignaturaId(g.getId(), asignaturaId);
            if (tieneAsignacion) {
                throw new ConflictoNegocioException("No se puede retirar la asignatura '" + ga.getAsignatura().getNombre()
                        + "' del plan de estudios de " + ga.getGrado().getNombre()
                        + " porque ya cuenta con asignaciones académicas docentes activas en el grupo "
                        + g.getNombre() + " (UC-PROV-07 E2).");
            }
        }

        ga.setActivo(false);
        gradoAsignaturaRepository.save(ga);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "DESACTIVACION_PLAN_ESTUDIOS",
                    "GradoAsignatura",
                    id.toString(),
                    "Activo: true",
                    "Activo: false",
                    "Retiro de asignatura del plan de estudios",
                    "EXITOSO"
            );
        }
    }

    private void validarPonderacionAsignatura(Long gradoId, Long anioLectivoId, Asignatura asignatura, BigDecimal nuevoPeso, Long gaIdExcluir) {
        if (asignatura.getArea() == null || nuevoPeso == null) {
            return;
        }

        Long areaId = asignatura.getArea().getId();
        List<GradoAsignatura> activasDelGrado = gradoAsignaturaRepository.findByGradoIdAndActivoTrue(gradoId);

        List<GradoAsignatura> deLaMismaArea = activasDelGrado.stream()
                .filter(g -> g.getAsignatura().getArea() != null && g.getAsignatura().getArea().getId().equals(areaId))
                .filter(g -> gaIdExcluir == null || !g.getId().equals(gaIdExcluir))
                .filter(g -> !g.getAsignatura().getId().equals(asignatura.getId()))
                .toList();

        if (!deLaMismaArea.isEmpty()) {
            BigDecimal sumaExistente = deLaMismaArea.stream()
                    .map(GradoAsignatura::getPesoPorcentualEnArea)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal totalResultante = sumaExistente.add(nuevoPeso);
            if (totalResultante.compareTo(new BigDecimal("100.00")) > 0) {
                throw new ConflictoNegocioException("La suma de los pesos porcentuales de las asignaturas en el área '"
                        + asignatura.getArea().getNombre() + "' no puede superar el 100.00%. Total acumulado resultante: "
                        + totalResultante + "% (UC-PROV-07 E1, RN-EST-ACA-03).");
            }
        }
    }
}
