package com.pulpapp.msorders.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de salida con el estado de pago de un pedido.
 * Usado en GET /orders/{id}/payment y en el dashboard admin.
 */
@Data
public class PaymentStatusDTO {

    private Long orderId;
    private Double total;
    private Double uniqueAmount;
    private String paymentStatus;
    private String approvedBy;
    private LocalDateTime paidAt;
    private LocalDateTime approvedAt;
    private String rejectionReason;
}
