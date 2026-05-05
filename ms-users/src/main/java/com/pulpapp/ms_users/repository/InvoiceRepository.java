package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    Optional<Invoice> findByIdAndTenantId(Long id, Long tenantId);

    @Query("SELECT COALESCE(MAX(i.id), 0) FROM Invoice i WHERE i.tenantId = :tenantId")
    Long findMaxIdByTenantId(@Param("tenantId") Long tenantId);
}
