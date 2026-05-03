package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.TenantRequestDTO;
import com.pulpapp.ms_users.dto.TenantResponseDTO;
import com.pulpapp.ms_users.entity.Tenant;
import com.pulpapp.ms_users.entity.TenantStatus;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de gestión de tenants.
 *
 * Responsabilidades:
 *  - CRUD de tenants
 *  - Resolución del tenant por defecto para el registro público
 *  - Conversión entidad ↔ DTO
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TenantService {

    /**
     * Nombre del tenant por defecto. Se crea automáticamente si no existe.
     * Todos los usuarios registrados públicamente se asignan a este tenant
     * hasta que se implemente selección de tenant en el registro.
     */
    public static final String DEFAULT_TENANT_NAME = "Zentrix";

    private final TenantRepository tenantRepository;

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------

    public List<TenantResponseDTO> findAll() {
        return tenantRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public TenantResponseDTO findById(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant no encontrado con id: " + id));
        return toResponseDTO(tenant);
    }

    public List<TenantResponseDTO> findByStatus(TenantStatus status) {
        return tenantRepository.findByStatus(status).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // ---------------------------------------------------------------
    // Creación
    // ---------------------------------------------------------------

    @Transactional
    public TenantResponseDTO create(TenantRequestDTO request) {
        if (tenantRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Ya existe un tenant con el nombre: " + request.getName());
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setStatus(TenantStatus.ACTIVE);

        tenant = tenantRepository.save(tenant);
        log.info("Tenant creado: id={}, name={}", tenant.getId(), tenant.getName());

        return toResponseDTO(tenant);
    }

    // ---------------------------------------------------------------
    // Actualización de estado
    // ---------------------------------------------------------------

    @Transactional
    public TenantResponseDTO updateStatus(Long id, TenantStatus status) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant no encontrado con id: " + id));

        tenant.setStatus(status);
        tenant = tenantRepository.save(tenant);
        log.info("Tenant actualizado: id={}, status={}", tenant.getId(), tenant.getStatus());

        return toResponseDTO(tenant);
    }

    // ---------------------------------------------------------------
    // Tenant por defecto
    // ---------------------------------------------------------------

    /**
     * Obtiene o crea el tenant por defecto.
     *
     * Estrategia: Si el tenant "Zentrix" no existe, se crea automáticamente.
     * Esto garantiza que el sistema funcione desde el primer arranque sin
     * configuración manual, manteniendo compatibilidad con el flujo existente.
     */
    @Transactional
    public Tenant getOrCreateDefaultTenant() {
        return tenantRepository.findByNameIgnoreCase(DEFAULT_TENANT_NAME)
                .orElseGet(() -> {
                    log.info("Creando tenant por defecto: {}", DEFAULT_TENANT_NAME);
                    Tenant tenant = new Tenant();
                    tenant.setName(DEFAULT_TENANT_NAME);
                    tenant.setStatus(TenantStatus.ACTIVE);
                    return tenantRepository.save(tenant);
                });
    }

    // ---------------------------------------------------------------
    // Mapeo
    // ---------------------------------------------------------------

    private TenantResponseDTO toResponseDTO(Tenant tenant) {
        return TenantResponseDTO.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .status(tenant.getStatus())
                .createdAt(tenant.getCreatedAt())
                .build();
    }
}
