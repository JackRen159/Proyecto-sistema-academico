package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.AnioLectivoRequest;
import com.jackren.sistemaacademico.dto.AnioLectivoResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.AnioLectivoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.GradoAsignaturaRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import com.jackren.sistemaacademico.repository.PeriodoAcademicoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AnioLectivoService {

    private final AnioLectivoRepository anioLectivoRepository;
    private final AnioLectivoMapper anioLectivoMapper;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    private final GrupoRepository grupoRepository;
    private final GradoAsignaturaRepository gradoAsignaturaRepository;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public AnioLectivoService(AnioLectivoRepository anioLectivoRepository,
                              AnioLectivoMapper anioLectivoMapper,
                              PeriodoAcademicoRepository periodoAcademicoRepository,
                              GrupoRepository grupoRepository,
                              GradoAsignaturaRepository gradoAsignaturaRepository,
                              AuditoriaGlobalService auditoriaGlobalService) {
        this.anioLectivoRepository = anioLectivoRepository;
        this.anioLectivoMapper = anioLectivoMapper;
        this.periodoAcademicoRepository = periodoAcademicoRepository;
        this.grupoRepository = grupoRepository;
        this.gradoAsignaturaRepository = gradoAsignaturaRepository;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public List<AnioLectivoResponse> listarTodos() {
        return anioLectivoRepository.findAllByOrderByAnioDesc()
                .stream()
                .map(anioLectivoMapper::toResponse)
                .toList();
    }

    public AnioLectivoResponse obtenerPorId(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));
        return anioLectivoMapper.toResponse(anio);
    }

    public AnioLectivoResponse obtenerPorAnio(Integer anio) {
        AnioLectivo anioLectivo = anioLectivoRepository.findByAnio(anio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo correspondiente a: " + anio));
        return anioLectivoMapper.toResponse(anioLectivo);
    }

    public AnioLectivoResponse obtenerActivo() {
        AnioLectivo anioActivo = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe ningún año lectivo actualmente ACTIVO."));
        return anioLectivoMapper.toResponse(anioActivo);
    }

    public Optional<AnioLectivoResponse> buscarActivo() {
        return anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO)
                .map(anioLectivoMapper::toResponse);
    }

    @Transactional
    public AnioLectivoResponse guardar(AnioLectivoRequest request) {
        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        if (anioLectivoRepository.existsByAnio(request.getAnio())) {
            throw new ConflictoNegocioException("Ya existe un año lectivo registrado para el año: " + request.getAnio());
        }

        AnioLectivo anio = anioLectivoMapper.toEntity(request);
        AnioLectivo anioGuardado = anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_ANIO_LECTIVO",
                    "AnioLectivo",
                    anioGuardado.getId().toString(),
                    null,
                    "Año: " + anioGuardado.getAnio() + ", Estado: " + anioGuardado.getEstado(),
                    "Apertura / Registro de año lectivo",
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(anioGuardado);
    }

    @Transactional
    public AnioLectivoResponse actualizar(Long id, AnioLectivoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        if (esEstadoCerrado(anio.getEstado())) {
            throw new ConflictoNegocioException("No se puede modificar ordinariamente un año lectivo CERRADO (UC-14 E4). "
                    + "Se requiere el flujo rectoral de ajuste excepcional con número de resolución (A5).");
        }

        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        // Validación en cascada: las nuevas fechas no pueden dejar periodos existentes fuera de rango (UC-14 E1)
        if (periodoAcademicoRepository != null) {
            List<PeriodoAcademico> periodos = periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(id);
            for (PeriodoAcademico p : periodos) {
                if (p.getFechaInicio() != null && request.getFechaInicio() != null && request.getFechaInicio().isAfter(p.getFechaInicio())) {
                    throw new ConflictoNegocioException("La nueva fecha de inicio (" + request.getFechaInicio()
                            + ") deja al periodo '" + p.getNombre() + "' (inicio: " + p.getFechaInicio() + ") fuera del año lectivo (UC-14 E1).");
                }
                if (p.getFechaFin() != null && request.getFechaFin() != null && request.getFechaFin().isBefore(p.getFechaFin())) {
                    throw new ConflictoNegocioException("La nueva fecha de fin (" + request.getFechaFin()
                            + ") deja al periodo '" + p.getNombre() + "' (fin: " + p.getFechaFin() + ") fuera del año lectivo (UC-14 E1).");
                }
            }
        }

        if (!anio.getAnio().equals(request.getAnio()) && anioLectivoRepository.existsByAnio(request.getAnio())) {
            throw new ConflictoNegocioException("Ya existe un año lectivo registrado para el año: " + request.getAnio());
        }

        String anterior = "Año: " + anio.getAnio() + ", Estado: " + anio.getEstado() + ", Inicio: " + anio.getFechaInicio() + ", Fin: " + anio.getFechaFin();
        anioLectivoMapper.updateEntityFromRequest(anio, request);
        AnioLectivo actualizado = anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_ANIO_LECTIVO",
                    "AnioLectivo",
                    actualizado.getId().toString(),
                    anterior,
                    "Año: " + actualizado.getAnio() + ", Inicio: " + actualizado.getFechaInicio() + ", Fin: " + actualizado.getFechaFin(),
                    "Actualización de parámetros del año lectivo",
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(actualizado);
    }

    @Transactional
    public AnioLectivoResponse activar(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        if (esEstadoCerrado(anio.getEstado())) {
            throw new ConflictoNegocioException("No se puede activar un año lectivo que ya ha sido CERRADO formalmente.");
        }

        if (anio.getEstado() == EstadoAnioLectivo.ACTIVO) {
            return anioLectivoMapper.toResponse(anio);
        }

        // Invariante de negocio: solo puede haber un año activo
        Optional<AnioLectivo> activoExistente = anioLectivoRepository.findByEstado(EstadoAnioLectivo.ACTIVO);
        if (activoExistente.isPresent() && !activoExistente.get().getId().equals(id)) {
            throw new ConflictoNegocioException("No se puede activar el año lectivo porque el año "
                    + activoExistente.get().getAnio() + " ya se encuentra ACTIVO. Debe cerrarlo primero.");
        }

        // Validar ponderación de periodos si ya existen configurados
        if (periodoAcademicoRepository != null) {
            List<PeriodoAcademico> periodos = periodoAcademicoRepository.findByAnioLectivoIdOrderByNumeroAsc(id);
            if (!periodos.isEmpty()) {
                BigDecimal suma = periodos.stream().map(PeriodoAcademico::getPorcentaje).reduce(BigDecimal.ZERO, BigDecimal::add);
                if (suma.compareTo(new BigDecimal("100.00")) != 0) {
                    throw new ConflictoNegocioException("Para activar el año lectivo como VIGENTE, la suma de las ponderaciones de los periodos debe ser exactamente 100.00% (UC-14 E2). Suma actual: " + suma + "%");
                }
            }
        }

        String estadoPrevio = anio.getEstado().name();
        anio.setEstado(EstadoAnioLectivo.ACTIVO);
        AnioLectivo guardado = anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTIVACION_ANIO_LECTIVO",
                    "AnioLectivo",
                    guardado.getId().toString(),
                    "Estado: " + estadoPrevio,
                    "Estado: ACTIVO",
                    "Activación formal de año escolar",
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(guardado);
    }

    @Transactional
    public AnioLectivoResponse cerrar(Long id) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        String estadoPrevio = anio.getEstado().name();
        anio.setEstado(EstadoAnioLectivo.CERRADO);
        AnioLectivo guardado = anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CIERRE_ANIO_LECTIVO",
                    "AnioLectivo",
                    guardado.getId().toString(),
                    "Estado: " + estadoPrevio,
                    "Estado: CERRADO",
                    "Cierre anual definitivo del año lectivo",
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(guardado);
    }

    @Transactional
    public AnioLectivoResponse ajusteExcepcionalRectoral(Long id, AnioLectivoRequest request, String motivo, String resolucionRectoral) {
        AnioLectivo anio = anioLectivoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + id));

        if (motivo == null || motivo.trim().isBlank()) {
            throw new IllegalArgumentException("El motivo del ajuste excepcional rectoral es obligatorio (UC-14 A5).");
        }
        if (resolucionRectoral == null || resolucionRectoral.trim().isBlank()) {
            throw new IllegalArgumentException("La resolución rectoral es obligatoria para ajustes excepcionales sobre años cerrados.");
        }

        validarRangoFechas(request.getFechaInicio(), request.getFechaFin());

        String valorAnterior = "Estado: " + anio.getEstado() + ", Inicio: " + anio.getFechaInicio() + ", Fin: " + anio.getFechaFin();
        anioLectivoMapper.updateEntityFromRequest(anio, request);
        anio.setMotivoAjusteExcepcional(motivo);
        anio.setResolucionRectoralExcepcional(resolucionRectoral);
        AnioLectivo actualizado = anioLectivoRepository.save(anio);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "AJUSTE_EXCEPCIONAL_ANIO_LECTIVO",
                    "AnioLectivo",
                    actualizado.getId().toString(),
                    valorAnterior,
                    "Ajuste excepcional: " + motivo + " (Resolución: " + resolucionRectoral + ")",
                    motivo,
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(actualizado);
    }

    @Transactional
    public AnioLectivoResponse clonarEstructuraAnio(Long anioOrigenId, Long anioDestinoId) {
        AnioLectivo anioOrigen = anioLectivoRepository.findById(anioOrigenId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo origen con ID: " + anioOrigenId));

        AnioLectivo anioDestino = anioLectivoRepository.findById(anioDestinoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo destino con ID: " + anioDestinoId));

        if (anioDestino.getEstado() == EstadoAnioLectivo.ACTIVO || esEstadoCerrado(anioDestino.getEstado())) {
            throw new ConflictoNegocioException("La clonación de estructura solo puede realizarse sobre un año en estado EN_PLANEACION o PLANIFICACION (UC-14 A1).");
        }

        // Clonar grupos base del año origen al destino
        List<Grupo> gruposOrigen = grupoRepository.findByAnioLectivoIdAndActivoTrue(anioOrigenId);
        int gruposClonados = 0;
        for (Grupo g : gruposOrigen) {
            if (!grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(anioDestinoId, g.getGrado().getId(), g.getNombre())) {
                Grupo nuevoGrupo = new Grupo();
                nuevoGrupo.setAnioLectivo(anioDestino);
                nuevoGrupo.setGrado(g.getGrado());
                nuevoGrupo.setNombre(g.getNombre());
                nuevoGrupo.setJornada(g.getJornada());
                nuevoGrupo.setCupoMaximo(g.getCupoMaximo());
                nuevoGrupo.setSede(g.getSede());
                nuevoGrupo.setAulaBase(g.getAulaBase());
                nuevoGrupo.setEstado(EstadoGrupo.EN_PROYECCION);
                nuevoGrupo.setActivo(true);
                grupoRepository.save(nuevoGrupo);
                gruposClonados++;
            }
        }

        // Clonar plan de estudios base
        List<GradoAsignatura> planOrigen = gradoAsignaturaRepository.findByAnioLectivoIdAndActivoTrue(anioOrigenId);
        if (planOrigen.isEmpty()) {
            planOrigen = gradoAsignaturaRepository.findAll().stream().filter(ga -> Boolean.TRUE.equals(ga.getActivo())).toList();
        }
        int materiasClonadas = 0;
        for (GradoAsignatura ga : planOrigen) {
            Optional<GradoAsignatura> existente = gradoAsignaturaRepository
                    .findByGradoIdAndAsignaturaIdAndAnioLectivoId(ga.getGrado().getId(), ga.getAsignatura().getId(), anioDestinoId);
            if (existente.isEmpty()) {
                GradoAsignatura nuevaGA = new GradoAsignatura();
                nuevaGA.setGrado(ga.getGrado());
                nuevaGA.setAsignatura(ga.getAsignatura());
                nuevaGA.setAnioLectivo(anioDestino);
                nuevaGA.setIntensidadHoraria(ga.getIntensidadHoraria());
                nuevaGA.setEsOptativa(ga.getEsOptativa());
                nuevaGA.setPesoPorcentualEnArea(ga.getPesoPorcentualEnArea());
                nuevaGA.setTipoComponente(ga.getTipoComponente());
                nuevaGA.setComputaPromocion(ga.getComputaPromocion());
                nuevaGA.setActivo(true);
                gradoAsignaturaRepository.save(nuevaGA);
                materiasClonadas++;
            }
        }

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CLONACION_ESTRUCTURA_ANIO",
                    "AnioLectivo",
                    anioDestinoId.toString(),
                    null,
                    "Clonados " + gruposClonados + " grupos y " + materiasClonadas + " asignaciones curriculares desde año " + anioOrigen.getAnio(),
                    "Proyección de estructura académica para nuevo año lectivo (UC-14 A1)",
                    "EXITOSO"
            );
        }

        return anioLectivoMapper.toResponse(anioDestino);
    }

    private boolean esEstadoCerrado(EstadoAnioLectivo estado) {
        return estado == EstadoAnioLectivo.CERRADO || estado == EstadoAnioLectivo.CERRADO_HISTORICO;
    }

    private void validarRangoFechas(java.time.LocalDate inicio, java.time.LocalDate fin) {
        if (inicio != null && fin != null && fin.isBefore(inicio)) {
            throw new ConflictoNegocioException("La fecha de fin no puede ser anterior a la fecha de inicio.");
        }
    }
}
