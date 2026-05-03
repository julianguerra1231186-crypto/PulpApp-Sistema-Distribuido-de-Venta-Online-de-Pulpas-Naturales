package com.pulpapp.msproducts.tenant;

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
 *
 * ms-products NO tiene Spring Security. Este filtro solo se encarga de:
 *  1. Leer el header Authorization (Bearer token)
 *  2. Decodificar el claim "tenantId" del JWT
 *  3. Inyectarlo en TenantContext para el request actual
 *  4. Limpiar el contexto al finalizar (evita memory leaks)
 *
 * Para requests públicos (GET /products sin JWT), el contexto queda vacío.
 * El ProductService decide cómo manejar ese caso (devolver todos los productos
 * del tenant por defecto o todos los productos visibles).
 *
 * NOTA: Usa la misma clave secreta que ms-users para verificar la firma.
 * En producción, esta clave debería venir de un vault o config server compartido.
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
                    // JWT inválido o sin tenantId: el contexto queda vacío.
                    // Los endpoints públicos (GET /products) funcionan sin tenant.
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
