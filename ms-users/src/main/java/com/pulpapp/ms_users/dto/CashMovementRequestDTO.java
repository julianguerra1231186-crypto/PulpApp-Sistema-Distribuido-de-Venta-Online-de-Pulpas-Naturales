package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashMovementRequestDTO {

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    private String movementType;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que 0")
    private BigDecimal amount;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod;

    private String observations;
}
