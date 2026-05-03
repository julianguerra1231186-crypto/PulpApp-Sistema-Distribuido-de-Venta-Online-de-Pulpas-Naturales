package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.TenantRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTenantRoleDTO {

    private Long id;
    private Long userId;
    private Long tenantId;
    private TenantRole role;
    private LocalDateTime createdAt;

    // Datos enriquecidos del usuario
    private String userName;
    private String userEmail;
}
