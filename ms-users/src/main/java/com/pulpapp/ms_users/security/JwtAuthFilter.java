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

        // Si no hay header o no empieza con Bearer, se deja pasar sin autenticar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7); // quita "Bearer "

        try {
            final String email = jwtService.extractUsername(jwt);

            // Solo procesa si hay email y el contexto aún no tiene autenticación
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,                          // credentials null: ya autenticado
                                    userDetails.getAuthorities()   // roles del usuario
                            );

                    // Adjunta detalles del request (IP, session) al token de autenticación
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // Establece el contexto de seguridad para este request
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    // ── Fase 1 Multi-Tenant ──────────────────────────────
                    // Extrae tenantId del JWT y lo inyecta en TenantContext.
                    // Si el token no tiene tenantId (tokens pre-Fase 1), el contexto queda vacío.
                    try {
                        Long tenantId = jwtService.extractTenantId(jwt);
                        if (tenantId != null) {
                            TenantContext.setTenantId(tenantId);
                            log.debug("TenantContext set: tenantId={} for user={}",
                                    tenantId, email);
                        }
                    } catch (Exception tenantEx) {
                        log.debug("No se pudo extraer tenantId del JWT: {}",
                                tenantEx.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            // Token inválido, expirado o malformado: se deja el contexto vacío.
            // Spring Security devolverá 401 si el endpoint lo requiere.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
