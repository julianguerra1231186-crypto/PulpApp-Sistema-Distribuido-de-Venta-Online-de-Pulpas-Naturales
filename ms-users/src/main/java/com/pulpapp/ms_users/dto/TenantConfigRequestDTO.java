package com.pulpapp.ms_users.dto;

import lombok.Data;

@Data
public class TenantConfigRequestDTO {

    private String logoUrl;
    private String primaryColor;
    private String secondaryColor;
    private String bannerUrl;
}
