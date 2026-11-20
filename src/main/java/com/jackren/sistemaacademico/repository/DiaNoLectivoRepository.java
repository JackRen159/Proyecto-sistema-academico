package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.DiaNoLectivo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaNoLectivoRepository extends JpaRepository<DiaNoLectivo, Long> {

    List<DiaNoLectivo> findByAnioLectivoIdAndActivoTrueOrderByFechaInicioAsc(Long anioLectivoId);

    @Query("SELECT d FROM DiaNoLectivo d WHERE d.anioLectivo.id = :anioLectivoId AND d.activo = true " +
           "AND (:fecha BETWEEN d.fechaInicio AND d.fechaFin)")
    List<DiaNoLectivo> findDiasNoLectivosEnFecha(@Param("anioLectivoId") Long anioLectivoId, @Param("fecha") LocalDate fecha);

    @Query("SELECT COUNT(d) > 0 FROM DiaNoLectivo d WHERE d.anioLectivo.id = :anioLectivoId AND d.activo = true " +
           "AND (:fecha BETWEEN d.fechaInicio AND d.fechaFin)")
    boolean esDiaNoLectivo(@Param("anioLectivoId") Long anioLectivoId, @Param("fecha") LocalDate fecha);
}
