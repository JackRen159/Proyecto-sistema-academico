package jackren.sistemaacademico.main.repository;

import jackren.sistemaacademico.main.entity.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    boolean existsByCorreo(String correo);

    List<Estudiante> findByActivoTrue();
}
