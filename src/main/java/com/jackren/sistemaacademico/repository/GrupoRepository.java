package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Grupo;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT g FROM Grupo g WHERE g.id = :id")
    Optional<Grupo> findByIdWithLock(@Param("id") Long id);

    List<Grupo> findByAnioLectivoIdAndActivoTrue(Long anioLectivoId);

    List<Grupo> findByAnioLectivoIdAndGradoIdAndActivoTrue(Long anioLectivoId, Long gradoId);

    Optional<Grupo> findByAnioLectivoIdAndGradoIdAndNombre(Long anioLectivoId, Long gradoId, String nombre);

    boolean existsByAnioLectivoIdAndGradoIdAndNombre(Long anioLectivoId, Long gradoId, String nombre);

    boolean existsByGradoId(Long gradoId);

    boolean existsByAnioLectivoId(Long anioLectivoId);
}
