package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Client — Clientes del negocio (inquilinos/arrendatarios/compradores).
 * Cada cliente pertenece a un tenant (aislamiento multi-tenant).
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, length = 150)
    private String name;

    /** Tipo de documento: CC, CE, NIT, Pasaporte */
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

    /** Límite de crédito asignado al cliente */
    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit;

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
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
