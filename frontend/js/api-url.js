/**
 * =========================================================
 * api-url.js — Configuración Central del API Gateway
 * =========================================================
 * Zentrix SaaS — Red Matrix Solutions
 *
 * Este archivo es el ÚNICO punto de configuración para la URL
 * del API Gateway. Todas las páginas del frontend lo incluyen
 * y usan window.ZENTRIX_API para sus peticiones HTTP.
 *
 * ─── ARQUITECTURA ───────────────────────────────────────────
 *
 *   Frontend (Nginx/Cloud Run)
 *       │
 *       ▼
 *   API Gateway (Cloud Run)
 *       │
 *       ├── /auth/**        → ms-users
 *       ├── /users/**       → ms-users
 *       ├── /products/**    → ms-products
 *       ├── /orders/**      → ms-orders
 *       ├── /tenants/**     → ms-users
 *       ├── /clients/**     → ms-users
 *       ├── /inventory/**   → ms-users
 *       ├── /invoices/**    → ms-users
 *       └── ...
 *
 * ─── ENTORNOS ───────────────────────────────────────────────
 *
 *   PRODUCCIÓN (Cloud Run):
 *     URL: https://api-gateway-1010425986453.us-central1.run.app
 *     El frontend se despliega como servicio independiente.
 *     Todas las peticiones van directamente al gateway por HTTPS.
 *
 *   DOCKER COMPOSE (Local con contenedores):
 *     URL: /api (nginx proxy interno → api-gateway:8080)
 *     El frontend se sirve desde nginx que tiene un proxy /api/.
 *
 *   DESARROLLO LOCAL (sin Docker):
 *     URL: http://localhost:8090
 *     El gateway corre localmente mapeado al puerto 8090.
 *
 * ─── USO ────────────────────────────────────────────────────
 *
 *   1. Incluir este script ANTES de cualquier otro JS:
 *      <script src="js/api-url.js"></script>
 *
 *   2. Usar en código:
 *      const API = window.ZENTRIX_API;
 *      fetch(API + "/users/email?email=test@mail.com")
 *
 *   3. Override manual (para testing):
 *      Definir window.ZENTRIX_API_URL ANTES de cargar este script.
 *
 * ─── CAMBIOS ────────────────────────────────────────────────
 *
 *   ANTES: Cada archivo tenía "http://localhost:8090" hardcoded.
 *   AHORA: Un solo archivo controla la URL para todo el frontend.
 *
 * =========================================================
 */
(function(global) {
    "use strict";

    // ─────────────────────────────────────────────────────────
    // CONFIGURACIÓN DE PRODUCCIÓN
    // ─────────────────────────────────────────────────────────
    // URL del API Gateway desplegado en Google Cloud Run.
    // Esta es la URL DEFINITIVA de producción.
    var PRODUCTION_GATEWAY = "https://api-gateway-1010425986453.us-central1.run.app";

    // ─────────────────────────────────────────────────────────
    // DETECCIÓN AUTOMÁTICA DE ENTORNO
    // ─────────────────────────────────────────────────────────
    var hostname = global.location.hostname;
    var port = global.location.port;
    var protocol = global.location.protocol;

    // Determinar si estamos en producción (Cloud Run / dominio real)
    var isProduction = (
        hostname.includes("run.app") ||       // Cloud Run
        hostname.includes("zentrix") ||       // Dominio personalizado
        (protocol === "https:" && !hostname.includes("localhost"))  // HTTPS no-local
    );

    // Determinar si estamos en Docker Compose (nginx en puerto 80/3000)
    var isDocker = (
        hostname === "localhost" &&
        (!port || port === "80" || port === "3000")
    );

    // ─────────────────────────────────────────────────────────
    // RESOLUCIÓN FINAL DE URL
    // ─────────────────────────────────────────────────────────
    var resolvedUrl;

    if (global.ZENTRIX_API_URL) {
        // 1. Override manual (máxima prioridad)
        resolvedUrl = global.ZENTRIX_API_URL;
    } else if (isProduction) {
        // 2. Producción: Cloud Run gateway
        resolvedUrl = PRODUCTION_GATEWAY;
    } else if (isDocker) {
        // 3. Docker Compose: nginx proxy
        resolvedUrl = "/api";
    } else {
        // 4. Desarrollo local sin Docker
        resolvedUrl = "http://localhost:8090";
    }

    // Exponer globalmente
    global.ZENTRIX_API = resolvedUrl;

    // Log para debugging (solo en desarrollo)
    if (!isProduction) {
        console.log("[Zentrix] API Gateway:", resolvedUrl, "| Env:", isProduction ? "PROD" : isDocker ? "DOCKER" : "LOCAL");
    }

})(window);
