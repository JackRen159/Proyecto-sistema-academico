package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.InstitucionDatosLegales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitucionDatosLegalesRepository extends JpaRepository<InstitucionDatosLegales, Long> {

    Optional<InstitucionDatosLegales> findFirstByActivoTrue();

    Optional<InstitucionDatosLegales> findByCodigoDane(String codigoDane);
}
