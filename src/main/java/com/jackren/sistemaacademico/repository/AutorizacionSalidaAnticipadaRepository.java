package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AutorizacionSalidaAnticipada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AutorizacionSalidaAnticipadaRepository extends JpaRepository<AutorizacionSalidaAnticipada, Long> {

    Optional<AutorizacionSalidaAnticipada> findByNumeroAutorizacion(String numeroAutorizacion);

    List<AutorizacionSalidaAnticipada> findByMatriculaIdOrderByFechaDesc(Long matriculaId);

    List<AutorizacionSalidaAnticipada> findByFecha(LocalDate fecha);

    Optional<AutorizacionSalidaAnticipada> findByMatriculaIdAndFecha(Long matriculaId, LocalDate fecha);
}
