package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AuditoriaEvento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditoriaEventoRepository extends JpaRepository<AuditoriaEvento, Long> {

    Optional<AuditoriaEvento> findTopByOrderByIdDesc();

    List<AuditoriaEvento> findByCorrelationIdOrderByFechaEventoAsc(String correlationId);

    List<AuditoriaEvento> findByEntidadAndEntidadIdOrderByFechaEventoAsc(String entidad, String entidadId);

    Page<AuditoriaEvento> findByModuloAndFechaEventoBetween(
            String modulo, LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    List<AuditoriaEvento> findAllByOrderByIdAsc();
}
