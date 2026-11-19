package com.jackren.sistemaacademico.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;

/**
 * Validador personalizado de tokens JWT para Spring Security OAuth2 Resource Server.
 * Rechaza cualquier token cuyo identificador único (jti) figure en la lista de revocación
 * o cuya emisión sea anterior a la revocación forzosa del usuario (RN-SEG-06).
 */
public class TokenRevocationValidator implements OAuth2TokenValidator<Jwt> {

    private final TokenRevocationService tokenRevocationService;

    public TokenRevocationValidator(TokenRevocationService tokenRevocationService) {
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String jti = token.getId();
        if (jti != null && tokenRevocationService.isRevoked(jti)) {
            OAuth2Error error = new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_TOKEN,
                    "El token ha sido revocado tras el cierre de sesión",
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        }

        Long usuarioId = token.getClaim("id");
        Instant issuedAt = token.getIssuedAt();
        if (usuarioId != null && issuedAt != null && tokenRevocationService.isUserTokenRevoked(usuarioId, issuedAt)) {
            OAuth2Error error = new OAuth2Error(
                    OAuth2ErrorCodes.INVALID_TOKEN,
                    "La sesión ha sido revocada forzosamente en el servidor por cambio de credenciales o medida de seguridad (RN-SEG-06)",
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        }

        return OAuth2TokenValidatorResult.success();
    }
}
