package com.pulpapp.ms_users.dto;

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
public class CashMovementResponseDTO {
    private Long id;
    private String movementType;
    private String description;
    private BigDecimal amount;
    private String paymentMethod;
    private String observations;
    private LocalDateTime createdAt;
}
