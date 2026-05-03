package com.pulpapp.msorders.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para la vista de pedidos del vendedor (ROLE_SELLER / ROLE_ADMIN).
 *
 * Enriquece el pedido base con datos del cliente (nombre, email) resueltos
 * desde ms-users, y con nombres de productos resueltos desde ms-products.
 *
 * No expone entidades directamente — cumple con el patrón DTO y
 * Separation of Concerns.
 */
@Data
public class SellerOrderDTO {

    /** Identificador único del pedido. */
    private Long id;

    // ── Datos del cliente ──────────────────────────────────────────────────

    /** ID del usuario que realizó el pedido (userId de la entidad Order). */
    private Long userId;

    /**
     * Nombre completo del cliente.
     * Resuelto desde ms-users. Si el servicio no responde, se usa "Cliente #userId".
     */
    private String clientName;

    /**
     * Email del cliente.
     * Resuelto desde ms-users. Si el servicio no responde, se usa cadena vacía.
     */
    private String clientEmail;

    // ── Detalle del pedido ─────────────────────────────────────────────────

    /** Lista de productos con nombre, cantidad y precios. */
    private List<SellerOrderItemDTO> items;

    /** Total del pedido en pesos colombianos. */
    private Double total;

    /** Estado del pago (PENDING_PAYMENT, PENDING_APPROVAL, APPROVED, REJECTED). */
    private String paymentStatus;

    /** Monto único con centavos para identificar la transferencia. */
    private Double uniqueAmount;

    /** Email del admin que aprobó o rechazó el pago (auditoría). */
    private String approvedBy;

    /** Timestamp cuando el admin aprobó o rechazó. */
    private LocalDateTime approvedAt;

    /** Motivo de rechazo escrito por el admin (visible para el cliente). */
    private String rejectionReason;

    // ── Temporalidad ───────────────────────────────────────────────────────

    /**
     * Fecha y hora de creación del pedido.
     * Se serializa como ISO-8601 para facilitar el parseo en el frontend.
     */
    private LocalDateTime fecha;
}
