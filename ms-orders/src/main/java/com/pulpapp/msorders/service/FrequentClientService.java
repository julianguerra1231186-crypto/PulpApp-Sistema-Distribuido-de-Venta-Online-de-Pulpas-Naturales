package com.pulpapp.msorders.service;

import com.pulpapp.msorders.client.UserClient;
import com.pulpapp.msorders.dto.FrequentClientDTO;
import com.pulpapp.msorders.dto.UserSummaryDTO;
import com.pulpapp.msorders.repository.FrequentClientProjection;
import com.pulpapp.msorders.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para la vista de clientes frecuentes.
 *
 * Responsabilidad única: agrupa pedidos por cliente, cuenta frecuencia,
 * suma total gastado y enriquece con nombre/email desde ms-users.
 *
 * Aplica degradación elegante: si ms-users no responde, se usan
 * valores de fallback para no interrumpir la vista del admin.
 *
 * Separación de responsabilidades: independiente de SellerOrderService
 * y OrderService — no modifica contratos existentes.
 */
@Service
@RequiredArgsConstructor
public class FrequentClientService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;

    /**
     * Retorna la lista de clientes frecuentes ordenada por número de pedidos (DESC).
     *
     * Flujo:
     * 1. Query JPQL agrupa pedidos por userId y calcula orderCount y totalSpent.
     * 2. Por cada resultado, consulta ms-users para obtener nombre y email.
     * 3. Mapea a FrequentClientDTO.
     *
     * @return lista de clientes frecuentes enriquecidos
     */
    public List<FrequentClientDTO> findFrequentClients() {
        List<FrequentClientProjection> projections = orderRepository.findFrequentClients();

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
}
