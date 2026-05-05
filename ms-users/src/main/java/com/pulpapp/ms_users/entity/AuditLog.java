package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de auditoría — log de acciones del sistema.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "user_name", length = 100)
    private String userName;

    /** Acción: CREAR_CLIENTE, EDITAR_PRODUCTO, CREAR_FACTURA, etc. */
    @Column(nullable = false, length = 50)
    private String action;

    /** Módulo: CLIENTES, INVENTARIO, FACTURACION, CAJA, etc. */
    @Column(nullable = false, length = 30)
    private String module;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
