package com.pulpapp.msproducts.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 120, message = "El nombre no debe superar 120 caracteres")
    private String name;

    @NotBlank(message = "La descripcion es obligatoria")
    @Size(max = 255, message = "La descripcion no debe superar 255 caracteres")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que 0")
    private Double price;

    @NotNull(message = "El stock es obligatorio")
    @DecimalMin(value = "0", inclusive = true, message = "El stock no puede ser negativo")
    private Integer stock;

    @NotNull(message = "La disponibilidad es obligatoria")
    private Boolean available;

    @Size(max = 255, message = "La URL de imagen no debe superar 255 caracteres")
    private String imageUrl;
}
