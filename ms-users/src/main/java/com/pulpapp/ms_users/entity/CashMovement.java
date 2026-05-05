package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Movimiento de caja: ingreso o gasto.
 */
@Entity
@Table(name = "cash_movements")
@Getter
@Setter
public class CashMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /** INGRESO o GASTO */
    @Column(name = "movement_type", nullable = false, length = 20)
    private String movementType;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    /** EFECTIVO o TRANSFERENCIA */
    @Column(name = "payment_method", nullable = false, length = 30)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
