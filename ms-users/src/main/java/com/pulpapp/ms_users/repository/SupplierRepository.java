package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {

    List<Supplier> findByTenantIdOrderByBusinessNameAsc(Long tenantId);

    Optional<Supplier> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByDocumentAndTenantId(String document, Long tenantId);

    List<Supplier> findByTenantIdAndBusinessNameContainingIgnoreCaseOrderByBusinessNameAsc(Long tenantId, String name);

    long countByTenantIdAndActiveTrue(Long tenantId);
}
