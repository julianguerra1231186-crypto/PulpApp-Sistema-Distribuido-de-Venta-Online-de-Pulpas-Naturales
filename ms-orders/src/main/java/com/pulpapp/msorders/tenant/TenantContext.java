package com.pulpapp.msorders.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Contexto de tenant basado en ThreadLocal para ms-orders.
 * Mismo patrón que ms-users y ms-products (Fase 1 y 2).
 */
@Slf4j
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {}

    public static void setTenantId(Long tenantId) {
        log.debug("Tenant context set: tenantId={}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    public static Long requireTenantId() {
        Long tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException(
                    "No hay tenant en el contexto actual. "
                  + "Asegúrese de que el request incluya un JWT válido con tenantId.");
        }
        return tenantId;
    }

    public static void clear() {
        log.debug("Tenant context cleared");
        CURRENT_TENANT.remove();
    }
}
