package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que 0")
    private BigDecimal amount;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod method;

    /** Referencia de la transacción (opcional). */
    private String reference;

    @NotBlank(message = "La URL del comprobante es obligatoria")
    private String proofUrl;

    /** Nombre deseado para el tenant (se usa al aprobar el pago). */
    private String tenantName;
}
