package com.pulpapp.ms_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierResponseDTO {

    private Long id;
    private Long tenantId;
    private String businessName;
    private String documentType;
    private String document;
    private String phone;
    private String city;
    private String address;
    private String email;
    private Integer productsCount;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
