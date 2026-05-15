package com.pulpapp.apigateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * =========================================================
 * CorsWebFilter — Solución definitiva de CORS para Cloud Run
 * =========================================================
 *
 * PROBLEMA:
 *   Spring Cloud Gateway 3.x con globalcors en YAML no responde
 *   correctamente a los preflights OPTIONS cuando el frontend
 *   está en un dominio diferente (Cloud Run).
 *   El preflight se reenvía al backend que responde 401.
 *
 * SOLUCIÓN:
 *   Este WebFilter intercepta TODAS las peticiones OPTIONS
 *   y responde inmediatamente con los headers CORS correctos
 *   SIN reenviar al backend. Para peticiones normales (GET, POST, etc.)
 *   agrega los headers CORS a la respuesta.
 *
 * COMPATIBILIDAD:
 *   - Cloud Run: funciona con cualquier origen
 *   - Docker Compose: funciona igual
 *   - Local: funciona igual
 *
 * =========================================================
 */
@Configuration
public class CorsWebFilter {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter corsFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            var request = exchange.getRequest();
            var response = exchange.getResponse();
            var headers = response.getHeaders();

            // Agregar headers CORS a TODAS las respuestas
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE, PATCH, OPTIONS");
            headers.set(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
            headers.set(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "3600");

            // Si es OPTIONS (preflight), responder inmediatamente sin reenviar al backend
            if (request.getMethod() == HttpMethod.OPTIONS) {
                response.setStatusCode(HttpStatus.OK);
                return response.setComplete();
            }

            return chain.filter(exchange);
        };
    }
}
