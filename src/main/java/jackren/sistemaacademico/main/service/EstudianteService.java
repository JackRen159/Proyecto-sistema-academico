package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.EstudianteRequest;
import jackren.sistemaacademico.main.dto.EstudianteResponse;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.exception.ConflictoNegocioException;
import jackren.sistemaacademico.main.exception.RecursoNoEncontradoException;
import jackren.sistemaacademico.main.mapper.EstudianteMapper;
import jackren.sistemaacademico.main.repository.EstudianteAcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final EstudianteMapper estudianteMapper;

    public EstudianteService(EstudianteRepository estudianteRepository,
                             EstudianteAcudienteRepository estudianteAcudienteRepository,
                             EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.estudianteMapper = estudianteMapper;
    }

    // Listado paginado de estudiantes activos
    public Page<EstudianteResponse> listarPaginado(Pageable pageable) {
        return estudianteRepository.findByActivoTrue(pageable)
                .map(estudianteMapper::toResponse);
    }

    // Obtener todos los estudiantes activos (sin paginacion)
    public List<EstudianteResponse> listarTodos() {
        return estudianteRepository.findByActivoTrue()
                .stream()
                .map(estudianteMapper::toResponse)
                .toList();
    }

    // Buscar un estudiante por ID con excepcion si no existe
    public EstudianteResponse obtenerPorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));
    }

    // Buscar un estudiante por ID (retorno Optional)
    public Optional<EstudianteResponse> buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::toResponse);
    }

    // Buscar un estudiante por documento con excepcion si no existe
    public EstudianteResponse obtenerPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento)
                .map(estudianteMapper::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documento));
    }

    // Buscar un estudiante por documento (retorno Optional)
    public Optional<EstudianteResponse> buscarPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento)
                .map(estudianteMapper::toResponse);
    }

    // Crear un estudiante
    @Transactional
    public EstudianteResponse guardar(EstudianteRequest request) {
        if (estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        if (correoNormalizado != null && estudianteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el correo: " + correoNormalizado);
        }

        Estudiante estudiante = estudianteMapper.toEntity(request);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteGuardado);
    }

    // Actualizar un estudiante existente
    @Transactional
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        // Validar unicidad de documento si cambio
        if (!estudiante.getDocumento().equals(request.getDocumento())
                && estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        String correoNormalizado = normalizarCorreo(request.getCorreo());
        request.setCorreo(correoNormalizado);

        // Validar unicidad de correo si cambio
        if (correoNormalizado != null
                && !correoNormalizado.equalsIgnoreCase(estudiante.getCorreo())
                && estudianteRepository.existsByCorreo(correoNormalizado)) {
            throw new ConflictoNegocioException("Ya existe un estudiante con el correo: " + correoNormalizado);
        }

        estudianteMapper.updateEntityFromRequest(estudiante, request);
        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteActualizado);
    }

    // Desactivar / Eliminar logicamente
    @Transactional
    public void desactivar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        estudiante.setActivo(false);
        estudianteRepository.save(estudiante);
    }

    // Deshacer eliminacion / Restaurar estudiante
    @Transactional
    public void restaurar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        estudiante.setActivo(true);
        estudianteRepository.save(estudiante);
    }

    // Eliminar definitivamente de la base de datos (IRREVERSIBLE - requiere confirmacion)
    @Transactional
    public void eliminarPermanente(Long id, boolean confirmar) {
        if (!confirmar) {
            throw new IllegalArgumentException("Debe confirmar explicitamente la eliminacion definitiva del estudiante.");
        }

        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con ID: " + id));

        if (estudianteAcudienteRepository.existsByEstudiante(estudiante)) {
            throw new ConflictoNegocioException("No se puede eliminar fisicamente el estudiante porque cuenta con vinculaciones registradas. Debe utilizar la desactivacion logica.");
        }

        estudianteRepository.delete(estudiante);
    }

    private String normalizarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return null;
        }
        return correo.trim().toLowerCase();
    }
}
