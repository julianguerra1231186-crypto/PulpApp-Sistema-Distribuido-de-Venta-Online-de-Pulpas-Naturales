package com.pulpapp.msorders.dto;

import lombok.Data;

/**
 * DTO que representa un item de pedido en la vista del vendedor.
 * Expone nombre del producto (resuelto desde ms-products) y cantidad.
 * No expone la entidad directamente — cumple con el patrón DTO.
 */
@Data
public class SellerOrderItemDTO {

    /** Identificador interno del producto en ms-products. */
    private Long productId;

    /**
     * Nombre del producto resuelto desde ms-products.
     * Si el servicio no está disponible, se usa "Producto #id" como fallback.
     */
    private String productName;

    /** Cantidad de unidades compradas. */
    private Integer cantidad;

    /** Precio unitario capturado al momento de la compra (precio histórico). */
    private Double precioUnitario;

    /** Subtotal = precioUnitario × cantidad. */
    private Double subtotal;
}
