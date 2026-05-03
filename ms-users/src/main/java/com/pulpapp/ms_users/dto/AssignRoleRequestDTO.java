package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.TenantRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignRoleRequestDTO {

    @NotNull(message = "El rol es obligatorio")
    private TenantRole role;
}
