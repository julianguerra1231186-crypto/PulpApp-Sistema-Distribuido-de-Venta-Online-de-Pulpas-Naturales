package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobApplicationRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String fullName;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;

    private String position;
    private String message;
}
