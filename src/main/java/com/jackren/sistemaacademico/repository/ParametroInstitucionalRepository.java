package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ParametroInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParametroInstitucionalRepository extends JpaRepository<ParametroInstitucional, Long> {

    Optional<ParametroInstitucional> findByClave(String clave);

    List<ParametroInstitucional> findByCategoriaOrderByClaveAsc(String categoria);

    boolean existsByClave(String clave);
}
