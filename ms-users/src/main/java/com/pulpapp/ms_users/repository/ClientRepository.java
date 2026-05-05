package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByTenantIdOrderByNameAsc(Long tenantId);

    List<Client> findByTenantIdAndActiveOrderByNameAsc(Long tenantId, Boolean active);

    Optional<Client> findByIdAndTenantId(Long id, Long tenantId);

    boolean existsByDocumentAndTenantId(String document, Long tenantId);

    List<Client> findByTenantIdAndNameContainingIgnoreCaseOrderByNameAsc(Long tenantId, String name);
}
