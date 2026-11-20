package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Profesor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {

    Optional<Profesor> findByDocumento(String documento);

    Optional<Profesor> findByCorreo(String correo);

    Optional<Profesor> findByUsuarioId(Long usuarioId);

    boolean existsByDocumento(String documento);

    boolean existsByCorreo(String correo);

    boolean existsByUsuarioId(Long usuarioId);

    List<Profesor> findByActivoTrue();

    List<Profesor> findByEstadoAdministrativo(com.jackren.sistemaacademico.entity.EstadoAdministrativoDocente estadoAdministrativo);

    Page<Profesor> findByActivoTrue(Pageable pageable);
}
