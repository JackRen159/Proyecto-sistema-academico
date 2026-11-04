package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Estudiante;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByDocumento(String documento);

    boolean existsByDocumento(String documento);

    boolean existsByCorreo(String correo);

    List<Estudiante> findByActivoTrue();

    Page<Estudiante> findByActivoTrue(Pageable pageable);
}
