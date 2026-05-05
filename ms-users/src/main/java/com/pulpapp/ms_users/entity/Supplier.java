package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidad Supplier — Proveedores del negocio.
 * Cada proveedor pertenece a un tenant (aislamiento multi-tenant).
 */
@Entity
@Table(name = "suppliers")
@Getter
@Setter
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /** Razón social o nombre del proveedor */
    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    /** Tipo de documento: CC, NIT, CE */
    @Column(name = "document_type", nullable = false, length = 20)
    private String documentType;

    @Column(nullable = false, length = 30)
    private String document;

    @Column(length = 30)
    private String phone;

    @Column(length = 100)
    private String city;

    @Column(length = 255)
    private String address;

    @Column(length = 150)
    private String email;

    /** Cantidad de productos que suministra */
    @Column(name = "products_count")
    private Integer productsCount = 0;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (active == null) active = true;
        if (productsCount == null) productsCount = 0;
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
