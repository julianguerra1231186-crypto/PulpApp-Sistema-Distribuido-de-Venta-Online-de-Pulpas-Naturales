package com.pulpapp.msorders.repository;

import com.pulpapp.msorders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para operaciones CRUD sobre pedidos.
 */
public interface OrderRepository extends JpaRepository<Order, Long> {

    // ── Fase 3 Multi-Tenant ──────────────────────────────────────

    /** Lista todos los pedidos de un tenant específico. */
    List<Order> findAllByTenantId(Long tenantId);

    /** Busca un pedido por ID dentro de un tenant (aislamiento de datos). */
    Optional<Order> findByIdAndTenantId(Long id, Long tenantId);

    /**
     * Agrupa pedidos por userId dentro de un tenant, cuenta el número de pedidos
     * y suma el total gastado. Ordena descendente por número de pedidos.
     */
    @Query("""
            SELECT o.userId   AS userId,
                   COUNT(o)   AS orderCount,
                   SUM(o.total) AS totalSpent
            FROM Order o
            WHERE o.tenantId = :tenantId
            GROUP BY o.userId
            ORDER BY COUNT(o) DESC
            """)
    List<FrequentClientProjection> findFrequentClientsByTenantId(@Param("tenantId") Long tenantId);
}
