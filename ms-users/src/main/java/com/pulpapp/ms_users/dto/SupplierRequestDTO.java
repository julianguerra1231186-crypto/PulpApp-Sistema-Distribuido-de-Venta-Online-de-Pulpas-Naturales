package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SupplierRequestDTO {

    @NotBlank(message = "La razón social es obligatoria")
    private String businessName;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String documentType;

    @NotBlank(message = "El número de documento es obligatorio")
    private String document;

    private String phone;
    private String city;
    private String address;
    private String email;
}
