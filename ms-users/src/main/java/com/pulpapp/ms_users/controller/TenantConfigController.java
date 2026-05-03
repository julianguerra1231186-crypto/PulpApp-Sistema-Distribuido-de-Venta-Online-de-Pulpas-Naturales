package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.TenantConfigRequestDTO;
import com.pulpapp.ms_users.dto.TenantConfigResponseDTO;
import com.pulpapp.ms_users.service.TenantConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoints de configuración visual (branding) por tenant.
 */
@RestController
@RequestMapping("/tenant/config")
@RequiredArgsConstructor
public class TenantConfigController {

    private final TenantConfigService tenantConfigService;

    /**
     * GET /tenant/config — Obtiene la configuración del tenant actual.
     */
    @GetMapping
    public TenantConfigResponseDTO getMyConfig() {
        return tenantConfigService.getMyConfig();
    }

    /**
     * PUT /tenant/config — Actualiza la configuración del tenant actual.
     */
    @PutMapping
    public TenantConfigResponseDTO updateMyConfig(@RequestBody TenantConfigRequestDTO request) {
        return tenantConfigService.updateMyConfig(request);
    }
}
