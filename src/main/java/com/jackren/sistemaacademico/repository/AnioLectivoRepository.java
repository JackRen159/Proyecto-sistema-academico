package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.AnioLectivo;
import com.jackren.sistemaacademico.entity.EstadoAnioLectivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnioLectivoRepository extends JpaRepository<AnioLectivo, Long> {

    Optional<AnioLectivo> findByAnio(Integer anio);

    Optional<AnioLectivo> findByEstado(EstadoAnioLectivo estado);

    boolean existsByAnio(Integer anio);

    boolean existsByEstado(EstadoAnioLectivo estado);

    List<AnioLectivo> findAllByOrderByAnioDesc();
}
