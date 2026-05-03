package com.pulpapp.ms_users.repository;

import com.pulpapp.ms_users.entity.UserTenantRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserTenantRoleRepository extends JpaRepository<UserTenantRole, Long> {

    Optional<UserTenantRole> findByUserIdAndTenantId(Long userId, Long tenantId);

    List<UserTenantRole> findByTenantId(Long tenantId);

    List<UserTenantRole> findByUserId(Long userId);

    boolean existsByUserIdAndTenantId(Long userId, Long tenantId);

    void deleteByUserIdAndTenantId(Long userId, Long tenantId);
}
