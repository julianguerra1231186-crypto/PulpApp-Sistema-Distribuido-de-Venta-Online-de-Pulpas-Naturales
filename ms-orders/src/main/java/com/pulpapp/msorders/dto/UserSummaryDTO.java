package com.pulpapp.msorders.dto;

import lombok.Data;

/**
 * DTO mínimo para deserializar la respuesta de ms-users.
 * Solo expone los campos que ms-orders necesita para enriquecer la vista del vendedor.
 * No duplica la lógica de negocio de ms-users.
 */
@Data
public class UserSummaryDTO {

    private Long id;
    private String name;
    private String email;
}
