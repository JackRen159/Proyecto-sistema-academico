package jackren.sistemaacademico.main.service;

import jackren.sistemaacademico.main.dto.EstudianteAcudienteRequest;
import jackren.sistemaacademico.main.dto.EstudianteAcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.entity.EstudianteAcudiente;
import jackren.sistemaacademico.main.exception.ConflictoNegocioException;
import jackren.sistemaacademico.main.exception.RecursoNoEncontradoException;
import jackren.sistemaacademico.main.mapper.EstudianteAcudienteMapper;
import jackren.sistemaacademico.main.repository.AcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteAcudienteRepository;
import jackren.sistemaacademico.main.repository.EstudianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EstudianteAcudienteService {

    private final EstudianteAcudienteRepository estudianteAcudienteRepository;
    private final EstudianteRepository estudianteRepository;
    private final AcudienteRepository acudienteRepository;
    private final EstudianteAcudienteMapper estudianteAcudienteMapper;

    public EstudianteAcudienteService(EstudianteAcudienteRepository estudianteAcudienteRepository,
                                      EstudianteRepository estudianteRepository,
                                      AcudienteRepository acudienteRepository,
                                      EstudianteAcudienteMapper estudianteAcudienteMapper) {
        this.estudianteAcudienteRepository = estudianteAcudienteRepository;
        this.estudianteRepository = estudianteRepository;
        this.acudienteRepository = acudienteRepository;
        this.estudianteAcudienteMapper = estudianteAcudienteMapper;
    }

    // Asociar un acudiente a un estudiante mediante sus documentos
    @Transactional
    public EstudianteAcudienteResponse asociar(EstudianteAcudienteRequest request) {
        Estudiante estudiante = estudianteRepository.findByDocumento(request.getDocumentoEstudiante())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + request.getDocumentoEstudiante()));

        if (!estudiante.getActivo()) {
            throw new ConflictoNegocioException("El estudiante con documento " + request.getDocumentoEstudiante() + " se encuentra inactivo");
        }

        Acudiente acudiente = acudienteRepository.findByDocumento(request.getDocumentoAcudiente())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + request.getDocumentoAcudiente()));

        if (!acudiente.getActivo()) {
            throw new ConflictoNegocioException("El acudiente con documento " + request.getDocumentoAcudiente() + " se encuentra inactivo");
        }

        if (estudianteAcudienteRepository.existsByEstudianteAndAcudiente(estudiante, acudiente)) {
            throw new ConflictoNegocioException("El acudiente con documento " + request.getDocumentoAcudiente()
                    + " ya se encuentra vinculado al estudiante con documento " + request.getDocumentoEstudiante());
        }

        // Si es el primer acudiente del estudiante, obligatoriamente es el principal
        boolean esElPrimero = estudianteAcudienteRepository.countByEstudiante(estudiante) == 0;
        boolean marcarComoPrincipal = esElPrimero || Boolean.TRUE.equals(request.getEsPrincipal());

        // Si se marca como principal, desmarcar a cualquier acudiente principal previo
        if (marcarComoPrincipal) {
            estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)
                    .ifPresent(principalAnterior -> {
                        principalAnterior.setEsPrincipal(false);
                        estudianteAcudienteRepository.save(principalAnterior);
                    });
        }

        EstudianteAcudiente relacion = estudianteAcudienteMapper.toEntity(estudiante, acudiente, request);
        relacion.setEsPrincipal(marcarComoPrincipal);

        EstudianteAcudiente guardada = estudianteAcudienteRepository.save(relacion);
        return estudianteAcudienteMapper.toResponse(guardada);
    }

    // Listar todos los acudientes de un estudiante por su documento
    public List<EstudianteAcudienteResponse> listarAcudientesPorEstudiante(String documentoEstudiante) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        return estudianteAcudienteRepository.findByEstudiante(estudiante)
                .stream()
                .map(estudianteAcudienteMapper::toResponse)
                .toList();
    }

    // Listar todos los estudiantes a cargo de un acudiente por su documento
    public List<EstudianteAcudienteResponse> listarEstudiantesPorAcudiente(String documentoAcudiente) {
        Acudiente acudiente = acudienteRepository.findByDocumento(documentoAcudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoAcudiente));

        return estudianteAcudienteRepository.findByAcudiente(acudiente)
                .stream()
                .map(estudianteAcudienteMapper::toResponse)
                .toList();
    }

    // Cambiar el acudiente principal de un estudiante
    @Transactional
    public EstudianteAcudienteResponse cambiarAcudientePrincipal(String documentoEstudiante, String documentoNuevoPrincipal) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        Acudiente nuevoPrincipal = acudienteRepository.findByDocumento(documentoNuevoPrincipal)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoNuevoPrincipal));

        EstudianteAcudiente relacionNuevoPrincipal = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, nuevoPrincipal)
                .orElseThrow(() -> new ConflictoNegocioException("El acudiente no esta vinculado a este estudiante"));

        if (Boolean.TRUE.equals(relacionNuevoPrincipal.getEsPrincipal())) {
            return estudianteAcudienteMapper.toResponse(relacionNuevoPrincipal);
        }

        // Desmarcar al anterior principal
        estudianteAcudienteRepository.findByEstudianteAndEsPrincipalTrue(estudiante)
                .ifPresent(principalAnterior -> {
                    principalAnterior.setEsPrincipal(false);
                    estudianteAcudienteRepository.save(principalAnterior);
                });

        relacionNuevoPrincipal.setEsPrincipal(true);
        EstudianteAcudiente guardada = estudianteAcudienteRepository.save(relacionNuevoPrincipal);
        return estudianteAcudienteMapper.toResponse(guardada);
    }

    // Desvincular un acudiente de un estudiante
    @Transactional
    public void desvincular(String documentoEstudiante, String documentoAcudiente) {
        Estudiante estudiante = estudianteRepository.findByDocumento(documentoEstudiante)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el estudiante con documento: " + documentoEstudiante));

        Acudiente acudiente = acudienteRepository.findByDocumento(documentoAcudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontro el acudiente con documento: " + documentoAcudiente));

        EstudianteAcudiente relacion = estudianteAcudienteRepository.findByEstudianteAndAcudiente(estudiante, acudiente)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe vinculo entre el estudiante y el acudiente especificados"));

        // Regla institucional: Si es el acudiente principal y hay otros acudientes, debe reemplazarse primero
        if (Boolean.TRUE.equals(relacion.getEsPrincipal())) {
            long totalAcudientes = estudianteAcudienteRepository.countByEstudiante(estudiante);
            if (totalAcudientes > 1) {
                throw new ConflictoNegocioException("No se puede desvincular al acudiente principal. Primero debe designar a otro de sus acudientes como principal.");
            }
        }

        estudianteAcudienteRepository.delete(relacion);
    }
}
