package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Aula;
import com.jackren.sistemaacademico.entity.EstadoAula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {

    Optional<Aula> findBySedeAndCodigo(String sede, String codigo);

    List<Aula> findBySedeAndEstado(String sede, EstadoAula estado);

    List<Aula> findByEstado(EstadoAula estado);

    boolean existsBySedeAndCodigo(String sede, String codigo);
}
