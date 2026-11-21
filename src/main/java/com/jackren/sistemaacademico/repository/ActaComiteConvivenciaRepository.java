package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.ActaComiteConvivencia;
import com.jackren.sistemaacademico.entity.TipoSesionComiteConvivencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActaComiteConvivenciaRepository extends JpaRepository<ActaComiteConvivencia, Long> {
    Optional<ActaComiteConvivencia> findByNumeroActa(String numeroActa);
    List<ActaComiteConvivencia> findByAnioLectivoId(Long anioLectivoId);
    List<ActaComiteConvivencia> findByTipoSesion(TipoSesionComiteConvivencia tipoSesion);
    boolean existsByNumeroActa(String numeroActa);
}
