package com.pulpapp.ms_users.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JobApplicationResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String position;
    private String message;
    private String cvFile;
    private LocalDateTime createdAt;
}
