package jackren.sistemaacademico.main.repository;

import jackren.sistemaacademico.main.entity.Acudiente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcudienteRepository extends JpaRepository<Acudiente, Long> {

    Optional<Acudiente> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    boolean existsByCorreo(String correo);

    List<Acudiente> findByActivoTrue();
}
