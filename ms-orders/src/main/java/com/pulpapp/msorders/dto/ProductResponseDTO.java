package com.pulpapp.msorders.dto;

import lombok.Data;

/**
 * DTO para deserializar la respuesta de ms-products sin duplicar su logica de negocio.
 */
@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Boolean available;
    private String imageUrl;
}
