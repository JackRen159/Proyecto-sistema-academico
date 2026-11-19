package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConfiguracionSieeVersion;
import com.jackren.sistemaacademico.entity.EstadoVersionConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionSieeVersionRepository extends JpaRepository<ConfiguracionSieeVersion, Long> {

    Optional<ConfiguracionSieeVersion> findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion estado);

    Optional<ConfiguracionSieeVersion> findByNumeroVersion(Integer numeroVersion);

    List<ConfiguracionSieeVersion> findAllByOrderByNumeroVersionDesc();

    Optional<ConfiguracionSieeVersion> findTopByOrderByNumeroVersionDesc();
}
