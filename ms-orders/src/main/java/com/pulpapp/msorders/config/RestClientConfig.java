package com.pulpapp.msorders.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configura el cliente HTTP utilizado para consultar ms-products.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient productsRestClient(@Value("${ms-products.base-url}") String productsBaseUrl) {
        return RestClient.builder()
                .baseUrl(productsBaseUrl)
                .build();
    }
}
