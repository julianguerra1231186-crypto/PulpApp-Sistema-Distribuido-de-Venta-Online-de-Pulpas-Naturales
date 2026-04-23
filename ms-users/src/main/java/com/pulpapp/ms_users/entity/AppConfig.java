package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa una entrada de configuración dinámica del sistema.
 *
 * Modelo clave-valor: permite al administrador modificar parámetros del sistema
 * (número de WhatsApp, modo de domicilio, módulos activos, etc.)
 * sin necesidad de redeploy.
 *
 * La clave (config_key) es única — un UPSERT actualiza el valor si ya existe.
 */
@Entity
@Table(name = "app_config")
@Data
public class AppConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Clave única de configuración. Ej: "whatsapp_number", "delivery_mode". */
    @Column(name = "config_key", nullable = false, unique = true, length = 100)
    private String configKey;

    /** Valor de la configuración. Puede ser cualquier texto. */
    @Column(name = "config_value", columnDefinition = "TEXT")
    private String configValue;
}
