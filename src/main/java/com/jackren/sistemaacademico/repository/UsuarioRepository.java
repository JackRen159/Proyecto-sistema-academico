package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.TipoPersonaUsuario;
import com.jackren.sistemaacademico.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByPersonaTipoAndPersonaId(TipoPersonaUsuario personaTipo, Long personaId);

    boolean existsByUsername(String username);
}

