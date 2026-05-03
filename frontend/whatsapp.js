/**
 * whatsapp.js — Botón flotante de WhatsApp dinámico.
 *
 * Lee el número de contacto desde GET /notifications (endpoint público)
 * a través de la configuración del sistema (app_config.whatsapp_number).
 *
 * Si el backend no responde o no hay número configurado, usa el número
 * de fallback hardcodeado para garantizar que el botón siempre funcione.
 *
 * Uso: incluir este script en cualquier página que tenga un elemento
 * con class="whatsapp-fab". El script actualiza el href automáticamente.
 */
(function initWhatsApp() {
    "use strict";

    const BASE            = "http://localhost:8090";
    const FALLBACK_NUMBER = "573001234567";
    const WA_MESSAGE      = "Hola%2C%20necesito%20ayuda%20con%20mi%20pedido%20en%20Zentrix";

    /**
     * Construye la URL de WhatsApp con el número dado.
     * @param {string} number - Número en formato internacional (solo dígitos)
     * @returns {string} URL completa de wa.me
     */
    function buildWhatsAppUrl(number) {
        const clean = (number || FALLBACK_NUMBER).replace(/\D/g, "");
        return `https://wa.me/${clean}?text=${WA_MESSAGE}`;
    }

    /**
     * Actualiza el href del botón flotante con el número dinámico.
     * @param {string} number
     */
    function updateFab(number) {
        const fab = document.querySelector(".whatsapp-fab");
        if (!fab) return;
        fab.href = buildWhatsAppUrl(number);
    }

    /**
     * Consulta el número de WhatsApp desde la configuración del sistema.
     * Endpoint público: GET /admin/config (retorna todas las configs).
     * Usa fallback silencioso si el servicio no responde.
     */
    async function loadWhatsAppNumber() {
        try {
            const res = await fetch(`${BASE}/admin/config`);
            if (!res.ok) {
                updateFab(FALLBACK_NUMBER);
                return;
            }
            const configs = await res.json();
            const waConfig = Array.isArray(configs)
                ? configs.find(c => c.configKey === "whatsapp_number")
                : null;

            updateFab(waConfig?.configValue || FALLBACK_NUMBER);
        } catch {
            // Si el backend no responde, el botón sigue funcionando con el fallback
            updateFab(FALLBACK_NUMBER);
        }
    }

    // Ejecutar cuando el DOM esté listo
    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", loadWhatsAppNumber);
    } else {
        loadWhatsAppNumber();
    }

})();
