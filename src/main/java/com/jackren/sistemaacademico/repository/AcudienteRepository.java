package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Acudiente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AcudienteRepository extends JpaRepository<Acudiente, Long> {

    Optional<Acudiente> findByDocumento(String documento);

    Optional<Acudiente> findByCorreo(String correo);

    boolean existsByDocumento(String documento);

    boolean existsByCorreo(String correo);

    List<Acudiente> findByActivoTrue();

    Page<Acudiente> findByActivoTrue(Pageable pageable);
}
