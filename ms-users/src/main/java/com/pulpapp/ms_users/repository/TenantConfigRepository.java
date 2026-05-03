package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.TenantConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantConfigRepository extends JpaRepository<TenantConfig, Long> {

    Optional<TenantConfig> findByTenantId(Long tenantId);
}
