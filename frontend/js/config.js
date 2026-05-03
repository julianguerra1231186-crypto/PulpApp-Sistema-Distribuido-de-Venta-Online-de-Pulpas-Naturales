/**
 * Configuración global de marca — Zentrix by Red Matrix Solutions.
 *
 * Centraliza todos los textos de branding para evitar hardcodeo.
 * Importar desde cualquier JS: window.APP_CONFIG
 */
(function initConfig(global) {

    global.APP_CONFIG = {
        name: "Zentrix",
        company: "Red Matrix Solutions",
        tagline: "Plataforma SaaS Multi-Tenant",
        footer: "Diseñado y desarrollado por Red Matrix Solutions",
        copyright: `© ${new Date().getFullYear()} Zentrix — Red Matrix Solutions`,
        whatsappGreeting: "Hola%2C%20necesito%20ayuda%20con%20mi%20pedido%20en%20Zentrix",
        supportEmail: "soporte@zentrix.app"
    };

})(window);
