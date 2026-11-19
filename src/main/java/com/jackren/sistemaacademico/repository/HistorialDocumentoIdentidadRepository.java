package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.HistorialDocumentoIdentidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialDocumentoIdentidadRepository extends JpaRepository<HistorialDocumentoIdentidad, Long> {

    List<HistorialDocumentoIdentidad> findByEstudianteIdOrderByFechaCambioDesc(Long estudianteId);

    boolean existsByNumeroDocumentoNuevo(String numeroDocumentoNuevo);
}
