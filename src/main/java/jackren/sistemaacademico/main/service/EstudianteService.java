package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.EstudianteRequest;
import jackren.sistemaacademico.main.dto.EstudianteResponse;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.mapper.EstudianteMapper;
import jackren.sistemaacademico.main.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final EstudianteMapper estudianteMapper;

    public EstudianteService(EstudianteRepository estudianteRepository, EstudianteMapper estudianteMapper) {
        this.estudianteRepository = estudianteRepository;
        this.estudianteMapper = estudianteMapper;
    }

    // Obtener todos los estudiantes activos
    public List<EstudianteResponse> listarTodos() {
        return estudianteRepository.findByActivoTrue()
                .stream()
                .map(estudianteMapper::toResponse)
                .toList();
    }

    // Buscar un estudiante por ID
    public Optional<EstudianteResponse> buscarPorId(Long id) {
        return estudianteRepository.findById(id)
                .map(estudianteMapper::toResponse);
    }

    // Buscar un estudiante por documento
    public Optional<EstudianteResponse> buscarPorDocumento(String documento) {
        return estudianteRepository.findByDocumento(documento)
                .map(estudianteMapper::toResponse);
    }

    // Crear un estudiante
    @Transactional
    public EstudianteResponse guardar(EstudianteRequest request) {
        if (estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        if (request.getCorreo() != null && !request.getCorreo().isBlank()
                && estudianteRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un estudiante con el correo: " + request.getCorreo());
        }

        Estudiante estudiante = estudianteMapper.toEntity(request);
        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteGuardado);
    }

    // Actualizar un estudiante existente
    @Transactional
    public EstudianteResponse actualizar(Long id, EstudianteRequest request) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + id));

        // Validar unicidad de documento si cambió
        if (!estudiante.getDocumento().equals(request.getDocumento())
                && estudianteRepository.existsByDocumento(request.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un estudiante con el documento: " + request.getDocumento());
        }

        // Validar unicidad de correo si cambió
        if (request.getCorreo() != null && !request.getCorreo().isBlank()
                && !request.getCorreo().equalsIgnoreCase(estudiante.getCorreo())
                && estudianteRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("Ya existe un estudiante con el correo: " + request.getCorreo());
        }

        estudianteMapper.updateEntityFromRequest(estudiante, request);
        Estudiante estudianteActualizado = estudianteRepository.save(estudiante);

        return estudianteMapper.toResponse(estudianteActualizado);
    }

    // Desactivar / Eliminar lógicamente (se puede deshacer con restaurar)
    @Transactional
    public void desactivar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + id));

        estudiante.setActivo(false);
        estudianteRepository.save(estudiante);
    }

    // Deshacer eliminación / Restaurar estudiante
    @Transactional
    public void restaurar(Long id) {
        Estudiante estudiante = estudianteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el estudiante con ID: " + id));

        estudiante.setActivo(true);
        estudianteRepository.save(estudiante);
    }

    // Eliminar definitivamente de la base de datos (IRREVERSIBLE - requiere confirmación)
    @Transactional
    public void eliminarPermanente(Long id, boolean confirmar) {
        if (!confirmar) {
            throw new IllegalArgumentException("Debe confirmar explícitamente la eliminación definitiva del estudiante.");
        }

        if (!estudianteRepository.existsById(id)) {
            throw new IllegalArgumentException("No se encontró el estudiante con ID: " + id);
        }

        estudianteRepository.deleteById(id);
    }
}