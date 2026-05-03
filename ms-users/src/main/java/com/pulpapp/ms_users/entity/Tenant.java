package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representa un tenant (inquilino) en la arquitectura SaaS Multi-Tenant.
 *
 * Cada tenant agrupa un conjunto de usuarios y, en fases posteriores,
 * sus productos, pedidos y configuraciones estarán aislados por tenant_id.
 *
 * Fase 1: Solo se asocia a users. Products y orders se integrarán en fases siguientes.
 */
@Entity
@Table(name = "tenants")
@Data
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TenantStatus status = TenantStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (status == null) {
            status = TenantStatus.ACTIVE;
        }
    }
}
