package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Jornada;
import com.jackren.sistemaacademico.entity.PlantillaFranjaHoraria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantillaFranjaHorariaRepository extends JpaRepository<PlantillaFranjaHoraria, Long> {

    List<PlantillaFranjaHoraria> findByAnioLectivoIdAndJornadaOrderByNumeroBloqueAsc(Long anioLectivoId, Jornada jornada);

    List<PlantillaFranjaHoraria> findByAnioLectivoIdOrderByJornadaAscNumeroBloqueAsc(Long anioLectivoId);

    Optional<PlantillaFranjaHoraria> findByAnioLectivoIdAndJornadaAndNumeroBloque(Long anioLectivoId, Jornada jornada, Integer numeroBloque);
}
