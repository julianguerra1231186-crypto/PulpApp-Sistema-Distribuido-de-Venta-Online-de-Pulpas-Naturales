package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.SupplierRequestDTO;
import com.pulpapp.ms_users.dto.SupplierResponseDTO;
import com.pulpapp.ms_users.entity.Supplier;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.SupplierRepository;
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
public class SupplierService {

    private final SupplierRepository repository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    public List<SupplierResponseDTO> findAll() {
        return repository.findByTenantIdOrderByBusinessNameAsc(resolveTenantId()).stream().map(this::toDTO).toList();
    }

    public List<SupplierResponseDTO> search(String query) {
        return repository.findByTenantIdAndBusinessNameContainingIgnoreCaseOrderByBusinessNameAsc(resolveTenantId(), query).stream().map(this::toDTO).toList();
    }

    public SupplierResponseDTO findById(Long id) {
        Supplier s = repository.findByIdAndTenantId(id, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        return toDTO(s);
    }

    @Transactional
    public SupplierResponseDTO create(SupplierRequestDTO request) {
        Long tenantId = resolveTenantId();
        if (repository.existsByDocumentAndTenantId(request.getDocument(), tenantId)) {
            throw new IllegalArgumentException("Ya existe un proveedor con ese documento");
        }
        Supplier s = new Supplier();
        s.setTenantId(tenantId);
        applyDTO(request, s);
        s = repository.save(s);
        log.info("Proveedor creado: id={}, name={}, tenantId={}", s.getId(), s.getBusinessName(), tenantId);
        return toDTO(s);
    }

    @Transactional
    public SupplierResponseDTO update(Long id, SupplierRequestDTO request) {
        Supplier s = repository.findByIdAndTenantId(id, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        applyDTO(request, s);
        s = repository.save(s);
        return toDTO(s);
    }

    @Transactional
    public SupplierResponseDTO toggleActive(Long id, boolean active) {
        Supplier s = repository.findByIdAndTenantId(id, resolveTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id: " + id));
        s.setActive(active);
        s = repository.save(s);
        return toDTO(s);
    }

    private Long resolveTenantId() {
        Long t = TenantContext.getTenantId();
        return t != null ? t : defaultTenantId;
    }

    private void applyDTO(SupplierRequestDTO dto, Supplier s) {
        s.setBusinessName(dto.getBusinessName().trim());
        s.setDocumentType(dto.getDocumentType().trim());
        s.setDocument(dto.getDocument().trim());
        s.setPhone(dto.getPhone());
        s.setCity(dto.getCity());
        s.setAddress(dto.getAddress());
        s.setEmail(dto.getEmail());
    }

    private SupplierResponseDTO toDTO(Supplier s) {
        return SupplierResponseDTO.builder()
                .id(s.getId()).tenantId(s.getTenantId()).businessName(s.getBusinessName())
                .documentType(s.getDocumentType()).document(s.getDocument()).phone(s.getPhone())
                .city(s.getCity()).address(s.getAddress()).email(s.getEmail())
                .productsCount(s.getProductsCount()).active(s.getActive())
                .createdAt(s.getCreatedAt()).updatedAt(s.getUpdatedAt()).build();
    }
}
