package com.pulpapp.msorders.service;

import com.pulpapp.msorders.client.UserClient;
import com.pulpapp.msorders.dto.FrequentClientDTO;
import com.pulpapp.msorders.dto.UserSummaryDTO;
import com.pulpapp.msorders.repository.FrequentClientProjection;
import com.pulpapp.msorders.repository.OrderRepository;
import com.pulpapp.msorders.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de clientes frecuentes con aislamiento Multi-Tenant (Fase 3).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FrequentClientService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    /**
     * Retorna la lista de clientes frecuentes del tenant actual.
     */
    public List<FrequentClientDTO> findFrequentClients() {
        Long tenantId = resolveTenantId();
        log.debug("findFrequentClients: tenantId={}", tenantId);

        List<FrequentClientProjection> projections = orderRepository.findFrequentClientsByTenantId(tenantId);

        return projections.stream()
                .map(this::toFrequentClientDTO)
                .toList();
    }

    // ── Mapeo privado ──────────────────────────────────────────────────────

    /**
     * Convierte una proyección de la query a FrequentClientDTO,
     * enriqueciendo con datos del cliente desde ms-users.
     */
    private FrequentClientDTO toFrequentClientDTO(FrequentClientProjection projection) {
        FrequentClientDTO dto = new FrequentClientDTO();
        dto.setUserId(projection.getUserId());
        dto.setOrderCount(projection.getOrderCount());
        dto.setTotalSpent(projection.getTotalSpent());

        // Enriquecer con nombre y email desde ms-users (con fallback)
        UserSummaryDTO user = userClient.getUserById(projection.getUserId());
        dto.setClientName(user.getName());
        dto.setClientEmail(user.getEmail());

        return dto;
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) return tenantId;
        log.debug("No hay tenantId en contexto, usando default: {}", defaultTenantId);
        return defaultTenantId;
    }
}
