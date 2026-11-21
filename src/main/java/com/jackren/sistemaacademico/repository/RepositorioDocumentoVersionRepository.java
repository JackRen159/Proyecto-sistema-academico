package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.RepositorioDocumentoVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioDocumentoVersionRepository extends JpaRepository<RepositorioDocumentoVersion, Long> {
    List<RepositorioDocumentoVersion> findByDocumentoIdOrderByNumeroVersionDesc(Long documentoId);
    Optional<RepositorioDocumentoVersion> findByDocumentoIdAndNumeroVersion(Long documentoId, Integer numeroVersion);
}
