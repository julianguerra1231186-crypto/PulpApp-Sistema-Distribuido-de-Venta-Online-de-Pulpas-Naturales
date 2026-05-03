package com.pulpapp.msproducts.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Contexto de tenant basado en ThreadLocal para ms-products.
 *
 * Almacena el tenantId del request actual para que el servicio
 * y los repositorios puedan filtrar datos por tenant sin recibirlo
 * como parámetro en cada método.
 *
 * Ciclo de vida:
 *  1. TenantJwtFilter extrae tenantId del JWT y llama a setTenantId().
 *  2. ProductService consulta getTenantId() para filtrar/asignar datos.
 *  3. TenantJwtFilter llama a clear() en el bloque finally.
 */
@Slf4j
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Utility class
    }

    public static void setTenantId(Long tenantId) {
        log.debug("Tenant context set: tenantId={}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Retorna el tenantId o lanza excepción si no está presente.
     * Usar en operaciones de escritura que REQUIEREN contexto de tenant.
     */
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
