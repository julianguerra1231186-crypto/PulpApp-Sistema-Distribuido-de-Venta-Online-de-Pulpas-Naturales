package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TenantRequestDTO {

    @NotBlank(message = "El nombre del tenant es obligatorio")
    @Size(max = 100, message = "El nombre del tenant no puede exceder 100 caracteres")
    private String name;
}
