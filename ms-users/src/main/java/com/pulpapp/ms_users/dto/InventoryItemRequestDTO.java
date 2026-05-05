package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryItemRequestDTO {

    @NotBlank(message = "El código es obligatorio")
    private String code;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    private String description;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0", message = "El precio de compra no puede ser negativo")
    private BigDecimal costPrice;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0", message = "El precio de venta no puede ser negativo")
    private BigDecimal salePrice;

    private String supplier;
}
