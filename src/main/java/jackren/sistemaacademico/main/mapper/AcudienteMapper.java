package jackren.sistemaacademico.main.mapper;

import jackren.sistemaacademico.main.dto.AcudienteRequest;
import jackren.sistemaacademico.main.dto.AcudienteResponse;
import jackren.sistemaacademico.main.entity.Acudiente;
import org.springframework.stereotype.Component;

@Component
public class AcudienteMapper {

    // Convierte un DTO de creación/edición a la Entidad JPA
    public Acudiente toEntity(AcudienteRequest request) {
        if (request == null) {
            return null;
        }

        Acudiente acudiente = new Acudiente();
        acudiente.setNombres(request.getNombres());
        acudiente.setApellidos(request.getApellidos());
        acudiente.setDocumento(request.getDocumento());
        acudiente.setTelefono(request.getTelefono());
        acudiente.setTelefonoAlternativo(request.getTelefonoAlternativo());
        acudiente.setCorreo(request.getCorreo());
        acudiente.setDireccion(request.getDireccion());
        acudiente.setOcupacion(request.getOcupacion());

        return acudiente;
    }

    // Actualiza los campos de una entidad existente a partir de los datos del DTO
    public void updateEntityFromRequest(Acudiente acudiente, AcudienteRequest request) {
        if (acudiente == null || request == null) {
            return;
        }

        acudiente.setNombres(request.getNombres());
        acudiente.setApellidos(request.getApellidos());
        acudiente.setDocumento(request.getDocumento());
        acudiente.setTelefono(request.getTelefono());
        acudiente.setTelefonoAlternativo(request.getTelefonoAlternativo());
        acudiente.setCorreo(request.getCorreo());
        acudiente.setDireccion(request.getDireccion());
        acudiente.setOcupacion(request.getOcupacion());
    }

    // Convierte la Entidad JPA al DTO de respuesta
    public AcudienteResponse toResponse(Acudiente acudiente) {
        if (acudiente == null) {
            return null;
        }

        return new AcudienteResponse(
                acudiente.getId(),
                acudiente.getNombres(),
                acudiente.getApellidos(),
                acudiente.getDocumento(),
                acudiente.getTelefono(),
                acudiente.getTelefonoAlternativo(),
                acudiente.getCorreo(),
                acudiente.getDireccion(),
                acudiente.getOcupacion(),
                acudiente.getActivo(),
                acudiente.getFechaCreacion(),
                acudiente.getFechaActualizacion()
        );
    }
}
