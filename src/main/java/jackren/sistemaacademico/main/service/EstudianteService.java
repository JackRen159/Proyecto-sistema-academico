package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.EstudianteRequest;
import jackren.sistemaacademico.main.dto.EstudianteResponse;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.repository.EstudianteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;

    public EstudianteService(EstudianteRepository estudianteRepository) {
        this.estudianteRepository = estudianteRepository;
    }

    // Obtener todos los estudiantes
    public List<EstudianteResponse> listarTodos() {

        return estudianteRepository.findAll()
                .stream()
                .map(this::convertirAResponse)
                .toList();
    }

    // Buscar un estudiante por ID
    public Optional<EstudianteResponse> buscarPorId(Long id) {

        return estudianteRepository.findById(id)
                .map(this::convertirAResponse);
    }

    // Crear un estudiante
    public EstudianteResponse guardar(EstudianteRequest request) {

        Estudiante estudiante = new Estudiante();

        estudiante.setNombres(request.getNombres());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setDocumento(request.getDocumento());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setTelefono(request.getTelefono());

        Estudiante estudianteGuardado = estudianteRepository.save(estudiante);

        return convertirAResponse(estudianteGuardado);
    }

    // Convertir Entity → Response
    private EstudianteResponse convertirAResponse(Estudiante estudiante) {

        return new EstudianteResponse(
                estudiante.getId(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getDocumento(),
                estudiante.getFechaNacimiento(),
                estudiante.getCorreo(),
                estudiante.getTelefono(),
                estudiante.getActivo()
        );
    }

    // Eliminar un estudiante
    public void eliminar(Long id) {
        estudianteRepository.deleteById(id);
    }
}