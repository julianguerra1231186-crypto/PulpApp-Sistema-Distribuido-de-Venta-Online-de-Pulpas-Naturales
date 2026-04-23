package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para la configuración dinámica del sistema.
 */
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

    /** Busca una entrada de configuración por su clave única. */
    Optional<AppConfig> findByConfigKey(String configKey);
}
