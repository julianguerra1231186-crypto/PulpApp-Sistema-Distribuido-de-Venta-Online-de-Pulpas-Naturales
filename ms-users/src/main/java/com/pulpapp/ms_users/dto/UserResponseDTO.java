package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.Role;
import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String cedula;
    private String name;
    private String email;
    private String direccion;
    private String telefono;
    private Role role;
    private Boolean activo;

}