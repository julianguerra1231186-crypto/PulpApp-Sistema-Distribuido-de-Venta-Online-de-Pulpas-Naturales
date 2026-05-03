package com.pulpapp.ms_users.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cedula;

    // Se deja nullable a nivel de esquema para permitir la migracion sobre registros
    // historicos ya existentes. Los usuarios nuevos siguen obligados a enviar telefono
    // porque UserRequestDTO lo valida como campo requerido.
    @Column
    private String telefono;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_SELLER;

    @Column(nullable = false)
    private Boolean activo = true;

    /**
     * Estado del ciclo de vida del usuario en el flujo SaaS.
     * Fase 4: PENDING_PAYMENT → PENDING_APPROVAL → ACTIVE.
     * Usuarios existentes se migran a ACTIVE por Liquibase.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private UserStatus status = UserStatus.ACTIVE;

    /**
     * Identificador del tenant al que pertenece este usuario.
     * Fase 1 Multi-Tenant: nullable para compatibilidad con usuarios existentes.
     * La migración Liquibase asigna el tenant por defecto a los registros históricos.
     * Los usuarios nuevos SIEMPRE reciben un tenantId en el registro.
     */
    @Column(name = "tenant_id")
    private Long tenantId;

    /**
     * Relación ManyToOne hacia Tenant.
     * insertable/updatable = false porque tenantId se gestiona directamente.
     * Esto permite leer el tenant sin duplicar la columna en JPA.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", insertable = false, updatable = false)
    private Tenant tenant;
}
