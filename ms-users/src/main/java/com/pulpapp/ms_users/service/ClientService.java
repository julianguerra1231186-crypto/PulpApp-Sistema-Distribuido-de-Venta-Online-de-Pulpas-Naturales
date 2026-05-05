package com.pulpapp.ms_users.service;

import com.pulpapp.ms_users.dto.ClientRequestDTO;
import com.pulpapp.ms_users.dto.ClientResponseDTO;
import com.pulpapp.ms_users.entity.Client;
import com.pulpapp.ms_users.exception.ResourceNotFoundException;
import com.pulpapp.ms_users.repository.ClientRepository;
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
public class ClientService {

    private final ClientRepository clientRepository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    // ── Consultas ──

    public List<ClientResponseDTO> findAll() {
        Long tenantId = resolveTenantId();
        return clientRepository.findByTenantIdOrderByNameAsc(tenantId).stream()
                .map(this::toDTO).toList();
    }

    public List<ClientResponseDTO> search(String query) {
        Long tenantId = resolveTenantId();
        return clientRepository.findByTenantIdAndNameContainingIgnoreCaseOrderByNameAsc(tenantId, query).stream()
                .map(this::toDTO).toList();
    }

    public ClientResponseDTO findById(Long id) {
        Long tenantId = resolveTenantId();
        Client client = clientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
        return toDTO(client);
    }

    // ── Creación ──

    @Transactional
    public ClientResponseDTO create(ClientRequestDTO request) {
        Long tenantId = resolveTenantId();

        if (clientRepository.existsByDocumentAndTenantId(request.getDocument(), tenantId)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese documento en este negocio");
        }

        Client client = new Client();
        client.setTenantId(tenantId);
        applyDTO(request, client);

        client = clientRepository.save(client);
        log.info("Cliente creado: id={}, name={}, tenantId={}", client.getId(), client.getName(), tenantId);
        return toDTO(client);
    }

    // ── Actualización ──

    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO request) {
        Long tenantId = resolveTenantId();
        Client client = clientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        applyDTO(request, client);
        client = clientRepository.save(client);
        log.info("Cliente actualizado: id={}, tenantId={}", id, tenantId);
        return toDTO(client);
    }

    // ── Activar/Inactivar ──

    @Transactional
    public ClientResponseDTO toggleActive(Long id, boolean active) {
        Long tenantId = resolveTenantId();
        Client client = clientRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));

        client.setActive(active);
        client = clientRepository.save(client);
        log.info("Cliente {}: id={}, tenantId={}", active ? "activado" : "inactivado", id, tenantId);
        return toDTO(client);
    }

    // ── Helpers ──

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        return tenantId != null ? tenantId : defaultTenantId;
    }

    private void applyDTO(ClientRequestDTO dto, Client client) {
        client.setName(dto.getName().trim());
        client.setDocumentType(dto.getDocumentType().trim());
        client.setDocument(dto.getDocument().trim());
        client.setPhone(dto.getPhone());
        client.setCity(dto.getCity());
        client.setAddress(dto.getAddress());
        client.setEmail(dto.getEmail());
        client.setCreditLimit(dto.getCreditLimit());
    }

    private ClientResponseDTO toDTO(Client c) {
        return ClientResponseDTO.builder()
                .id(c.getId())
                .tenantId(c.getTenantId())
                .name(c.getName())
                .documentType(c.getDocumentType())
                .document(c.getDocument())
                .phone(c.getPhone())
                .city(c.getCity())
                .address(c.getAddress())
                .email(c.getEmail())
                .creditLimit(c.getCreditLimit())
                .active(c.getActive())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
