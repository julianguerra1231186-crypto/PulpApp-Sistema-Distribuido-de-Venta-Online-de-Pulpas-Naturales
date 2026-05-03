package com.pulpapp.msorders.tenant;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Base64;

/**
 * Filtro HTTP que extrae el tenantId del JWT propagado por el API Gateway.
 * Mismo patrón que ms-products (Fase 2).
 *
 * ms-orders NO tiene Spring Security. Este filtro solo decodifica el JWT
 * para extraer el claim "tenantId" e inyectarlo en TenantContext.
 *
 * Para requests sin JWT (endpoints públicos como POST /orders),
 * el contexto queda vacío y el servicio usa el tenant por defecto.
 */
@Slf4j
@Component
public class TenantJwtFilter extends OncePerRequestFilter {

    private final SecretKey signingKey;

    public TenantJwtFilter(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                final String jwt = authHeader.substring(7);

                try {
                    Claims claims = Jwts.parser()
                            .verifyWith(signingKey)
                            .build()
                            .parseSignedClaims(jwt)
                            .getPayload();

                    Object tenantValue = claims.get("tenantId");
                    if (tenantValue != null) {
                        Long tenantId;
                        if (tenantValue instanceof Number number) {
                            tenantId = number.longValue();
                        } else {
                            tenantId = Long.parseLong(tenantValue.toString());
                        }
                        TenantContext.setTenantId(tenantId);
                        log.debug("TenantJwtFilter: tenantId={} for URI={}",
                                tenantId, request.getRequestURI());
                    }
                } catch (Exception ex) {
                    log.debug("TenantJwtFilter: no se pudo extraer tenantId: {}",
                            ex.getMessage());
                }
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }
}
