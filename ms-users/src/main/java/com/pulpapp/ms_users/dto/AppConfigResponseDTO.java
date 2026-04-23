package com.pulpapp.ms_users.dto;

import lombok.Data;

/**
 * DTO de salida para una entrada de configuración del sistema.
 * No expone el ID interno — solo la clave y el valor.
 */
@Data
public class AppConfigResponseDTO {

    private String configKey;
    private String configValue;
}
