package com.jackren.sistemaacademico.security;

import com.jackren.sistemaacademico.entity.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Servicio de emision y estructuracion de tokens JWT para el sistema academico.
 * Incorpora identificador unico jti para soportar revocacion explicita tras logout.
 */
@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration-hours:8}")
    private long expirationHours;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    /**
     * Construye y codifica un token JWT para un usuario autenticado.
     *
     * @param usuario Entidad de usuario autenticado.
     * @param now Instante actual.
     * @param expiresAt Instante de expiracion.
     * @return Valor serializado del token JWT.
     */
    public String generarToken(Usuario usuario, Instant now, Instant expiresAt) {
        String jti = UUID.randomUUID().toString();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(jti)
                .issuer("sistema-academico")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(usuario.getUsername())
                .claim("id", usuario.getId())
                .claim("rol", usuario.getRol().name())
                .build();

        JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    public long getExpirationHours() {
        return expirationHours;
    }
}
