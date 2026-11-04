package com.jackren.sistemaacademico.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.jackren.sistemaacademico.security.TokenRevocationService;
import com.jackren.sistemaacademico.security.TokenRevocationValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Configuracion de componentes criptograficos y validacion de tokens JWT.
 *
 * <h3>Compromiso Arquitectonico del MVP — Stateless Tradeoff:</h3>
 * <p>
 * En la fase actual del sistema, la arquitectura de autenticacion opera bajo el modelo
 * completamente stateless (sin estado persistente en servidor). El token JWT reside
 * exclusivamente en la memoria de la aplicacion cliente (SPA Angular) con un tiempo de vida
 * predeterminado de 8 horas (jornada escolar tipica).
 * </p>
 * <ul>
 *   <li><b>Revocacion en Logout:</b> Se gestiona mediante una lista de exclusion local en memoria
 *   ({@link TokenRevocationService}) que asocia el identificador unico del token ({@code jti})
 *   hasta su fecha de expiracion, impidiendo su reutilizacion en el nodo local.</li>
 *   <li><b>Tradeoff de Usuarios Desactivados:</b> Dado que el Resource Server valida los tokens
 *   de manera puramente criptografica sin consultar la base de datos en cada peticion HTTP
 *   (para maximizar el rendimiento y evitar cuellos de botella I/O), la desactivacion de una cuenta
 *   ({@code activo = false}) impide la emision de nuevos tokens pero no revoca instantaneamente
 *   tokens ya emitidos con anterioridad a menos que se hayan registrado explicitamente en logout.</li>
 *   <li><b>Mitigacion Futura (Fase de Produccion/Escalado):</b> Para escenarios multi-instancia
 *   y cumplimiento estricto de revocacion inmediata ante desactivacion de cuentas, se implementara
 *   un esquema de tokens de acceso de corta duracion (TTL de 15 a 30 minutos) respaldado por
 *   Refresh Tokens rotativos y un almacenamiento distribuido en cache (Redis) para la lista de revocacion.</li>
 * </ul>
 */
@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Bean
    public SecretKey secretKey() {
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey, TokenRevocationService tokenRevocationService) {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(secretKey).build();

        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefault();
        OAuth2TokenValidator<Jwt> revocationValidator = new TokenRevocationValidator(tokenRevocationService);
        OAuth2TokenValidator<Jwt> combinedValidator = new DelegatingOAuth2TokenValidator<>(defaultValidator, revocationValidator);

        jwtDecoder.setJwtValidator(combinedValidator);
        return jwtDecoder;
    }

    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey) {
        JWKSource<SecurityContext> jwkSource = new ImmutableSecret<>(secretKey);
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("rol");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
