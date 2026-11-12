package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.EstadoPeriodo;
import com.jackren.sistemaacademico.entity.PeriodoAcademico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PeriodoAcademicoRepository extends JpaRepository<PeriodoAcademico, Long> {

    List<PeriodoAcademico> findByAnioLectivoIdOrderByNumeroAsc(Long anioLectivoId);

    Optional<PeriodoAcademico> findByAnioLectivoIdAndNumero(Long anioLectivoId, Integer numero);

    boolean existsByAnioLectivoIdAndNumero(Long anioLectivoId, Integer numero);

    List<PeriodoAcademico> findByAnioLectivoIdAndEstado(Long anioLectivoId, EstadoPeriodo estado);

    boolean existsByAnioLectivoId(Long anioLectivoId);
}
