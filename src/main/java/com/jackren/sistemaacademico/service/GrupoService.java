package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.GrupoRequest;
import com.jackren.sistemaacademico.dto.GrupoResponse;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.GrupoMapper;
import com.jackren.sistemaacademico.repository.AnioLectivoRepository;
import com.jackren.sistemaacademico.repository.GradoRepository;
import com.jackren.sistemaacademico.repository.GrupoRepository;
import com.jackren.sistemaacademico.repository.MatriculaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final AnioLectivoRepository anioLectivoRepository;
    private final GradoRepository gradoRepository;
    private final MatriculaRepository matriculaRepository;
    private final GrupoMapper grupoMapper;
    private final AuditoriaGlobalService auditoriaGlobalService;

    public GrupoService(GrupoRepository grupoRepository,
                        AnioLectivoRepository anioLectivoRepository,
                        GradoRepository gradoRepository,
                        MatriculaRepository matriculaRepository,
                        GrupoMapper grupoMapper,
                        AuditoriaGlobalService auditoriaGlobalService) {
        this.grupoRepository = grupoRepository;
        this.anioLectivoRepository = anioLectivoRepository;
        this.gradoRepository = gradoRepository;
        this.matriculaRepository = matriculaRepository;
        this.grupoMapper = grupoMapper;
        this.auditoriaGlobalService = auditoriaGlobalService;
    }

    public List<GrupoResponse> listarPorAnio(Long anioLectivoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        return grupoRepository.findByAnioLectivoIdAndActivoTrue(anioLectivoId)
                .stream()
                .map(this::convertirAResponseConCupos)
                .toList();
    }

    public List<GrupoResponse> listarPorAnioYGrado(Long anioLectivoId, Long gradoId) {
        if (!anioLectivoRepository.existsById(anioLectivoId)) {
            throw new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + anioLectivoId);
        }

        if (!gradoRepository.existsById(gradoId)) {
            throw new RecursoNoEncontradoException("No se encontró el grado con ID: " + gradoId);
        }

        return grupoRepository.findByAnioLectivoIdAndGradoIdAndActivoTrue(anioLectivoId, gradoId)
                .stream()
                .map(this::convertirAResponseConCupos)
                .toList();
    }

    public GrupoResponse obtenerPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));
        return convertirAResponseConCupos(grupo);
    }

    @Transactional
    public GrupoResponse guardar(GrupoRequest request) {
        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + request.getAnioLectivoId()));

        if (anio.getEstado() == EstadoAnioLectivo.CERRADO || anio.getEstado() == EstadoAnioLectivo.CERRADO_HISTORICO) {
            throw new ConflictoNegocioException("No se pueden crear grupos en un año lectivo que ya ha sido CERRADO.");
        }

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        String nombreNormalizado = request.getNombre().trim().toUpperCase();
        request.setNombre(nombreNormalizado);

        if (grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(anio.getId(), grado.getId(), nombreNormalizado)) {
            throw new ConflictoNegocioException("Ya existe el grupo '" + nombreNormalizado + "' para el grado "
                    + grado.getNombre() + " en el año lectivo " + anio.getAnio() + " (UC-PROV-06 E3).");
        }

        Grupo grupo = grupoMapper.toEntity(request, anio, grado);
        Grupo guardado = grupoRepository.save(grupo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "CREACION_GRUPO",
                    "Grupo",
                    guardado.getId().toString(),
                    null,
                    "Grupo " + guardado.getNombre() + " (" + grado.getNombre() + ", cupo: " + guardado.getCupoMaximo() + ")",
                    "Creación y apertura de oferta de grupo",
                    "EXITOSO"
            );
        }

        return convertirAResponseConCupos(guardado);
    }

    @Transactional
    public GrupoResponse actualizar(Long id, GrupoRequest request) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));

        AnioLectivo anio = anioLectivoRepository.findById(request.getAnioLectivoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el año lectivo con ID: " + request.getAnioLectivoId()));

        Grado grado = gradoRepository.findById(request.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grado con ID: " + request.getGradoId()));

        // Validar que el cupo no se reduzca por debajo de las matrículas actuales (UC-PROV-06 E1)
        long matriculadosActivos = matriculaRepository != null ? matriculaRepository.countByGrupoIdAndEstado(id, EstadoMatricula.MATRICULADA) : 0;
        if (request.getCupoMaximo() < matriculadosActivos) {
            throw new ConflictoNegocioException("No se puede reducir el cupo máximo a " + request.getCupoMaximo()
                    + " porque el grupo ya cuenta con " + matriculadosActivos + " estudiantes formalmente matriculados (UC-PROV-06 E1).");
        }

        String nombreNormalizado = request.getNombre().trim().toUpperCase();
        request.setNombre(nombreNormalizado);

        boolean cambioClave = !grupo.getAnioLectivo().getId().equals(anio.getId())
                || !grupo.getGrado().getId().equals(grado.getId())
                || !grupo.getNombre().equalsIgnoreCase(nombreNormalizado);

        if (cambioClave && grupoRepository.existsByAnioLectivoIdAndGradoIdAndNombre(anio.getId(), grado.getId(), nombreNormalizado)) {
            throw new ConflictoNegocioException("Ya existe el grupo '" + nombreNormalizado + "' para el grado "
                    + grado.getNombre() + " en el año lectivo " + anio.getAnio() + " (UC-PROV-06 E3).");
        }

        String valorAnterior = "Nombre: " + grupo.getNombre() + ", Cupo: " + grupo.getCupoMaximo() + ", Jornada: " + grupo.getJornada();
        grupoMapper.updateEntityFromRequest(grupo, request, anio, grado);
        Grupo actualizado = grupoRepository.save(grupo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "ACTUALIZACION_GRUPO",
                    "Grupo",
                    actualizado.getId().toString(),
                    valorAnterior,
                    "Nombre: " + actualizado.getNombre() + ", Cupo: " + actualizado.getCupoMaximo() + ", Jornada: " + actualizado.getJornada(),
                    "Actualización de parámetros de grupo",
                    "EXITOSO"
            );
        }

        return convertirAResponseConCupos(actualizado);
    }

    @Transactional
    public void desactivar(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));

        // Bloqueo de cierre/desactivación con estudiantes matriculados (UC-PROV-06 E2)
        long matriculadosActivos = matriculaRepository != null ? matriculaRepository.countByGrupoIdAndEstado(id, EstadoMatricula.MATRICULADA) : 0;
        if (matriculadosActivos > 0) {
            throw new ConflictoNegocioException("No se puede desactivar o cerrar el grupo " + grupo.getNombre()
                    + " porque cuenta con " + matriculadosActivos + " estudiantes matriculados activos. Deben reubicarse previamente (UC-PROV-06 E2).");
        }

        grupo.setActivo(false);
        grupo.setEstado(EstadoGrupo.CERRADO_SIN_CUPOS);
        grupoRepository.save(grupo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "DESACTIVACION_GRUPO",
                    "Grupo",
                    id.toString(),
                    "Activo: true, Estado: " + grupo.getEstado(),
                    "Activo: false, Estado: CERRADO_SIN_CUPOS",
                    "Cierre o inactivación de grupo sin cupos",
                    "EXITOSO"
            );
        }
    }

    @Transactional
    public void activar(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el grupo con ID: " + id));
        grupo.setActivo(true);
        grupo.setEstado(EstadoGrupo.ACTIVO);
        grupoRepository.save(grupo);

        if (auditoriaGlobalService != null) {
            auditoriaGlobalService.registrarEvento(
                    "REACTIVACION_GRUPO",
                    "Grupo",
                    id.toString(),
                    "Activo: false",
                    "Activo: true, Estado: ACTIVO",
                    "Reactivación de oferta de grupo",
                    "EXITOSO"
            );
        }
    }

    private GrupoResponse convertirAResponseConCupos(Grupo grupo) {
        int matriculados = 0;
        if (matriculaRepository != null && grupo.getId() != null) {
            matriculados = (int) matriculaRepository.countByGrupoIdAndEstado(grupo.getId(), EstadoMatricula.MATRICULADA);
        }
        return grupoMapper.toResponse(grupo, matriculados);
    }
}
