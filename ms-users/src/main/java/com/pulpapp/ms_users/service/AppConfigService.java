package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.AppConfigRequestDTO;
import com.pulpapp.ms_users.dto.AppConfigResponseDTO;
import com.pulpapp.ms_users.entity.AppConfig;
import com.pulpapp.ms_users.repository.AppConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Servicio de lógica de negocio para la configuración dinámica del sistema.
 *
 * Implementa un patrón UPSERT: si la clave ya existe, actualiza el valor;
 * si no existe, crea una nueva entrada. Esto garantiza idempotencia en las
 * llamadas del frontend admin.
 */
@Service
@RequiredArgsConstructor
public class AppConfigService {

    private final AppConfigRepository repository;

    /**
     * Crea o actualiza una entrada de configuración (UPSERT por config_key).
     *
     * @param dto DTO con la clave y el nuevo valor
     * @return DTO de respuesta con la configuración persistida
     */
    public AppConfigResponseDTO upsert(AppConfigRequestDTO dto) {
        // Busca entrada existente por clave; si no existe, crea una nueva
        AppConfig config = repository.findByConfigKey(dto.getConfigKey())
                .orElseGet(AppConfig::new);

        config.setConfigKey(dto.getConfigKey());
        config.setConfigValue(dto.getConfigValue());

        AppConfig saved = repository.save(config);
        return toResponseDto(saved);
    }

    /**
     * Retorna todas las entradas de configuración del sistema.
     *
     * @return lista de configuraciones como DTOs
     */
    public List<AppConfigResponseDTO> findAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Busca el valor de una clave específica.
     * Útil para consultas internas (ej: obtener whatsapp_number).
     *
     * @param key clave de configuración
     * @return valor como String, o null si no existe
     */
    public String getValue(String key) {
        return repository.findByConfigKey(key)
                .map(AppConfig::getConfigValue)
                .orElse(null);
    }

    // ── Mapeo privado ──────────────────────────────────────────────────────

    private AppConfigResponseDTO toResponseDto(AppConfig entity) {
        AppConfigResponseDTO dto = new AppConfigResponseDTO();
        dto.setConfigKey(entity.getConfigKey());
        dto.setConfigValue(entity.getConfigValue());
        return dto;
    }
}
