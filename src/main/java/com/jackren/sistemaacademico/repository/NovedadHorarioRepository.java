package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.NovedadHorario;
import com.jackren.sistemaacademico.entity.TipoNovedadHorario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NovedadHorarioRepository extends JpaRepository<NovedadHorario, Long> {

    List<NovedadHorario> findByHorarioBloqueId(Long horarioBloqueId);

    List<NovedadHorario> findByTipoNovedad(TipoNovedadHorario tipoNovedad);

    @Query("SELECT n FROM NovedadHorario n WHERE n.horarioBloque.id = :bloqueId AND :fecha BETWEEN n.fechaInicio AND n.fechaFin")
    List<NovedadHorario> findNovedadesActivasEnFechaBloque(
            @Param("bloqueId") Long bloqueId,
            @Param("fecha") LocalDate fecha
    );

    @Query("SELECT n FROM NovedadHorario n WHERE :fecha BETWEEN n.fechaInicio AND n.fechaFin")
    List<NovedadHorario> findNovedadesActivasEnFecha(@Param("fecha") LocalDate fecha);
}
