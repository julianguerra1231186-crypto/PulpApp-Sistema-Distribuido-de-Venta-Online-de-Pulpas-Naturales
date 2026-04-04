package com.pulpapp.msorders.dto;

import lombok.Data;

/**
 * DTO de salida para cada item persistido en el pedido.
 */
@Data
public class OrderItemResponseDTO {

    private Long id;
    private Long productId;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
