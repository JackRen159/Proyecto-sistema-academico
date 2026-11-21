package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AnotacionObservadorAdenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnotacionObservadorAdendaRepository extends JpaRepository<AnotacionObservadorAdenda, Long> {
    List<AnotacionObservadorAdenda> findByAnotacionIdOrderByFechaAdendaAsc(Long anotacionId);
    List<AnotacionObservadorAdenda> findByAutorUsuarioId(Long autorUsuarioId);
}
