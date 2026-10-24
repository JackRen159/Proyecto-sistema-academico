package jackren.sistemaacademico.main.mapper;

import jackren.sistemaacademico.main.dto.EstudianteAcudienteRequest;
import jackren.sistemaacademico.main.dto.EstudianteAcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.entity.EstudianteAcudiente;
import org.springframework.stereotype.Component;

@Component
public class EstudianteAcudienteMapper {

    public EstudianteAcudiente toEntity(Estudiante estudiante, Acudiente acudiente, EstudianteAcudienteRequest request) {
        if (estudiante == null || acudiente == null || request == null) {
            return null;
        }

        EstudianteAcudiente relacion = new EstudianteAcudiente();
        relacion.setEstudiante(estudiante);
        relacion.setAcudiente(acudiente);
        relacion.setParentesco(request.getParentesco());
        relacion.setEsPrincipal(request.getEsPrincipal());
        relacion.setConviveConEstudiante(request.getConviveConEstudiante());
        relacion.setAutorizadoRetiro(request.getAutorizadoRetiro());

        return relacion;
    }

    public EstudianteAcudienteResponse toResponse(EstudianteAcudiente relacion) {
        if (relacion == null) {
            return null;
        }

        Estudiante estudiante = relacion.getEstudiante();
        Acudiente acudiente = relacion.getAcudiente();

        return new EstudianteAcudienteResponse(
                relacion.getId(),
                estudiante.getId(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getDocumento(),
                acudiente.getId(),
                acudiente.getNombres(),
                acudiente.getApellidos(),
                acudiente.getDocumento(),
                acudiente.getTelefono(),
                acudiente.getTelefonoAlternativo(),
                acudiente.getCorreo(),
                relacion.getParentesco(),
                relacion.getEsPrincipal(),
                relacion.getConviveConEstudiante(),
                relacion.getAutorizadoRetiro(),
                relacion.getFechaVinculacion()
        );
    }
}

