package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ConfiguracionManualConvivenciaVersion;
import com.jackren.sistemaacademico.entity.EstadoVersionConfiguracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionManualConvivenciaVersionRepository extends JpaRepository<ConfiguracionManualConvivenciaVersion, Long> {

    Optional<ConfiguracionManualConvivenciaVersion> findFirstByEstadoOrderByNumeroVersionDesc(EstadoVersionConfiguracion estado);

    Optional<ConfiguracionManualConvivenciaVersion> findByNumeroVersion(Integer numeroVersion);

    List<ConfiguracionManualConvivenciaVersion> findAllByOrderByNumeroVersionDesc();

    Optional<ConfiguracionManualConvivenciaVersion> findTopByOrderByNumeroVersionDesc();
}
