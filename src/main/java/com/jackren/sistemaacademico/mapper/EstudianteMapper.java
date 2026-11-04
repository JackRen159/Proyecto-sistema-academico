package com.jackren.sistemaacademico.mapper;

import com.jackren.sistemaacademico.dto.EstudianteRequest;
import com.jackren.sistemaacademico.dto.EstudianteResponse;
import com.jackren.sistemaacademico.entity.Estudiante;
import org.springframework.stereotype.Component;

@Component
public class EstudianteMapper {

    // Convierte un DTO de creación/edición a la Entidad JPA
    public Estudiante toEntity(EstudianteRequest request) {
        if (request == null) {
            return null;
        }

        Estudiante estudiante = new Estudiante();
        estudiante.setNombres(request.getNombres());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setDocumento(request.getDocumento());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());

        return estudiante;
    }

    // Actualiza los campos de una entidad existente a partir de los datos del DTO
    public void updateEntityFromRequest(Estudiante estudiante, EstudianteRequest request) {
        if (estudiante == null || request == null) {
            return;
        }

        estudiante.setNombres(request.getNombres());
        estudiante.setApellidos(request.getApellidos());
        estudiante.setDocumento(request.getDocumento());
        estudiante.setFechaNacimiento(request.getFechaNacimiento());
        estudiante.setCorreo(request.getCorreo());
        estudiante.setTelefono(request.getTelefono());
        estudiante.setDireccion(request.getDireccion());
    }

    // Convierte la Entidad JPA al DTO de respuesta
    public EstudianteResponse toResponse(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }

        return new EstudianteResponse(
                estudiante.getId(),
                estudiante.getNombres(),
                estudiante.getApellidos(),
                estudiante.getDocumento(),
                estudiante.getFechaNacimiento(),
                estudiante.getCorreo(),
                estudiante.getTelefono(),
                estudiante.getDireccion(),
                estudiante.getActivo(),
                estudiante.getFechaCreacion(),
                estudiante.getFechaActualizacion()
        );
    }
}
