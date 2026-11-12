package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {

    List<Asignatura> findByOrderByNombreAsc();

    List<Asignatura> findByActivoTrueOrderByNombreAsc();

    List<Asignatura> findByAreaIdAndActivoTrueOrderByNombreAsc(Long areaId);

    Optional<Asignatura> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByAreaId(Long areaId);
}
