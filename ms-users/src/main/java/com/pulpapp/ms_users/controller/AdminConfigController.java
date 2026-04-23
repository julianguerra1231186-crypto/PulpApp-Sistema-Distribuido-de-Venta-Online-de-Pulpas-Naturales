package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.AppConfigRequestDTO;
import com.pulpapp.ms_users.dto.AppConfigResponseDTO;
import com.pulpapp.ms_users.service.AppConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de configuración dinámica del sistema.
 *
 * Seguridad: solo ROLE_ADMIN (configurado en SecurityConfig).
 *
 * Separación de responsabilidades:
 * Este controlador gestiona únicamente la configuración del sistema.
 * No mezcla lógica de usuarios ni notificaciones.
 */
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminConfigController {

    private final AppConfigService appConfigService;

    /**
     * PUT /admin/config
     *
     * Crea o actualiza una entrada de configuración (UPSERT).
     * Si la clave ya existe, actualiza el valor.
     * Si no existe, crea una nueva entrada.
     *
     * Acceso: ROLE_ADMIN
     *
     * Ejemplo de body:
     * { "configKey": "whatsapp_number", "configValue": "573124763922" }
     */
    @PutMapping("/config")
    public AppConfigResponseDTO upsertConfig(@Valid @RequestBody AppConfigRequestDTO dto) {
        return appConfigService.upsert(dto);
    }

    /**
     * GET /admin/config
     *
     * Retorna todas las entradas de configuración del sistema.
     * Acceso: ROLE_ADMIN
     */
    @GetMapping("/config")
    public List<AppConfigResponseDTO> getAllConfig() {
        return appConfigService.findAll();
    }
}
