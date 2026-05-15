/**
 * api-url.js — Resolución dinámica de la URL del API Gateway.
 *
 * ANTES: Cada página tenía "http://localhost:8090" hardcoded.
 * PROBLEMA: No funciona en Docker ni Cloud Run.
 *
 * AHORA: Detección automática del entorno:
 *   - Docker/Cloud (nginx proxy en puerto 80/3000): usa "/api"
 *   - Desarrollo local (otro puerto): usa "http://localhost:8090"
 *   - Override manual: definir window.ZENTRIX_API_URL antes de cargar este script
 *
 * USO: Incluir este script ANTES de cualquier otro JS que use la API.
 *      Luego usar window.ZENTRIX_API para obtener la URL base.
 */
(function(global) {
    var port = global.location.port;
    var isNginx = (!port || port === "80" || port === "3000");
    global.ZENTRIX_API = global.ZENTRIX_API_URL || (isNginx ? "/api" : "http://localhost:8090");
})(window);
