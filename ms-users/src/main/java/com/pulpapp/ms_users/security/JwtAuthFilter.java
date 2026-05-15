package com.pulpapp.ms_users.security;

import com.pulpapp.ms_users.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro JWT que se ejecuta una sola vez por request.
 *
 * Flujo:
 *  1. Extrae el header Authorization
 *  2. Valida que empiece con "Bearer "
 *  3. Extrae el email del token
 *  4. Carga el UserDetails desde la base de datos
 *  5. Valida firma y expiración del token
 *  6. Establece el contexto de seguridad de Spring
 *  7. Extrae tenantId del JWT y lo inyecta en TenantContext (Fase 1 Multi-Tenant)
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        log.info("[JWT-FILTER] {} {} | Auth header present: {}", 
                request.getMethod(), request.getRequestURI(), authHeader != null);

        // Si no hay header o no empieza con Bearer, se deja pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.info("[JWT-FILTER] No Bearer token found, skipping auth");
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // quita "Bearer "
        log.info("[JWT-FILTER] Token extracted, length={}", jwt.length());

        try {
            final String email = jwtService.extractUsername(jwt);
            log.info("[JWT-FILTER] Username from token: {}", email);

            // Solo procesa si hay email y el contexto aún no tiene autenticación
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                log.info("[JWT-FILTER] User loaded: {}, authorities: {}", email, userDetails.getAuthorities());

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info("[JWT-FILTER] Token VALID for user: {}", email);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    try {
                        Long tenantId = jwtService.extractTenantId(jwt);
                        if (tenantId != null) {
                            TenantContext.setTenantId(tenantId);
                        }
                    } catch (Exception tenantEx) {
                        log.debug("No se pudo extraer tenantId: {}", tenantEx.getMessage());
                    }
                } else {
                    log.warn("[JWT-FILTER] Token INVALID for user: {}", email);
                }
            }
        } catch (Exception ex) {
            log.error("[JWT-FILTER] Token processing FAILED: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
