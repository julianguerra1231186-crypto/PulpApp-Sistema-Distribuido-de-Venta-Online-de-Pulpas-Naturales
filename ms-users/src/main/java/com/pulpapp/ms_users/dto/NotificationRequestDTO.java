package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para crear una notificación.
 * Usado en POST /admin/notifications.
 */
@Data
public class NotificationRequestDTO {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar 150 caracteres")
    private String title;

    @NotBlank(message = "El mensaje es obligatorio")
    private String message;
}
