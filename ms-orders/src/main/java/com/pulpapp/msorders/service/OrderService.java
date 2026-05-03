package com.pulpapp.msorders.service;

import com.pulpapp.msorders.client.ProductClient;
import com.pulpapp.msorders.dto.CreateOrderRequestDTO;
import com.pulpapp.msorders.dto.OrderItemRequestDTO;
import com.pulpapp.msorders.dto.OrderItemResponseDTO;
import com.pulpapp.msorders.dto.OrderResponseDTO;
import com.pulpapp.msorders.dto.ProductResponseDTO;
import com.pulpapp.msorders.entity.Order;
import com.pulpapp.msorders.entity.OrderItem;
import com.pulpapp.msorders.exception.ResourceNotFoundException;
import com.pulpapp.msorders.repository.OrderRepository;
import com.pulpapp.msorders.tenant.TenantContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Lógica de negocio del microservicio de pedidos con aislamiento Multi-Tenant (Fase 3).
 *
 * Estrategia de resolución de tenant:
 *  - Si hay tenantId en TenantContext (JWT presente) → filtra por ese tenant.
 *  - Si no hay tenantId (request público sin JWT) → usa el tenant por defecto.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final Random random = new Random();

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    // ---------------------------------------------------------------
    // Creación
    // ---------------------------------------------------------------

    /**
     * Crea un pedido consultando el precio real de cada producto en ms-products.
     * Asigna automáticamente tenantId y uniqueAmount.
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        Long tenantId = resolveTenantId();
        log.info("createOrder: userId={}, tenantId={}, items={}", request.getUserId(), tenantId, request.getItems().size());

        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotal(0.0);
        order.setTenantId(tenantId);

        double total = 0.0;

        for (OrderItemRequestDTO itemRequest : request.getItems()) {
            ProductResponseDTO product = productClient.getProductById(itemRequest.getProductId());

            if (Boolean.FALSE.equals(product.getAvailable())) {
                throw new IllegalStateException("Product with id " + itemRequest.getProductId() + " is not available");
            }

            if (product.getStock() != null && product.getStock() < itemRequest.getCantidad()) {
                throw new IllegalStateException("Insufficient stock for product with id " + itemRequest.getProductId());
            }

            double unitPrice = product.getPrice();
            double subtotal = unitPrice * itemRequest.getCantidad();

            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setCantidad(itemRequest.getCantidad());
            item.setPrecioUnitario(unitPrice);
            item.setSubtotal(subtotal);
            item.setTenantId(tenantId);

            order.addItem(item);
            total += subtotal;
        }

        order.setTotal(total);

        Order savedOrder = orderRepository.save(order);
        assignUniqueAmount(savedOrder, tenantId);
        savedOrder = orderRepository.save(savedOrder);

        return toResponseDto(savedOrder);
    }

    /**
     * Genera un monto único con centavos aleatorios para identificar la transferencia.
     * Solo compara contra pedidos activos del mismo tenant.
     */
    private void assignUniqueAmount(Order order, Long tenantId) {
        List<Double> activeAmounts = orderRepository.findAllByTenantId(tenantId).stream()
                .filter(o -> !o.getId().equals(order.getId()))
                .filter(o -> "PENDING_PAYMENT".equals(o.getPaymentStatus())
                          || "PENDING_APPROVAL".equals(o.getPaymentStatus()))
                .map(Order::getUniqueAmount)
                .filter(a -> a != null)
                .toList();

        double candidate;
        int attempts = 0;
        do {
            double cents = (random.nextInt(99) + 1) / 100.0;
            candidate = Math.round((order.getTotal() + cents) * 100.0) / 100.0;
            attempts++;
        } while (activeAmounts.contains(candidate) && attempts < 50);

        order.setUniqueAmount(candidate);
    }

    // ---------------------------------------------------------------
    // Consultas
    // ---------------------------------------------------------------

    public List<OrderResponseDTO> findAll() {
        Long tenantId = resolveTenantId();
        log.debug("findAll: tenantId={}", tenantId);

        return orderRepository.findAllByTenantId(tenantId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public OrderResponseDTO findById(Long id) {
        Long tenantId = resolveTenantId();
        log.debug("findById: id={}, tenantId={}", id, tenantId);

        Order order = orderRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return toResponseDto(order);
    }

    // ---------------------------------------------------------------
    // Resolución de tenant
    // ---------------------------------------------------------------

    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantId;
        }
        log.debug("No hay tenantId en contexto, usando default: {}", defaultTenantId);
        return defaultTenantId;
    }

    // ---------------------------------------------------------------
    // Mapeo
    // ---------------------------------------------------------------

    private OrderResponseDTO toResponseDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotal(order.getTotal());
        dto.setFecha(order.getFecha());
        dto.setUniqueAmount(order.getUniqueAmount());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setApprovedBy(order.getApprovedBy());
        dto.setPaidAt(order.getPaidAt());
        dto.setApprovedAt(order.getApprovedAt());
        dto.setTenantId(order.getTenantId());
        dto.setItems(order.getItems().stream().map(this::toItemResponseDto).toList());
        return dto;
    }

    private OrderItemResponseDTO toItemResponseDto(OrderItem item) {
        OrderItemResponseDTO dto = new OrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
