package com.pulpapp.msproducts.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Category — clasifica los productos del catálogo de pulpas.
 * Relación: Category (1) → Product (N)
 * La tabla es creada y versionada por Liquibase (changeSet 1-create-category-table).
 */
@Entity
@Table(name = "category")
@Data
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre único de la categoría (ej: "Tropicales", "Berries"). */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** Descripción opcional de la categoría. */
    @Column(length = 255)
    private String description;

    /**
     * Lista de productos que pertenecen a esta categoría.
     * mappedBy apunta al campo "category" en Product.
     */
    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    /**
     * Identificador del tenant propietario de esta categoría.
     * Fase 2 Multi-Tenant: cada categoría pertenece a un tenant.
     * Nullable para compatibilidad con categorías semilla existentes.
     */
    @Column(name = "tenant_id")
    private Long tenantId;
}
