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

import java.util.Random;
import com.pulpapp.msorders.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Contiene la logica de negocio del microservicio de pedidos.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final Random random = new Random();

    /**
     * Crea un pedido consultando el precio real de cada producto en ms-products.
     * Asigna automáticamente un uniqueAmount con centavos aleatorios para identificar
     * la transferencia bancaria del cliente.
     */
    @Transactional
    public OrderResponseDTO createOrder(CreateOrderRequestDTO request) {
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotal(0.0);

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

            order.addItem(item);
            total += subtotal;
        }

        order.setTotal(total);

        // Guardar primero para obtener el ID, luego asignar uniqueAmount
        Order savedOrder = orderRepository.save(order);
        assignUniqueAmount(savedOrder);
        savedOrder = orderRepository.save(savedOrder);

        return toResponseDto(savedOrder);
    }

    /**
     * Genera un monto único con centavos aleatorios (0.01–0.99) para identificar
     * la transferencia del cliente. Evita duplicados con pedidos activos.
     */
    private void assignUniqueAmount(Order order) {
        java.util.List<Double> activeAmounts = orderRepository.findAll().stream()
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

    /**
     * Consulta todos los pedidos registrados.
     */
    public List<OrderResponseDTO> findAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    /**
     * Consulta un pedido puntual por su identificador.
     */
    public OrderResponseDTO findById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        return toResponseDto(order);
    }

    private OrderResponseDTO toResponseDto(Order order) {
        OrderResponseDTO dto = new OrderResponseDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUserId());
        dto.setTotal(order.getTotal());
        dto.setFecha(order.getFecha());
        dto.setUniqueAmount(order.getUniqueAmount());
        dto.setPaymentStatus(order.getPaymentStatus());
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
