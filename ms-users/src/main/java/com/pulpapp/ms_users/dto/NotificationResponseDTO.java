package com.pulpapp.ms_users.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO de salida para una notificación.
 * Expone todos los campos necesarios para el frontend.
 */
@Data
public class NotificationResponseDTO {

    private Long id;
    private String title;
    private String message;
    private Boolean active;
    private LocalDateTime createdAt;
}
