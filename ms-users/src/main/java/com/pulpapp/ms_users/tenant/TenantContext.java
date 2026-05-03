package com.pulpapp.ms_users.tenant;

import lombok.extern.slf4j.Slf4j;

/**
 * Contexto de tenant basado en ThreadLocal.
 *
 * Almacena el tenantId del request actual para que cualquier capa
 * (servicio, repositorio, etc.) pueda consultarlo sin recibirlo como parámetro.
 *
 * Ciclo de vida:
 *  1. El filtro TenantFilter extrae tenantId del JWT y llama a setTenantId().
 *  2. Los servicios/repositorios consultan getTenantId() cuando necesitan filtrar datos.
 *  3. El filtro llama a clear() en el bloque finally para evitar memory leaks.
 *
 * IMPORTANTE: En entornos reactivos (WebFlux) este patrón no funciona.
 * Para ms-users (Spring MVC / Servlet) es la solución estándar.
 */
@Slf4j
public final class TenantContext {

    private static final ThreadLocal<Long> CURRENT_TENANT = new ThreadLocal<>();

    private TenantContext() {
        // Utility class — no instanciable
    }

    /**
     * Establece el tenantId para el hilo actual.
     */
    public static void setTenantId(Long tenantId) {
        log.debug("Tenant context set: tenantId={}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Retorna el tenantId del hilo actual, o null si no se ha establecido.
     */
    public static Long getTenantId() {
        return CURRENT_TENANT.get();
    }

    /**
     * Retorna el tenantId o lanza excepción si no está presente.
     * Útil en operaciones que REQUIEREN contexto de tenant.
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

    /**
     * Limpia el contexto del hilo actual.
     * DEBE llamarse en el bloque finally del filtro para evitar memory leaks
     * en pools de hilos (Tomcat reutiliza hilos entre requests).
     */
    public static void clear() {
        log.debug("Tenant context cleared");
        CURRENT_TENANT.remove();
    }
}
