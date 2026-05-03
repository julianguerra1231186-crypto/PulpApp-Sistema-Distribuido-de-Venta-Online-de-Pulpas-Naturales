package com.pulpapp.ms_users.entity;

/**
 * Estados posibles de un tenant.
 *
 * ACTIVE   — El tenant opera con normalidad.
 * INACTIVE — El tenant está suspendido. Sus usuarios no pueden autenticarse.
 */
public enum TenantStatus {
    ACTIVE,
    INACTIVE
}
