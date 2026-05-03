package com.pulpapp.ms_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TenantConfigResponseDTO {

    private Long id;
    private Long tenantId;
    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String bannerUrl;
}
