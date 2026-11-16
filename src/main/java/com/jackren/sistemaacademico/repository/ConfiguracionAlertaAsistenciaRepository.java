package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConfiguracionAlertaAsistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionAlertaAsistenciaRepository extends JpaRepository<ConfiguracionAlertaAsistencia, Long> {

    Optional<ConfiguracionAlertaAsistencia> findByAnioLectivoId(Long anioLectivoId);

    boolean existsByAnioLectivoId(Long anioLectivoId);
}
