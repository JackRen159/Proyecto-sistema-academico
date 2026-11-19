package com.jackren.sistemaacademico.repository;

import com.jackren.sistemaacademico.entity.TokenRecuperacionPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRecuperacionPasswordRepository extends JpaRepository<TokenRecuperacionPassword, Long> {

    Optional<TokenRecuperacionPassword> findByTokenHash(String tokenHash);

    List<TokenRecuperacionPassword> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}
