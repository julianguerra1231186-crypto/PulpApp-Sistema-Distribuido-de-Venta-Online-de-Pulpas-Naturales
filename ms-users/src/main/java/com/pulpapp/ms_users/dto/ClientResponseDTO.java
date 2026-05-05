package com.pulpapp.ms_users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    private Long id;
    private Long tenantId;
    private String name;
    private String documentType;
    private String document;
    private String phone;
    private String city;
    private String address;
    private String email;
    private BigDecimal creditLimit;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
