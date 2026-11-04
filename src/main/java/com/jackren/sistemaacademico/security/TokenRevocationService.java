package com.jackren.sistemaacademico.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio en memoria para la gestion de revocacion de tokens JWT (Blacklist local).
 * Permite invalidar tokens tras la invocacion de logout mediante el identificador unico (jti).
 */
@Service
public class TokenRevocationService {

    private final ConcurrentHashMap<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    /**
     * Registra un token como revocado hasta su fecha de expiracion.
     *
     * @param jti Identificador unico del token JWT.
     * @param expiresAt Fecha y hora de expiracion del token.
     */
    public void revokeToken(String jti, Instant expiresAt) {
        if (jti != null && !jti.isBlank()) {
            Instant expiration = expiresAt != null ? expiresAt : Instant.now().plusSeconds(8 * 3600);
            revokedTokens.put(jti, expiration);
            cleanupExpired();
        }
    }

    /**
     * Verifica si un token ha sido revocado.
     *
     * @param jti Identificador unico del token JWT.
     * @return true si el token se encuentra en la lista de revocados y no ha expirado aun; false en caso contrario.
     */
    public boolean isRevoked(String jti) {
        if (jti == null || jti.isBlank()) {
            return false;
        }
        Instant expiresAt = revokedTokens.get(jti);
        if (expiresAt == null) {
            return false;
        }
        if (Instant.now().isAfter(expiresAt)) {
            revokedTokens.remove(jti);
            return false;
        }
        return true;
    }

    /**
     * Limpia todos los tokens revocados (util para pruebas y reinicios).
     */
    public void clear() {
        revokedTokens.clear();
    }

    /**
     * Limpieza perezosa de entradas cuya fecha de expiracion ya transcurrio.
     */
    private void cleanupExpired() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}

