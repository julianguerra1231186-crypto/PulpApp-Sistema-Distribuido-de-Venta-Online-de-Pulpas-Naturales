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
public class InventoryItemResponseDTO {

    private Long id;
    private Long tenantId;
    private String code;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal costPrice;
    private BigDecimal salePrice;
    private BigDecimal unitProfit;
    private String supplier;
    private String ownerName;
    private String propertyAddress;
    private BigDecimal rentValue;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
