package com.pulpapp.msorders.repository;

import com.pulpapp.msorders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para operaciones CRUD sobre pedidos.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
