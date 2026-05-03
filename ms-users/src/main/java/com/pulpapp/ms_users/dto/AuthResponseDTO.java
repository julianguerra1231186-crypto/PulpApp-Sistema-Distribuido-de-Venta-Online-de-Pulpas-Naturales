package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private Long   id;
    private String email;
    private String name;
    private String cedula;
    private String telefono;
    private String direccion;
    private Role   role;
    private Long   tenantId;
}
