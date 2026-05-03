package com.pulpapp.msorders.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa el encabezado de un pedido realizado por un usuario.
 * Incluye campos de pago por transferencia (Fase 4).
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private LocalDateTime fecha;

    // ── Campos de pago por transferencia ──────────────────────────

    /**
     * Monto único con centavos aleatorios (0.01–0.99) para identificar
     * la transferencia del cliente. Nunca se repite en pedidos activos.
     */
    @Column(name = "unique_amount")
    private Double uniqueAmount;

    /**
     * Estado del pago:
     * PENDING_PAYMENT   — pedido creado, cliente aún no ha pagado
     * PENDING_APPROVAL  — cliente marcó "Ya pagué", admin debe validar
     * APPROVED          — admin aprobó el pago
     * REJECTED          — admin rechazó el pago
     */
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus = "PENDING_PAYMENT";

    /** Email del admin que aprobó o rechazó el pago. */
    @Column(name = "approved_by")
    private String approvedBy;

    /** Timestamp cuando el cliente presionó "Ya pagué". */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /** Timestamp cuando el admin aprobó el pago. */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    /**
     * Motivo de rechazo del pago, escrito por el admin.
     * Visible para el cliente en su historial de pedidos.
     */
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Identificador del tenant propietario de este pedido.
     * Fase 3 Multi-Tenant: cada pedido pertenece a un tenant.
     * Nullable para compatibilidad con pedidos existentes (la migración
     * Liquibase asigna el tenant por defecto a los registros históricos).
     */
    @Column(name = "tenant_id")
    private Long tenantId;

    @PrePersist
    public void prePersist() {
        if (fecha == null) {
            fecha = LocalDateTime.now();
        }
        if (paymentStatus == null) {
            paymentStatus = "PENDING_PAYMENT";
        }
    }

    public void addItem(OrderItem item) {
        item.setOrder(this);
        items.add(item);
    }
}
