package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.Tenant;
import com.pulpapp.ms_users.entity.TenantStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, Long> {

    Optional<Tenant> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);

    List<Tenant> findByStatus(TenantStatus status);
}
