package com.pulpapp.msproducts.repository;

import com.pulpapp.msproducts.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    // ── Fase 2 Multi-Tenant ──────────────────────────────────────

    /** Lista todos los productos de un tenant específico. */
    List<Product> findAllByTenantId(Long tenantId);

    /** Busca un producto por ID dentro de un tenant (aislamiento de datos). */
    Optional<Product> findByIdAndTenantId(Long id, Long tenantId);

    /** Verifica nombre único dentro del mismo tenant. */
    boolean existsByNameIgnoreCaseAndTenantId(String name, Long tenantId);

    /** Verifica nombre único dentro del mismo tenant, excluyendo un ID (para updates). */
    boolean existsByNameIgnoreCaseAndTenantIdAndIdNot(String name, Long tenantId, Long id);
}
