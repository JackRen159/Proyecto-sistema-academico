package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioId(Long usuarioId);

    List<UsuarioRol> findByUsuarioIdAndActivoTrue(Long usuarioId);

    Optional<UsuarioRol> findByUsuarioIdAndRolAndActivoTrue(Long usuarioId, Rol rol);

    long countByRolAndActivoTrue(Rol rol);
}
