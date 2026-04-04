package com.pulpapp.msorders.client;

import com.pulpapp.msorders.dto.ProductResponseDTO;
import com.pulpapp.msorders.exception.ExternalServiceException;
import com.pulpapp.msorders.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Cliente HTTP encargado de consultar datos del microservicio ms-products.
 */
@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient productsRestClient;

    /**
     * Consulta un producto por su identificador para obtener el precio real vigente.
     */
    public ProductResponseDTO getProductById(Long productId) {
        try {
            ProductResponseDTO product = productsRestClient.get()
                    .uri("/products/{id}", productId)
                    .retrieve()
                    .body(ProductResponseDTO.class);

            if (product == null) {
                throw new ExternalServiceException("ms-products returned an empty response for product " + productId);
            }

            return product;
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResourceNotFoundException("Product not found with id: " + productId);
            }

            throw new ExternalServiceException("Error while retrieving product " + productId + " from ms-products");
        } catch (RestClientException ex) {
            throw new ExternalServiceException("ms-products is unavailable or did not respond correctly");
        }
    }
}
