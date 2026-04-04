package com.pulpapp.msorders.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * DTO principal para la creacion de un pedido.
 */
@Data
public class CreateOrderRequestDTO {

    @NotNull(message = "El userId es obligatorio")
    private Long userId;

    @Valid
    @NotEmpty(message = "El pedido debe incluir al menos un item")
    private List<OrderItemRequestDTO> items;
}
