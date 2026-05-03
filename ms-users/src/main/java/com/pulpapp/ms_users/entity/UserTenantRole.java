package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Relación usuario-tenant-rol.
 * Permite que un usuario tenga roles distintos en diferentes tenants.
 *
 * Fase 5 RBAC Multi-Tenant:
 *  - Un usuario puede pertenecer a múltiples tenants (futuro).
 *  - Cada asignación tiene un rol específico dentro de ese tenant.
 *  - La tabla user_tenant_roles es la fuente de verdad para autorización.
 */
@Entity
@Table(name = "user_tenant_roles",
       uniqueConstraints = @UniqueConstraint(
               name = "uk_user_tenant",
               columnNames = {"user_id", "tenant_id"}
       ))
@Getter
@Setter
public class UserTenantRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantRole role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
