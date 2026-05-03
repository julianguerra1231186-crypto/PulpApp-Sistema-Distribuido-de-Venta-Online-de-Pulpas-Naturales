package com.pulpapp.ms_users.controller;

import com.pulpapp.ms_users.dto.TenantRequestDTO;
import com.pulpapp.ms_users.dto.TenantResponseDTO;
import com.pulpapp.ms_users.entity.TenantStatus;
import com.pulpapp.ms_users.service.TenantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para gestión de tenants.
 * Solo accesible por ROLE_ADMIN (configurado en SecurityConfig).
 */
@RestController
@RequestMapping("/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final TenantService tenantService;

    @GetMapping
    public List<TenantResponseDTO> getAll() {
        return tenantService.findAll();
    }

    @GetMapping("/{id}")
    public TenantResponseDTO getById(@PathVariable Long id) {
        return tenantService.findById(id);
    }

    @GetMapping("/status/{status}")
    public List<TenantResponseDTO> getByStatus(@PathVariable TenantStatus status) {
        return tenantService.findByStatus(status);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TenantResponseDTO create(@Valid @RequestBody TenantRequestDTO request) {
        return tenantService.create(request);
    }

    @PatchMapping("/{id}/status")
    public TenantResponseDTO updateStatus(@PathVariable Long id,
                                          @RequestParam TenantStatus status) {
        return tenantService.updateStatus(id, status);
    }
}
