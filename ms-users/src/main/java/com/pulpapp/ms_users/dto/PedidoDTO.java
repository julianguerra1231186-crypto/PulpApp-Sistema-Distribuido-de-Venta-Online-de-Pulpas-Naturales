package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PedidoDTO {

    private Long id;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El total es obligatorio")
    private Double total;

    @NotNull(message = "El userId es obligatorio")
    private Long userId;
}