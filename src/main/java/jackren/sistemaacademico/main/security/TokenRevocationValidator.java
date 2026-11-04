package jackren.sistemaacademico.main.security;

import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Validador personalizado de tokens JWT para Spring Security OAuth2 Resource Server.
 * Rechaza cualquier token cuyo identificador unico (jti) figure en la lista de revocacion.
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
                    "El token ha sido revocado tras el cierre de sesion",
                    null
            );
            return OAuth2TokenValidatorResult.failure(error);
        }
        return OAuth2TokenValidatorResult.success();
    }
}

