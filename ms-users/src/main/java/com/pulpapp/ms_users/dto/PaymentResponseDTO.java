package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.PaymentMethod;
import com.pulpapp.ms_users.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {

    private Long id;
    private Long userId;
    private BigDecimal amount;
    private PaymentMethod method;
    private String reference;
    private String proofUrl;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String tenantName;

    // Datos del usuario (para vista admin)
    private String userName;
    private String userEmail;
}
