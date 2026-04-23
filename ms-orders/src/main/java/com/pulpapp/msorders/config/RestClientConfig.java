package com.pulpapp.msorders.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura los clientes HTTP para comunicación con otros microservicios.
 *
 * Cada bean tiene un nombre explícito para evitar ambigüedad cuando Spring
 * inyecta RestClient por tipo en múltiples componentes.
 */
@Configuration
public class RestClientConfig {

    /**
     * Cliente HTTP para consultar el catálogo de productos en ms-products.
     * Usado por ProductClient para obtener precios vigentes al crear pedidos.
     */
    @Bean
    public RestClient productsRestClient(@Value("${ms-products.base-url}") String productsBaseUrl) {
        return RestClient.builder()
                .baseUrl(productsBaseUrl)
                .build();
    }

    /**
     * Cliente HTTP para consultar datos de usuarios en ms-users.
     * Usado por UserClient para enriquecer la vista del vendedor con nombre y email del cliente.
     */
    @Bean
    public RestClient usersRestClient(@Value("${ms-users.base-url}") String usersBaseUrl) {
        return RestClient.builder()
                .baseUrl(usersBaseUrl)
                .build();
    }
}
