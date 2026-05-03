package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.TenantConfigRequestDTO;
import com.pulpapp.ms_users.dto.TenantConfigResponseDTO;
import com.pulpapp.ms_users.entity.TenantConfig;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.TenantConfigRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de configuración visual (branding) por tenant.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantConfigService {

    private final TenantConfigRepository tenantConfigRepository;

    public TenantConfigResponseDTO getConfig(Long tenantId) {
        TenantConfig config = tenantConfigRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Configuración no encontrada para tenantId: " + tenantId));
        return toResponseDTO(config);
    }

    public TenantConfigResponseDTO getMyConfig() {
        Long tenantId = TenantContext.requireTenantId();
        return getConfig(tenantId);
    }

    @Transactional
    public TenantConfigResponseDTO updateMyConfig(TenantConfigRequestDTO request) {
        Long tenantId = TenantContext.requireTenantId();

        TenantConfig config = tenantConfigRepository.findByTenantId(tenantId)
                .orElseGet(() -> {
                    TenantConfig newConfig = new TenantConfig();
                    newConfig.setTenantId(tenantId);
                    return newConfig;
                });

        if (request.getLogoUrl() != null) config.setLogoUrl(request.getLogoUrl());
        if (request.getPrimaryColor() != null) config.setPrimaryColor(request.getPrimaryColor());
        if (request.getSecondaryColor() != null) config.setSecondaryColor(request.getSecondaryColor());
        if (request.getBannerUrl() != null) config.setBannerUrl(request.getBannerUrl());

        config = tenantConfigRepository.save(config);
        log.info("TenantConfig actualizada: tenantId={}", tenantId);

        return toResponseDTO(config);
    }

    private TenantConfigResponseDTO toResponseDTO(TenantConfig config) {
        return TenantConfigResponseDTO.builder()
                .id(config.getId())
                .tenantId(config.getTenantId())
                .logoUrl(config.getLogoUrl())
                .primaryColor(config.getPrimaryColor())
                .secondaryColor(config.getSecondaryColor())
                .bannerUrl(config.getBannerUrl())
                .build();
    }
}
