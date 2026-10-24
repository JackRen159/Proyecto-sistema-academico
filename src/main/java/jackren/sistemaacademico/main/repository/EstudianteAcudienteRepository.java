package jackren.sistemaacademico.main.repository;

import jackren.sistemaacademico.main.entity.Acudiente;
import jackren.sistemaacademico.main.entity.Estudiante;
import jackren.sistemaacademico.main.entity.EstudianteAcudiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteAcudienteRepository extends JpaRepository<EstudianteAcudiente, Long> {

    // Lista todos los vínculos de un estudiante (para ver sus acudientes)
    List<EstudianteAcudiente> findByEstudiante(Estudiante estudiante);

    // Lista todos los vínculos de un acudiente (para ver sus estudiantes)
    List<EstudianteAcudiente> findByAcudiente(Acudiente acudiente);

    // Verifica si ya existe el vínculo entre un estudiante y un acudiente
    boolean existsByEstudianteAndAcudiente(Estudiante estudiante, Acudiente acudiente);

    // Obtiene el vínculo específico entre un estudiante y un acudiente
    Optional<EstudianteAcudiente> findByEstudianteAndAcudiente(Estudiante estudiante, Acudiente acudiente);

    // Obtiene el acudiente principal de un estudiante (para validar el reemplazo)
    Optional<EstudianteAcudiente> findByEstudianteAndEsPrincipalTrue(Estudiante estudiante);

    // Cuenta cuántos acudientes tiene un estudiante (para reglas de negocio)
    long countByEstudiante(Estudiante estudiante);
}

