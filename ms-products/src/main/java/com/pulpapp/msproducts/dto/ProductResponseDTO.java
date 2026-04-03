package com.pulpapp.msproducts.dto;

import lombok.Data;

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
