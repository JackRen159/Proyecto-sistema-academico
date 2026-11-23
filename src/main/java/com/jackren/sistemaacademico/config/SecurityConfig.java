package com.jackren.sistemaacademico.config;

import com.jackren.sistemaacademico.security.RestAccessDeniedHandler;
import com.jackren.sistemaacademico.security.RestAuthenticationEntryPoint;
import com.jackren.sistemaacademico.security.VerificacionPasswordTemporalFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final VerificacionPasswordTemporalFilter passwordTemporalFilter;

    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost,http://localhost:80}")
    private String corsAllowedOrigins;

    public SecurityConfig(RestAuthenticationEntryPoint authenticationEntryPoint,
                          RestAccessDeniedHandler accessDeniedHandler,
                          JwtAuthenticationConverter jwtAuthenticationConverter,
                          VerificacionPasswordTemporalFilter passwordTemporalFilter) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
        this.passwordTemporalFilter = passwordTemporalFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            /*
             * JUSTIFICACIÓN ARQUITECTÓNICA DE DESHABILITACIÓN DE CSRF:
             * La API REST es completamente stateless y autentica mediante tokens JWT (Bearer token).
             * No se utilizan cookies de sesión HTTP ni credenciales autoadjuntables por el navegador.
             * Al residir el token exclusivamente en la memoria del cliente (SPA Angular), las peticiones
             * entre sitios no pueden falsificar credenciales (inmunidad inherente a ataques CSRF).
             */
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos de autenticación y recuperación de contraseña
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/recuperar-password/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/health").permitAll()
                // Verificación pública de autenticidad de documentos oficiales emitidos (QR / hash)
                .requestMatchers(HttpMethod.GET, "/api/documentos/verificar/**").permitAll()
                // Consulta pública de configuración institucional vigente (UC-PROV-14)
                .requestMatchers(HttpMethod.GET, "/api/configuracion/institucion").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/configuracion/politica-datos/vigente").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/configuracion/siee/vigente").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/configuracion/manual-convivencia/vigente").permitAll()
                // Documentación OpenAPI 3.x y Swagger UI interactiva
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                .requestMatchers("/error").permitAll()
                // Toda otra operación bajo /api/** requiere autenticación
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
            )
            .addFilterAfter(passwordTemporalFilter, BearerTokenAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos: configurables por entorno (desarrollo, proxy Nginx, producción)
        List<String> origins = Arrays.stream(corsAllowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
        configuration.setAllowedOrigins(origins);
        // Métodos HTTP explícitamente permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cabeceras permitidas en las solicitudes
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        // Cabeceras expuestas para lectura por el cliente
        configuration.setExposedHeaders(List.of("Location", "Authorization"));
        // No se permiten credenciales automáticas (cookies) en modo stateless con token en memoria
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
