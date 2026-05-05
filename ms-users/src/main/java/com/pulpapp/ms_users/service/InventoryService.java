package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.InventoryItemRequestDTO;
import com.pulpapp.ms_users.dto.InventoryItemResponseDTO;
import com.pulpapp.ms_users.entity.InventoryItem;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.InventoryItemRepository;
import com.pulpapp.ms_users.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public List<InventoryItemResponseDTO> findAll() {
        Long tenantId = resolveTenantId();
        return repository.findByTenantIdOrderByNameAsc(tenantId).stream().map(this::toDTO).toList();
    }

    public List<InventoryItemResponseDTO> search(String query) {
        Long tenantId = resolveTenantId();
        return repository.findByTenantIdAndNameContainingIgnoreCaseOrderByNameAsc(tenantId, query).stream().map(this::toDTO).toList();
    }

    public InventoryItemResponseDTO findById(Long id) {
        Long tenantId = resolveTenantId();
        InventoryItem item = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toDTO(item);
    }

    @Transactional
    public InventoryItemResponseDTO create(InventoryItemRequestDTO request) {
        Long tenantId = resolveTenantId();
        if (repository.existsByCodeAndTenantId(request.getCode(), tenantId)) {
            throw new IllegalArgumentException("Ya existe un producto con ese código");
        }

        InventoryItem item = new InventoryItem();
        item.setTenantId(tenantId);
        applyDTO(request, item);
        item = repository.save(item);
        log.info("Producto creado: id={}, code={}, tenantId={}", item.getId(), item.getCode(), tenantId);
        return toDTO(item);
    }

    @Transactional
    public InventoryItemResponseDTO update(Long id, InventoryItemRequestDTO request) {
        Long tenantId = resolveTenantId();
        InventoryItem item = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        applyDTO(request, item);
        item = repository.save(item);
        log.info("Producto actualizado: id={}, tenantId={}", id, tenantId);
        return toDTO(item);
    }

    @Transactional
    public InventoryItemResponseDTO toggleActive(Long id, boolean active) {
        Long tenantId = resolveTenantId();
        InventoryItem item = repository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        item.setActive(active);
        item = repository.save(item);
        return toDTO(item);
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : defaultTenantId;
    }

    private void applyDTO(InventoryItemRequestDTO dto, InventoryItem item) {
        item.setCode(dto.getCode().trim());
        item.setName(dto.getName().trim());
        item.setDescription(dto.getDescription());
        item.setStock(dto.getStock());
        item.setCostPrice(dto.getCostPrice());
        item.setSalePrice(dto.getSalePrice());
        item.setSupplier(dto.getSupplier());
    }

    private InventoryItemResponseDTO toDTO(InventoryItem i) {
        return InventoryItemResponseDTO.builder()
                .id(i.getId()).tenantId(i.getTenantId()).code(i.getCode()).name(i.getName())
                .description(i.getDescription()).stock(i.getStock()).costPrice(i.getCostPrice())
                .salePrice(i.getSalePrice()).unitProfit(i.getUnitProfit()).supplier(i.getSupplier())
                .active(i.getActive()).createdAt(i.getCreatedAt()).updatedAt(i.getUpdatedAt())
                .build();
    }
}
