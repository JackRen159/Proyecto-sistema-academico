package com.jackren.sistemaacademico.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio en memoria para la gestión de revocación de tokens JWT (Blacklist local).
 * Permite invalidar tokens individuales tras logout (jti) o masivamente por usuario (RN-SEG-06).
 */
@Service
public class TokenRevocationService {

    private final ConcurrentHashMap<String, Instant> revokedTokens = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Instant> userRevocations = new ConcurrentHashMap<>();

    public void revokeToken(String jti, Instant expiresAt) {
        if (jti != null && !jti.isBlank()) {
            Instant expiration = expiresAt != null ? expiresAt : Instant.now().plusSeconds(8 * 3600);
            revokedTokens.put(jti, expiration);
            cleanupExpired();
        }
    }

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

    public void revokeUserTokens(Long usuarioId, Instant revokedBefore) {
        if (usuarioId != null) {
            userRevocations.put(usuarioId, revokedBefore != null ? revokedBefore : Instant.now());
        }
    }

    public boolean isUserTokenRevoked(Long usuarioId, Instant tokenIssuedAt) {
        if (usuarioId == null || tokenIssuedAt == null) {
            return false;
        }
        Instant revokedBefore = userRevocations.get(usuarioId);
        if (revokedBefore == null) {
            return false;
        }
        return tokenIssuedAt.isBefore(revokedBefore);
    }

    public void clear() {
        revokedTokens.clear();
        userRevocations.clear();
    }

    private void cleanupExpired() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> now.isAfter(entry.getValue()));
    }
}
