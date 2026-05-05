package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

    List<InventoryItem> findByTenantIdOrderByNameAsc(Long tenantId);

    Optional<InventoryItem> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByCodeAndTenantId(String code, Long tenantId);

    List<InventoryItem> findByTenantIdAndNameContainingIgnoreCaseOrderByNameAsc(Long tenantId, String name);

    long countByTenantIdAndActiveTrue(Long tenantId);
}
