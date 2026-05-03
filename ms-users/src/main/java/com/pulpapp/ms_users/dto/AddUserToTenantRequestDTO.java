package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.TenantRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para crear un usuario dentro de un tenant (invitación).
 * El admin del tenant proporciona los datos del nuevo usuario.
 */
@Data
public class AddUserToTenantRequestDTO {

    @NotBlank(message = "La cédula es obligatoria")
    private String cedula;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener mínimo 6 caracteres")
    private String password;

    @NotBlank(message = "La dirección es obligatoria")
    private String direccion;

    @NotNull(message = "El rol es obligatorio")
    private TenantRole role;
}
