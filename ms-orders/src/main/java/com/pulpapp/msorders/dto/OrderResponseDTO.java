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
}
