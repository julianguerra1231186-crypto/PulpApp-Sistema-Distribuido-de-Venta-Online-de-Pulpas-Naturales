package com.pulpapp.ms_users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO de entrada para crear o actualizar una entrada de configuración.
 * Usado en PUT /admin/config.
 */
@Data
public class AppConfigRequestDTO {

    @NotBlank(message = "La clave de configuración es obligatoria")
    @Size(max = 100, message = "La clave no puede superar 100 caracteres")
    private String configKey;

    /** El valor puede ser vacío (para limpiar una configuración). */
    private String configValue;
}
