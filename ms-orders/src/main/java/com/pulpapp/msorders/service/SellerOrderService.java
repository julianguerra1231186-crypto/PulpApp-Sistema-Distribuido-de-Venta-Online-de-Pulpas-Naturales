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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de lógica de negocio para la vista de pedidos del vendedor.
 *
 * Responsabilidad única: obtener todos los pedidos y enriquecerlos con
 * datos del cliente (ms-users) y nombres de productos (ms-products).
 *
 * Aplica degradación elegante: si algún microservicio externo no responde,
 * se usan valores de fallback para no interrumpir la vista del vendedor.
 *
 * No modifica ni reutiliza OrderService — cumple con Single Responsibility.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SellerOrderService {

    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    /**
     * Retorna todos los pedidos enriquecidos con datos de cliente y productos.
     *
     * Flujo:
     * 1. Carga todos los pedidos desde la base de datos.
     * 2. Por cada pedido, consulta ms-users para obtener nombre y email del cliente.
     * 3. Por cada ítem, consulta ms-products para obtener el nombre del producto.
     * 4. Mapea todo a SellerOrderDTO sin exponer entidades.
     *
     * @return lista de pedidos enriquecidos para la vista del vendedor
     */
    public List<SellerOrderDTO> findAllForSeller() {
        List<Order> orders = orderRepository.findAll();

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
}
