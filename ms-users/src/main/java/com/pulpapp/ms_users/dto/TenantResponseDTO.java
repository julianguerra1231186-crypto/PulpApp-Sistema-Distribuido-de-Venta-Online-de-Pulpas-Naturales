package com.pulpapp.ms_users.dto;

import com.pulpapp.ms_users.entity.TenantStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantResponseDTO {

    private Long id;
    private String name;
    private TenantStatus status;
    private LocalDateTime createdAt;
}
