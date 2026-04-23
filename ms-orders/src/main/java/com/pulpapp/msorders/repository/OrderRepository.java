package com.pulpapp.msorders.repository;

import com.pulpapp.msorders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repositorio JPA para operaciones CRUD sobre pedidos.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Agrupa pedidos por userId, cuenta el número de pedidos y suma el total gastado.
     * Ordena descendente por número de pedidos (clientes más frecuentes primero).
     *
     * La query usa JPQL sobre la entidad Order — no SQL nativo,
     * para mantener portabilidad entre bases de datos.
     */
    @Query("""
            SELECT o.userId   AS userId,
                   COUNT(o)   AS orderCount,
                   SUM(o.total) AS totalSpent
            FROM Order o
            GROUP BY o.userId
            ORDER BY COUNT(o) DESC
            """)
    List<FrequentClientProjection> findFrequentClients();
}
