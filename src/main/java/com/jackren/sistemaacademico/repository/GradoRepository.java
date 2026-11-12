package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Grado;
import com.jackren.sistemaacademico.entity.NivelEducativo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GradoRepository extends JpaRepository<Grado, Long> {

    List<Grado> findByOrderByOrdenJerarquicoAsc();

    List<Grado> findByActivoTrueOrderByOrdenJerarquicoAsc();

    List<Grado> findByNivelAndActivoTrueOrderByOrdenJerarquicoAsc(NivelEducativo nivel);

    Optional<Grado> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    boolean existsByOrdenJerarquico(Integer ordenJerarquico);
}
