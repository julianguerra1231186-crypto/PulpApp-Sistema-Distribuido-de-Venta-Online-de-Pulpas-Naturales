package com.pulpapp.msorders.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida del pedido completo.
 */
@Data
public class OrderResponseDTO {

    private Long id;
    private Long userId;
    private Double total;
    private LocalDateTime fecha;
    private List<OrderItemResponseDTO> items;

    // Campos de pago por transferencia
    private Double uniqueAmount;
    private String paymentStatus;

    // Auditoría de aprobación
    private String approvedBy;
    private LocalDateTime paidAt;
    private LocalDateTime approvedAt;
    private String rejectionReason;

    // Multi-Tenant
    private Long tenantId;
}
