package com.pulpapp.ms_users.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio responsable de generar, firmar y validar JSON Web Tokens.
 * Usa HMAC-SHA256 con una clave secreta configurable por variable de entorno.
 */
@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    // ---------------------------------------------------------------
    // Generación
    // ---------------------------------------------------------------

    /**
     * Genera un token incluyendo el rol del usuario como claim adicional.
     * Mantiene compatibilidad con el flujo existente (sin tenantId).
     */
    public String generateToken(UserDetails userDetails, String role) {
        return buildToken(Map.of("role", role), userDetails);
    }

    /**
     * Genera un token incluyendo rol y tenantId como claims adicionales.
     * Fase 1 Multi-Tenant: el tenantId viaja en el JWT para que cada
     * microservicio pueda identificar el tenant sin consultar la BD.
     */
    public String generateToken(UserDetails userDetails, String role, Long tenantId) {
        if (tenantId == null) {
            return generateToken(userDetails, role);
        }
        return buildToken(Map.of("role", role, "tenantId", tenantId), userDetails);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
    }

    // ---------------------------------------------------------------
    // Validación
    // ---------------------------------------------------------------

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // ---------------------------------------------------------------
    // Extracción de claims
    // ---------------------------------------------------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extrae el tenantId del token JWT.
     * Retorna null si el claim no existe (tokens generados antes de Fase 1).
     */
    public Long extractTenantId(String token) {
        return extractClaim(token, claims -> {
            Object value = claims.get("tenantId");
            if (value == null) return null;
            if (value instanceof Number number) return number.longValue();
            return Long.parseLong(value.toString());
        });
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
