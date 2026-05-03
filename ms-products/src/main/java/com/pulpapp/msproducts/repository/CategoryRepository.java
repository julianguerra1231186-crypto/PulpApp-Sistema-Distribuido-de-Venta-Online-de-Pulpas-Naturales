package com.pulpapp.msproducts.repository;

import com.pulpapp.msproducts.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad Category.
 * Permite operaciones CRUD sobre la tabla category,
 * cuyo esquema es gestionado por Liquibase.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // ── Fase 2 Multi-Tenant ──────────────────────────────────────

    /** Lista todas las categorías de un tenant específico. */
    List<Category> findAllByTenantId(Long tenantId);

    /** Busca una categoría por ID dentro de un tenant. */
    Optional<Category> findByIdAndTenantId(Long id, Long tenantId);
}
