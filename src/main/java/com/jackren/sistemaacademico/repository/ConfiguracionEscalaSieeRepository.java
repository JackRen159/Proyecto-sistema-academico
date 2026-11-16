package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConfiguracionEscalaSiee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionEscalaSieeRepository extends JpaRepository<ConfiguracionEscalaSiee, Long> {

    Optional<ConfiguracionEscalaSiee> findByAnioLectivoId(Long anioLectivoId);

    boolean existsByAnioLectivoId(Long anioLectivoId);
}
