package com.pulpapp.msproducts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String name;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    private Boolean available;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    /**
     * Categoría a la que pertenece el producto.
     * Relación: Product (N) → Category (1).
     * Nullable para no romper productos existentes sin categoría.
     * La columna category_id es gestionada por Liquibase (changeSet 3).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;
}
