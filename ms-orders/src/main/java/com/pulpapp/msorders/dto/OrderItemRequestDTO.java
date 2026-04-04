package com.pulpapp.msorders.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para recibir cada producto solicitado dentro de un pedido.
 */
@Data
public class OrderItemRequestDTO {

    @NotNull(message = "El productId es obligatorio")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser minimo 1")
    private Integer cantidad;
}
