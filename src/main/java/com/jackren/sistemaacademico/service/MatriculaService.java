package com.jackren.sistemaacademico.service;

import com.jackren.sistemaacademico.dto.*;
import com.jackren.sistemaacademico.entity.*;
import com.jackren.sistemaacademico.exception.ConflictoNegocioException;
import com.jackren.sistemaacademico.exception.RecursoNoEncontradoException;
import com.jackren.sistemaacademico.mapper.MatriculaMapper;
import com.jackren.sistemaacademico.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final HistorialTrasladoMatriculaRepository historialTrasladoRepository;
    private final EstudianteRepository estudianteRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final MatriculaMapper matriculaMapper;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            HistorialTrasladoMatriculaRepository historialTrasladoRepository,
                            EstudianteRepository estudianteRepository,
                            GrupoRepository grupoRepository,
                            UsuarioRepository usuarioRepository,
                            MatriculaMapper matriculaMapper) {
        this.matriculaRepository = matriculaRepository;
        this.historialTrasladoRepository = historialTrasladoRepository;
        this.estudianteRepository = estudianteRepository;
        this.grupoRepository = grupoRepository;
        this.usuarioRepository = usuarioRepository;
        this.matriculaMapper = matriculaMapper;
    }

    public MatriculaResponse matricular(MatriculaRequest request) {
        Estudiante estudiante = estudianteRepository.findById(request.getEstudianteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Estudiante no encontrado con ID " + request.getEstudianteId()));

        if (!Boolean.TRUE.equals(estudiante.getActivo())) {
            throw new ConflictoNegocioException("No se puede matricular un estudiante en estado inactivo");
        }

        Grupo grupo = grupoRepository.findById(request.getGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID " + request.getGrupoId()));

        if (!Boolean.TRUE.equals(grupo.getActivo())) {
            throw new ConflictoNegocioException("No se pueden realizar matrículas en un grupo inactivo");
        }

        AnioLectivo anioLectivo = grupo.getAnioLectivo();
        if (anioLectivo == null || anioLectivo.getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden realizar matrículas en un año lectivo cerrado");
        }

        if (matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(estudiante.getId(), anioLectivo.getId(), EstadoMatricula.MATRICULADA)) {
            throw new ConflictoNegocioException("El estudiante ya cuenta con una matrícula activa en el año lectivo " + anioLectivo.getAnio());
        }

        long matriculadosActuales = matriculaRepository.countByGrupoIdAndEstado(grupo.getId(), EstadoMatricula.MATRICULADA);
        int cupoMaximo = grupo.getCupoMaximo() != null ? grupo.getCupoMaximo() : 35;
        if (matriculadosActuales >= cupoMaximo) {
            throw new ConflictoNegocioException("El grupo '" + grupo.getNombre() + "' ha alcanzado su cupo máximo de " + cupoMaximo + " estudiantes");
        }

        String numeroMatricula = generarNumeroMatricula(anioLectivo);
        Matricula matricula = matriculaMapper.toEntity(request, estudiante, grupo, numeroMatricula);
        Matricula guardada = matriculaRepository.save(matricula);
        return matriculaMapper.toResponse(guardada);
    }

    public MatriculaResponse trasladarDeGrupo(Long matriculaId, TrasladoGrupoRequest request, Long usuarioId) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + matriculaId));

        if (matricula.getEstado() != EstadoMatricula.MATRICULADA) {
            throw new ConflictoNegocioException("Solo se pueden trasladar estudiantes con matrícula en estado MATRICULADA");
        }

        Grupo grupoOrigen = matricula.getGrupo();
        if (grupoOrigen.getId().equals(request.getNuevoGrupoId())) {
            throw new ConflictoNegocioException("El grupo destino debe ser diferente al grupo actual");
        }

        Grupo grupoDestino = grupoRepository.findById(request.getNuevoGrupoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo destino no encontrado con ID " + request.getNuevoGrupoId()));

        if (!Boolean.TRUE.equals(grupoDestino.getActivo())) {
            throw new ConflictoNegocioException("No se puede trasladar a un grupo inactivo");
        }

        if (grupoDestino.getAnioLectivo() == null || grupoDestino.getAnioLectivo().getEstado() == EstadoAnioLectivo.CERRADO) {
            throw new ConflictoNegocioException("No se pueden realizar traslados en un año lectivo cerrado");
        }

        if (!grupoDestino.getAnioLectivo().getId().equals(matricula.getAnioLectivo().getId())) {
            throw new ConflictoNegocioException("El grupo destino debe pertenecer al mismo año lectivo de la matrícula");
        }

        if (!grupoDestino.getGrado().getId().equals(grupoOrigen.getGrado().getId())) {
            throw new ConflictoNegocioException("El traslado interno de grupo solo está permitido entre cursos del mismo grado (" +
                    grupoOrigen.getGrado().getNombre() + ")");
        }

        long matriculadosDestino = matriculaRepository.countByGrupoIdAndEstado(grupoDestino.getId(), EstadoMatricula.MATRICULADA);
        int cupoMaximoDestino = grupoDestino.getCupoMaximo() != null ? grupoDestino.getCupoMaximo() : 35;
        if (matriculadosDestino >= cupoMaximoDestino) {
            throw new ConflictoNegocioException("El grupo destino '" + grupoDestino.getNombre() + "' ha alcanzado su cupo máximo de " +
                    cupoMaximoDestino + " estudiantes");
        }

        Usuario usuario = null;
        if (usuarioId != null) {
            usuario = usuarioRepository.findById(usuarioId).orElse(null);
        }

        HistorialTrasladoMatricula historial = new HistorialTrasladoMatricula(
                matricula, grupoOrigen, grupoDestino, request.getMotivo(), usuario
        );
        historialTrasladoRepository.save(historial);

        matricula.setGrupo(grupoDestino);
        Matricula actualizada = matriculaRepository.save(matricula);
        return matriculaMapper.toResponse(actualizada);
    }

    public MatriculaResponse cambiarEstado(Long matriculaId, CambioEstadoMatriculaRequest request) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + matriculaId));

        EstadoMatricula estadoAnterior = matricula.getEstado();
        EstadoMatricula nuevoEstado = request.getNuevoEstado();

        if (estadoAnterior == nuevoEstado) {
            return matriculaMapper.toResponse(matricula);
        }

        if (nuevoEstado == EstadoMatricula.MATRICULADA && estadoAnterior != EstadoMatricula.MATRICULADA) {
            AnioLectivo anio = matricula.getAnioLectivo();
            if (anio != null && anio.getEstado() == EstadoAnioLectivo.CERRADO) {
                throw new ConflictoNegocioException("No se puede reactivar una matrícula en un año lectivo cerrado");
            }

            if (matriculaRepository.existsByEstudianteIdAndAnioLectivoIdAndEstado(
                    matricula.getEstudiante().getId(), matricula.getAnioLectivo().getId(), EstadoMatricula.MATRICULADA)) {
                throw new ConflictoNegocioException("El estudiante ya cuenta con otra matrícula activa en este año lectivo");
            }

            long cupoActual = matriculaRepository.countByGrupoIdAndEstado(matricula.getGrupo().getId(), EstadoMatricula.MATRICULADA);
            int cupoMax = matricula.getGrupo().getCupoMaximo() != null ? matricula.getGrupo().getCupoMaximo() : 35;
            if (cupoActual >= cupoMax) {
                throw new ConflictoNegocioException("El grupo ha alcanzado su cupo máximo de " + cupoMax + " estudiantes");
            }
        }

        matricula.setEstado(nuevoEstado);
        if (request.getMotivo() != null && !request.getMotivo().isBlank()) {
            String obsPrevia = matricula.getObservaciones() != null ? matricula.getObservaciones() + " | " : "";
            matricula.setObservaciones(obsPrevia + "Estado cambiado a " + nuevoEstado + ": " + request.getMotivo());
        }

        Matricula guardada = matriculaRepository.save(matricula);
        return matriculaMapper.toResponse(guardada);
    }

    @Transactional(readOnly = true)
    public MatriculaResponse obtenerPorId(Long id) {
        Matricula matricula = matriculaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con ID " + id));
        return matriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public MatriculaResponse obtenerPorNumero(String numeroMatricula) {
        Matricula matricula = matriculaRepository.findByNumeroMatricula(numeroMatricula)
                .orElseThrow(() -> new RecursoNoEncontradoException("Matrícula no encontrada con número " + numeroMatricula));
        return matriculaMapper.toResponse(matricula);
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorGrupo(Long grupoId) {
        return matriculaRepository.findByGrupoIdAndEstado(grupoId, EstadoMatricula.MATRICULADA).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MatriculaResponse> listarPorEstudiante(Long estudianteId) {
        return matriculaRepository.findByEstudianteIdOrderByFechaMatriculaDesc(estudianteId).stream()
                .map(matriculaMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<MatriculaResponse> listarPorAnioLectivo(Long anioLectivoId, Pageable pageable) {
        return matriculaRepository.findByAnioLectivoId(anioLectivoId, pageable)
                .map(matriculaMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<HistorialTrasladoResponse> listarHistorialTraslados(Long matriculaId) {
        return historialTrasladoRepository.findByMatriculaIdOrderByFechaTrasladoDesc(matriculaId).stream()
                .map(matriculaMapper::toHistorialResponse)
                .toList();
    }

    /**
     * Regla de validación contextual para SIEE y Asistencia (Spec S2):
     * Valida si un estudiante cuenta con matrícula activa en el grupo especificado.
     */
    @Transactional(readOnly = true)
    public boolean tieneMatriculaActivaEnGrupo(Long estudianteId, Long grupoId) {
        if (estudianteId == null || grupoId == null) {
            return false;
        }
        return matriculaRepository.existsByEstudianteIdAndGrupoIdAndEstado(
                estudianteId, grupoId, EstadoMatricula.MATRICULADA
        );
    }

    private String generarNumeroMatricula(AnioLectivo anioLectivo) {
        int anio = anioLectivo != null ? anioLectivo.getAnio() : 2026;
        long conteo = matriculaRepository.countByAnioLectivoId(anioLectivo != null ? anioLectivo.getId() : 0L);
        String numero = String.format("MAT-%d-%04d", anio, conteo + 1);
        int intento = 1;
        while (matriculaRepository.existsByNumeroMatricula(numero)) {
            numero = String.format("MAT-%d-%04d-%d", anio, conteo + 1, intento++);
        }
        return numero;
    }
}
