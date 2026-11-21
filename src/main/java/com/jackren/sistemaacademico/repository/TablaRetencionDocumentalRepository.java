package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.TablaRetencionDocumental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TablaRetencionDocumentalRepository extends JpaRepository<TablaRetencionDocumental, Long> {
    Optional<TablaRetencionDocumental> findByCodigoSerie(String codigoSerie);
    List<TablaRetencionDocumental> findByActivoTrue();
    boolean existsByCodigoSerie(String codigoSerie);
}
