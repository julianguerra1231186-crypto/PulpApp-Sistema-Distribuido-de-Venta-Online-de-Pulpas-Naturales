package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad InventoryItem — Productos/items del inventario del negocio.
 * Cada item pertenece a un tenant (aislamiento multi-tenant).
 */
@Entity
@Table(name = "inventory_items")
@Getter
@Setter
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /** Código único del producto dentro del tenant */
    @Column(nullable = false, length = 30)
    private String code;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer stock;

    /** Precio de compra/costo */
    @Column(name = "cost_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal costPrice;

    /** Precio de venta */
    @Column(name = "sale_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal salePrice;

    /** Utilidad unitaria (calculada: salePrice - costPrice) */
    @Column(name = "unit_profit", precision = 12, scale = 2)
    private BigDecimal unitProfit;

    /** Nombre del proveedor principal */
    @Column(length = 150)
    private String supplier;

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
        calculateProfit();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        calculateProfit();
        updatedAt = LocalDateTime.now();
    }

    private void calculateProfit() {
        if (salePrice != null && costPrice != null) {
            unitProfit = salePrice.subtract(costPrice);
        }
    }
}
