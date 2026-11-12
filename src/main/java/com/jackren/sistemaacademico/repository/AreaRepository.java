package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Area;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByOrderByNombreAsc();

    List<Area> findByActivoTrueOrderByNombreAsc();

    Optional<Area> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreIgnoreCase(String nombre);
}
