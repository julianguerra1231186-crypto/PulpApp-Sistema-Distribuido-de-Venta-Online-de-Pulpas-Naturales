package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un comprobante de pago del onboarding SaaS.
 *
 * Flujo:
 *  1. Usuario se registra → status PENDING_PAYMENT
 *  2. Sube comprobante → se crea Payment con status PENDING
 *  3. Admin aprueba → Payment APPROVED, se crea Tenant, usuario ACTIVE
 *  4. Admin rechaza → Payment REJECTED, usuario puede reintentar
 */
@Entity
@Table(name = "payments")
@Getter
@Setter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentMethod method;

    /** Referencia de la transacción (número de Nequi, Daviplata, etc.). */
    @Column(length = 100)
    private String reference;

    /** URL del comprobante de pago (imagen subida). */
    @Column(name = "proof_url", nullable = false)
    private String proofUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** ID del admin que aprobó o rechazó. */
    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    /** Nombre del tenant solicitado por el usuario al registrarse. */
    @Column(name = "tenant_name", length = 100)
    private String tenantName;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = PaymentStatus.PENDING;
        }
    }
}
