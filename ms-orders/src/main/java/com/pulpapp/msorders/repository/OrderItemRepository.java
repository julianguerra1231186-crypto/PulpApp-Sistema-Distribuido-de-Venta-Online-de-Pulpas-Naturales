package com.pulpapp.msorders.repository;

import com.pulpapp.msorders.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para las lineas del pedido.
 */
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
