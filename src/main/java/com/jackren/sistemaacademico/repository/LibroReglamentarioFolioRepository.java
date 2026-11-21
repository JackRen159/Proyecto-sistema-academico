package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.LibroReglamentarioFolio;
import com.jackren.sistemaacademico.entity.TipoLibroReglamentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibroReglamentarioFolioRepository extends JpaRepository<LibroReglamentarioFolio, Long> {
    List<LibroReglamentarioFolio> findByAnioLectivoIdAndTipoLibro(Long anioLectivoId, TipoLibroReglamentario tipoLibro);
    Optional<LibroReglamentarioFolio> findByAnioLectivoIdAndTipoLibroAndNumeroTomoAndNumeroFolio(Long anioLectivoId, TipoLibroReglamentario tipoLibro, Integer numeroTomo, Integer numeroFolio);

    @Query("SELECT COALESCE(MAX(l.numeroFolio), 0) FROM LibroReglamentarioFolio l WHERE l.anioLectivo.id = :anioLectivoId AND l.tipoLibro = :tipoLibro AND l.numeroTomo = :numeroTomo")
    Integer findMaxFolioPorAnioYTipo(@Param("anioLectivoId") Long anioLectivoId, @Param("tipoLibro") TipoLibroReglamentario tipoLibro, @Param("numeroTomo") Integer numeroTomo);
}
