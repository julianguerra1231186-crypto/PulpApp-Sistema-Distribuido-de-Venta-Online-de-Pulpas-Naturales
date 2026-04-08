package com.pulpapp.msproducts.repository;

import com.pulpapp.msproducts.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Category.
 * Permite operaciones CRUD sobre la tabla category,
 * cuyo esquema es gestionado por Liquibase.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
