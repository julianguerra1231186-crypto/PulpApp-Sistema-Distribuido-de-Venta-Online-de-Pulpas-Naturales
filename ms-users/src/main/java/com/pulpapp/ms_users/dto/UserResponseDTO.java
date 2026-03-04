package com.pulpapp.ms_users.dto;

import lombok.Data;

@Data
public class UserResponseDTO {

    private Long id;
    private String cedula;
    private String name;
    private String email;
    private String direccion;

}