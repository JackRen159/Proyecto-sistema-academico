package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConsentimientoPoliticaDatos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsentimientoPoliticaDatosRepository extends JpaRepository<ConsentimientoPoliticaDatos, Long> {

    Optional<ConsentimientoPoliticaDatos> findByUsuarioIdAndPoliticaId(Long usuarioId, Long politicaId);

    boolean existsByUsuarioIdAndPoliticaId(Long usuarioId, Long politicaId);

    List<ConsentimientoPoliticaDatos> findByUsuarioIdOrderByFechaAceptacionDesc(Long usuarioId);
}
