package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.Rol;
import com.jackren.sistemaacademico.entity.UsuarioRol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, Long> {

    List<UsuarioRol> findByUsuarioId(Long usuarioId);

    List<UsuarioRol> findByUsuarioIdAndActivoTrue(Long usuarioId);

    @Query("SELECT ur FROM UsuarioRol ur WHERE ur.usuario.id = :usuarioId AND ur.activo = true " +
           "AND ur.fechaInicio <= :fechaActual " +
           "AND (ur.fechaFin IS NULL OR ur.fechaFin >= :fechaActual)")
    List<UsuarioRol> findRolesVigentesEntidadPorUsuario(@Param("usuarioId") Long usuarioId, @Param("fechaActual") LocalDate fechaActual);

    @Query("SELECT DISTINCT ur.rol FROM UsuarioRol ur WHERE ur.usuario.id = :usuarioId AND ur.activo = true " +
           "AND ur.fechaInicio <= :fechaActual " +
           "AND (ur.fechaFin IS NULL OR ur.fechaFin >= :fechaActual)")
    List<Rol> findRolesVigentesPorUsuario(@Param("usuarioId") Long usuarioId, @Param("fechaActual") LocalDate fechaActual);

    Optional<UsuarioRol> findByUsuarioIdAndRolAndActivoTrue(Long usuarioId, Rol rol);

    long countByRolAndActivoTrue(Rol rol);
}
