package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequestDTO {

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

    // Nombre del negocio/empresa del cliente
    private String businessName;

    // Tipo de negocio (inmobiliaria, restaurante, etc.)
    private String businessType;

    // Opcional: si no se envía se asigna ROLE_SELLER por defecto
    private Role role;
}
