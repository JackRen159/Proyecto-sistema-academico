package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByAnioLectivoIdAndActivoTrue(Long anioLectivoId);

    List<Grupo> findByAnioLectivoIdAndGradoIdAndActivoTrue(Long anioLectivoId, Long gradoId);

    Optional<Grupo> findByAnioLectivoIdAndGradoIdAndNombre(Long anioLectivoId, Long gradoId, String nombre);

    boolean existsByAnioLectivoIdAndGradoIdAndNombre(Long anioLectivoId, Long gradoId, String nombre);

    boolean existsByGradoId(Long gradoId);

    boolean existsByAnioLectivoId(Long anioLectivoId);
}
