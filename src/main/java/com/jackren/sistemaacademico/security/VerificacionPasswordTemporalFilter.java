package com.jackren.sistemaacademico.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Filtro de seguridad perimetral que exige el cambio obligatorio de contraseña temporal (UC-01 A1, RN-SEG-03).
 * Si el token JWT contiene el claim 'debeCambiarPassword: true', bloquea cualquier acceso a endpoints
 * de negocio protegidos devolviendo HTTP 403 Forbidden, permitiendo únicamente el cambio de contraseña,
 * cierre de sesión, consulta de usuario actual y endpoints públicos.
 */
@Component
public class VerificacionPasswordTemporalFilter extends OncePerRequestFilter {

    private static final Set<String> RUTAS_PERMITIDAS = Set.of(
            "/api/auth/cambiar-password",
            "/api/auth/me",
            "/api/auth/logout",
            "/api/auth/login",
            "/api/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            Object claimValue = jwt.getClaim("debeCambiarPassword");
            boolean debeCambiar = Boolean.TRUE.equals(claimValue) || "true".equalsIgnoreCase(String.valueOf(claimValue));

            if (debeCambiar) {
                String path = request.getRequestURI();

                if (!esRutaPermitida(path)) {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");

                    String json = String.format(
                            "{\"timestamp\":\"%s\",\"status\":403,\"error\":\"Forbidden\",\"codigo\":\"PASSWORD_CAMBIO_REQUERIDO\",\"message\":\"Debe cambiar su contraseña temporal antes de acceder a este recurso (UC-01 A1)\",\"path\":\"%s\"}",
                            LocalDateTime.now(),
                            path
                    );
                    response.getWriter().write(json);
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean esRutaPermitida(String path) {
        if (path == null) {
            return false;
        }
        if (RUTAS_PERMITIDAS.contains(path)) {
            return true;
        }
        return path.startsWith("/api/auth/recuperar-password")
                || path.startsWith("/api/documentos/verificar")
                || path.startsWith("/api/configuracion/")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-ui");
    }
}
