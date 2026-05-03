package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para que el admin resetee la contraseña de un usuario.
 */
@Data
public class ResetPasswordRequestDTO {

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String newPassword;
}
