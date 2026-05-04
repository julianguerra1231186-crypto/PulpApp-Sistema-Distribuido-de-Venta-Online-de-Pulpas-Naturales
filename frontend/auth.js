/**
 * auth.js — Gestión de autenticación JWT en el navbar de Zentrix.
 *
 * Responsabilidades:
 *  - Leer/escribir/eliminar el token JWT de localStorage
 *  - Decodificar el payload del token para extraer nombre y rol
 *  - Detectar expiración del token
 *  - Renderizar el navbar según el estado de sesión
 *  - Exponer funciones reutilizables en window.PulpAuth
 */

(function initAuth(global) {

    const TOKEN_KEY  = "pulpapp_jwt";
    const USER_KEY   = "pulpapp_user";

    // ─────────────────────────────────────────────
    // Token helpers
    // ─────────────────────────────────────────────

    function saveToken(token) {
        localStorage.setItem(TOKEN_KEY, token);
    }

    function getToken() {
        return localStorage.getItem(TOKEN_KEY);
    }

    function removeToken() {
        localStorage.removeItem(TOKEN_KEY);
    }

    /**
     * Decodifica el payload del JWT sin verificar firma (solo frontend).
     * Retorna null si el token es inválido o está malformado.
     */
    function decodePayload(token) {
        try {
            const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
            return JSON.parse(atob(base64));
        } catch {
            return null;
        }
    }

    /**
     * Verifica si el token existe y no ha expirado.
     */
    function isTokenValid() {
        const token = getToken();
        if (!token) return false;

        const payload = decodePayload(token);
        if (!payload || !payload.exp) return false;

        // exp está en segundos, Date.now() en milisegundos
        return payload.exp * 1000 > Date.now();
    }

    /**
     * Retorna los datos del usuario desde el token o localStorage.
     */
    function getSessionUser() {
        const token = getToken();
        if (token) {
            const payload = decodePayload(token);
            if (payload) {
                return {
                    email: payload.sub,
                    role:  payload.role,
                    name:  payload.name || null
                };
            }
        }
        // Fallback: datos guardados por el flujo anterior del frontend
        try {
            return JSON.parse(localStorage.getItem(USER_KEY) || "null");
        } catch {
            return null;
        }
    }

    // ─────────────────────────────────────────────
    // Sesión
    // ─────────────────────────────────────────────

    /**
     * Guarda el token y los datos del usuario tras un login/register exitoso.
     * Llama a updateNavbar() para reflejar el cambio sin recargar.
     */
    function login(token, userData) {
        saveToken(token);
        if (userData) {
            localStorage.setItem(USER_KEY, JSON.stringify(userData));
        }
        updateNavbar();
    }

    /**
     * Elimina token y datos de sesión, redirige al login.
     */
    function logout() {
        removeToken();
        localStorage.removeItem(TOKEN_KEY);
        localStorage.removeItem(USER_KEY);
        localStorage.removeItem("user");
        updateNavbar();
        window.location.href = "login.html";
    }

    // ─────────────────────────────────────────────
    // Navbar dinámico
    // ─────────────────────────────────────────────

    /**
     * Actualiza el navbar según el estado de autenticación.
     * Se ejecuta al cargar la página y tras login/logout.
     */
    function updateNavbar() {
        const nav = document.querySelector(".site-nav");
        if (!nav) return;

        const authenticated = isTokenValid();
        const user          = getSessionUser();

        // Enlace "Mi cuenta" / "Iniciar sesión"
        const accountLink = nav.querySelector("[data-nav='account']");
        if (accountLink) {
            if (authenticated && user) {
                const displayName = user.name || user.email || "Mi cuenta";
                accountLink.textContent = `👤 ${displayName}`;
                accountLink.href        = "admin-dashboard.html";
                accountLink.title       = `Sesión activa — ${user.role || ""}`;
            } else {
                accountLink.textContent = "Iniciar sesión";
                accountLink.href        = "login.html";
                accountLink.title       = "";
            }
        }

        // Botón "Cerrar sesión"
        let logoutBtn = nav.querySelector("[data-nav='logout']");

        if (authenticated) {
            if (!logoutBtn) {
                logoutBtn = document.createElement("button");
                logoutBtn.dataset.nav   = "logout";
                logoutBtn.className     = "nav-logout-btn";
                logoutBtn.textContent   = "Cerrar sesión";
                logoutBtn.type          = "button";
                logoutBtn.addEventListener("click", logout);
                nav.appendChild(logoutBtn);
            }
            logoutBtn.classList.remove("hidden");
        } else {
            if (logoutBtn) logoutBtn.classList.add("hidden");
        }
    }

    // ─────────────────────────────────────────────
    // Inicialización automática al cargar la página
    // ─────────────────────────────────────────────

    document.addEventListener("DOMContentLoaded", () => {
        // Si el token expiró, limpia la sesión silenciosamente
        if (getToken() && !isTokenValid()) {
            removeToken();
            localStorage.removeItem(USER_KEY);
        }
        updateNavbar();
    });

    // ─────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────

    global.PulpAuth = {
        login,
        logout,
        getToken,
        isTokenValid,
        getSessionUser,
        updateNavbar
    };

})(window);
