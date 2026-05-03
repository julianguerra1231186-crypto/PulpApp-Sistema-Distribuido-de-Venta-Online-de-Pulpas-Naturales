package com.pulpapp.ms_users.entity;

/**
 * Roles dentro de un tenant.
 * Sin prefijo ROLE_ — el prefijo se agrega al construir GrantedAuthority.
 * Esto separa el concepto de rol de tenant del enum Role global (legacy).
 */
public enum TenantRole {
    ADMIN,
    SELLER,
    CLIENT
}
