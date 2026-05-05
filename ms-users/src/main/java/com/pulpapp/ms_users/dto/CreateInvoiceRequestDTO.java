package com.pulpapp.ms_users.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateInvoiceRequestDTO {

    private Long clientId;
    private String clientName;

    @Valid
    @NotEmpty(message = "La factura debe tener al menos un producto")
    private List<InvoiceItemDTO> items;

    private BigDecimal discount;

    @NotBlank(message = "El método de pago es obligatorio")
    private String paymentMethod;

    private String dueDate;
    private String observations;
}
