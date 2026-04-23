package com.pulpapp.msorders.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO de entrada para que el admin apruebe o rechace un pago.
 * Usado en PUT /orders/{id}/approve y PUT /orders/{id}/reject.
 */
@Data
public class ApprovePaymentRequestDTO {

    /** Email del admin que realiza la acción (para auditoría). */
    @NotBlank(message = "El email del administrador es obligatorio")
    private String adminEmail;
}
