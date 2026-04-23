/*
    services.js centraliza el consumo de APIs REST y las utilidades de almacenamiento local.
*/

(function initializeServices(global) {

    const API_BASE_URL = "http://localhost:8090";
    const API_URL = "http://localhost:8090/products";
    const USERS_API_URL = `${API_BASE_URL}/users`;
    const ORDERS_API_URL = `${API_BASE_URL}/orders`;

    const STORAGE_KEYS = {
        cart: "pulpapp_cart",
        user: "pulpapp_user"
    };

    async function parseJsonResponse(response) {
        const text = await response.text();
        if (!text) return null;

        try {
            return JSON.parse(text);
        } catch {
            return { message: text };
        }
    }

    async function handleResponse(response, defaultMessage) {
        const data = await parseJsonResponse(response);

        if (!response.ok) {
            const error = new Error(data?.error || data?.message || defaultMessage);
            error.status = response.status;
            throw error;
        }

        return data;
    }

    function normalizeProductList(data) {
        if (Array.isArray(data)) return data;
        if (data && Array.isArray(data.content)) return data.content;
        if (data && Array.isArray(data.products)) return data.products;
        return [];
    }

    /*
        🔥 FIX PRINCIPAL AQUÍ 🔥
        Quitamos mode, credentials y headers innecesarios
    */
    async function getProducts() {
        console.log("Fetching from:", API_URL);

        try {
            const response = await fetch(API_URL); // 🔥 SIMPLE

            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return normalizeProductList(data);

        } catch (error) {
            console.error("Error fetching products:", error);
            throw error;
        }
    }

    const fetchProducts = getProducts;

    async function findUserByDocument(cedula, telefono) {
        const response = await fetch(`${USERS_API_URL}/validar/${cedula}/${telefono}`);
        return handleResponse(response, "No fue posible encontrar el usuario");
    }

    async function findUserByCedula(cedula) {
        const response = await fetch(`${USERS_API_URL}/cedula/${cedula}`);

        if (response.status === 404) return null;

        return handleResponse(response, "No fue posible verificar el usuario");
    }

    async function createUser(userData) {
        // Usa la contraseña que viene en userData.
        // Si no viene (flujo legacy del carrito), asigna "123456" como temporal
        // y el usuario deberá cambiarla desde su perfil.
        const payload = {
            ...userData,
            password: userData.password || "123456"
        };

        const existingUser = await findUserByCedula(userData.cedula);

        if (existingUser) {
            throw new Error("El usuario ya existe");
        }

        const response = await fetch(USERS_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        return handleResponse(response, "No fue posible registrar el usuario");
    }

    async function updateUser(userId, userData) {
        const payload = {
            ...userData,
            password: "123456"
        };

        const response = await fetch(`${USERS_API_URL}/${userId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(payload)
        });

        return handleResponse(response, "No fue posible actualizar el usuario");
    }

    // ── Autenticación JWT ──────────────────────────────────────────

    async function authLogin(email, password) {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password })
        });
        const data = await handleResponse(response, "Credenciales incorrectas");
        // Guarda el token y actualiza el navbar automáticamente
        if (data?.token && window.PulpAuth) {
            window.PulpAuth.login(data.token, data);
        }
        return data;
    }

    async function authRegister(userData) {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(userData)
        });
        const data = await handleResponse(response, "No fue posible registrar el usuario");
        if (data?.token && window.PulpAuth) {
            window.PulpAuth.login(data.token, data);
        }
        return data;
    }

    async function createOrder(orderData) {
        const response = await fetch(ORDERS_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(orderData)
        });

        return handleResponse(response, "No fue posible registrar el pedido");
    }

    function getCart() {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.cart) || "[]");
    }

    function saveCart(cart) {
        localStorage.setItem(STORAGE_KEYS.cart, JSON.stringify(cart));
    }

    function getCurrentUser() {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.user) || "null");
    }

    function saveCurrentUser(user) {
        localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
    }

    function clearCurrentUser() {
        localStorage.removeItem(STORAGE_KEYS.user);
    }

    global.PulpAppServices = {
        fetchProducts,
        getProducts,
        findUserByDocument,
        findUserByCedula,
        createUser,
        updateUser,
        authLogin,
        authRegister,
        createOrder,
        getCart,
        saveCart,
        getCurrentUser,
        saveCurrentUser,
        clearCurrentUser
    };

})(window);