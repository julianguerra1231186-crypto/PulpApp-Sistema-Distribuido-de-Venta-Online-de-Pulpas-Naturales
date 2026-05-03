package com.pulpapp.msproducts.service;

import com.pulpapp.msproducts.dto.ProductRequestDTO;
import com.pulpapp.msproducts.dto.ProductResponseDTO;
import com.pulpapp.msproducts.entity.Product;
import com.pulpapp.msproducts.exception.ResourceNotFoundException;
import com.pulpapp.msproducts.repository.ProductRepository;
import com.pulpapp.msproducts.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Servicio de productos con aislamiento Multi-Tenant (Fase 2).
 *
 * Estrategia de resolución de tenant:
 *  - Si hay tenantId en TenantContext (JWT presente) → filtra por ese tenant.
 *  - Si no hay tenantId (request público sin JWT) → usa el tenant por defecto.
 *
 * Esto mantiene compatibilidad total con el frontend actual, que consulta
 * GET /products sin JWT para mostrar el catálogo público.
 *
 * Operaciones de escritura (create, update, delete) REQUIEREN tenantId.
 * Si no hay tenant en el contexto, se usa el default (para compatibilidad
 * con el flujo actual donde el admin crea productos sin que el frontend
 * envíe tenantId explícitamente).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Value("${tenant.default-id:1}")
    private Long defaultTenantId;

    // ---------------------------------------------------------------
    // Consultas (lectura)
    // ---------------------------------------------------------------

    public List<ProductResponseDTO> findAll() {
        Long tenantId = resolveTenantId();
        log.debug("findAll: tenantId={}", tenantId);

        return productRepository.findAllByTenantId(tenantId)
                .stream()
                .map(this::toResponseDto)
                .toList();
    }

    public ProductResponseDTO findById(Long id) {
        Long tenantId = resolveTenantId();
        log.debug("findById: id={}, tenantId={}", id, tenantId);

        // Busca por ID + tenantId → si no pertenece al tenant, devuelve 404
        // (no 403, para evitar filtrado de información entre tenants)
        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        return toResponseDto(product);
    }

    // ---------------------------------------------------------------
    // Escritura
    // ---------------------------------------------------------------

    public ProductResponseDTO create(ProductRequestDTO dto) {
        Long tenantId = resolveTenantId();
        log.info("create: name={}, tenantId={}", dto.getName(), tenantId);

        validateUniqueName(dto.getName(), null, tenantId);

        Product product = new Product();
        applyDtoToEntity(dto, product);
        product.setTenantId(tenantId);

        return toResponseDto(productRepository.save(product));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Long tenantId = resolveTenantId();
        log.info("update: id={}, tenantId={}", id, tenantId);

        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        validateUniqueName(dto.getName(), id, tenantId);
        applyDtoToEntity(dto, product);

        return toResponseDto(productRepository.save(product));
    }

    public void delete(Long id) {
        Long tenantId = resolveTenantId();
        log.info("delete: id={}, tenantId={}", id, tenantId);

        Product product = productRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with id: " + id));

        productRepository.delete(product);
    }

    // ---------------------------------------------------------------
    // Resolución de tenant
    // ---------------------------------------------------------------

    /**
     * Resuelve el tenantId para la operación actual.
     *
     * Prioridad:
     *  1. TenantContext (extraído del JWT por TenantJwtFilter)
     *  2. Tenant por defecto (configurado en application.properties)
     *
     * El fallback al tenant por defecto garantiza compatibilidad con:
     *  - GET /products público (catálogo sin JWT)
     *  - Flujos legacy del frontend que aún no envían JWT
     */
    private Long resolveTenantId() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            return tenantId;
        }
        log.debug("No hay tenantId en contexto, usando default: {}", defaultTenantId);
        return defaultTenantId;
    }

    // ---------------------------------------------------------------
    // Validación
    // ---------------------------------------------------------------

    /**
     * Valida que el nombre del producto sea único DENTRO del mismo tenant.
     * Dos tenants diferentes pueden tener productos con el mismo nombre.
     */
    private void validateUniqueName(String name, Long excludedId, Long tenantId) {
        boolean exists = excludedId == null
                ? productRepository.existsByNameIgnoreCaseAndTenantId(name, tenantId)
                : productRepository.existsByNameIgnoreCaseAndTenantIdAndIdNot(name, tenantId, excludedId);

        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A product with that name already exists");
        }
    }

    // ---------------------------------------------------------------
    // Mapeo
    // ---------------------------------------------------------------

    private void applyDtoToEntity(ProductRequestDTO dto, Product product) {
        product.setName(dto.getName().trim());
        product.setDescription(dto.getDescription().trim());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setAvailable(dto.getAvailable());
        product.setImageUrl(dto.getImageUrl() == null ? null : dto.getImageUrl().trim());
    }

    private ProductResponseDTO toResponseDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStock(product.getStock());
        dto.setAvailable(product.getAvailable());
        dto.setImageUrl(product.getImageUrl());
        dto.setTenantId(product.getTenantId());
        return dto;
    }
}
