package com.pulpapp.msorders.service;

import com.pulpapp.msorders.client.ProductClient;
import com.pulpapp.msorders.client.UserClient;
import com.pulpapp.msorders.dto.ProductResponseDTO;
import com.pulpapp.msorders.dto.SellerOrderDTO;
import com.pulpapp.msorders.dto.SellerOrderItemDTO;
import com.pulpapp.msorders.dto.UserSummaryDTO;
import com.pulpapp.msorders.entity.Order;
import com.pulpapp.msorders.entity.OrderItem;
import com.pulpapp.msorders.repository.OrderRepository;
import com.pulpapp.msorders.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de pedidos enriquecidos para vendedores con aislamiento Multi-Tenant (Fase 3).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellerOrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    /**
     * Retorna todos los pedidos del tenant actual enriquecidos con datos de cliente y productos.
     */
    public List<SellerOrderDTO> findAllForSeller() {
        Long tenantId = resolveTenantId();
        log.debug("findAllForSeller: tenantId={}", tenantId);

        List<Order> orders = orderRepository.findAllByTenantId(tenantId);

        return orders.stream()
                .map(this::toSellerOrderDTO)
                .toList();
    }

    // ── Mapeo privado ──────────────────────────────────────────────────────

    /**
     * Convierte una entidad Order a SellerOrderDTO enriquecido.
     * Resuelve nombre y email del cliente desde ms-users.
     */
    private SellerOrderDTO toSellerOrderDTO(Order order) {
        SellerOrderDTO dto = new SellerOrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotal(order.getTotal());
        dto.setFecha(order.getFecha());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setUniqueAmount(order.getUniqueAmount());
        dto.setApprovedBy(order.getApprovedBy());
        dto.setApprovedAt(order.getApprovedAt());

        // Enriquecer con datos del cliente desde ms-users
        UserSummaryDTO user = userClient.getUserById(order.getUserId());
        dto.setClientName(user.getName());
        dto.setClientEmail(user.getEmail());

        // Mapear ítems con nombre de producto resuelto desde ms-products
        List<SellerOrderItemDTO> items = order.getItems().stream()
                .map(this::toSellerOrderItemDTO)
                .toList();
        dto.setItems(items);

        return dto;
    }

    /**
     * Convierte un OrderItem a SellerOrderItemDTO.
     * Intenta resolver el nombre del producto desde ms-products.
     * Si no está disponible, usa "Producto #id" como fallback.
     */
    private SellerOrderItemDTO toSellerOrderItemDTO(OrderItem item) {
        SellerOrderItemDTO dto = new SellerOrderItemDTO();
        dto.setProductId(item.getProductId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());

        // Resolver nombre del producto con degradación elegante
        String productName = resolveProductName(item.getProductId());
        dto.setProductName(productName);

        return dto;
    }

    /**
     * Consulta el nombre de un producto en ms-products.
     * Retorna un fallback descriptivo si el servicio no responde.
     */
    private String resolveProductName(Long productId) {
        try {
            ProductResponseDTO product = productClient.getProductById(productId);
            return product.getName() != null ? product.getName() : "Producto #" + productId;
        } catch (Exception ex) {
            log.warn("No se pudo resolver el nombre del producto {}: {}", productId, ex.getMessage());
            return "Producto #" + productId;
        }
    }

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) return tenantId;
        log.debug("No hay tenantId en contexto, usando default: {}", defaultTenantId);
        return defaultTenantId;
    }
}
