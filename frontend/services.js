/*
    services.js centraliza el consumo de APIs REST y las utilidades de almacenamiento local.
    Esto separa la logica de datos de la logica visual para mantener una arquitectura frontend limpia.
*/

(function initializeServices(global) {
    /*
        Comunicacion via API Gateway (Spring Cloud Gateway), no directamente contra cada microservicio.
        Un solo origen (API_BASE_URL) centraliza las peticiones, alinea el frontend con la arquitectura
        y facilita CORS y despliegues. El gateway enruta /users, /products y /orders a ms-users,
        ms-products y ms-orders respectivamente.
        Puerto 8090: mapeo host en docker-compose (contenedor del gateway sigue en 8080).
    */
    const API_BASE_URL = "http://localhost:8090";
    const USERS_API_URL = `${API_BASE_URL}/users`;
    const PRODUCTS_API_URL = `${API_BASE_URL}/products`;
    const ORDERS_API_URL = `${API_BASE_URL}/orders`;

    /*
        Claves de localStorage.
        Se nombran de forma explicita para evitar colisiones con otros datos del navegador.
    */
    const STORAGE_KEYS = {
        cart: "pulpapp_cart",
        user: "pulpapp_user"
    };

    /*
        Intenta convertir una respuesta fetch en JSON si el cuerpo realmente existe.
        Esto evita errores cuando el backend devuelve cuerpo vacio.
    */
    async function parseJsonResponse(response) {
        const text = await response.text();

        if (!text) {
            return null;
        }

        try {
            return JSON.parse(text);
        } catch (error) {
            return { message: text };
        }
    }

    /*
        Normaliza el manejo de errores HTTP.
        Si el backend responde con error, construimos un mensaje util para la UI.
    */
    async function handleResponse(response, defaultMessage) {
        const data = await parseJsonResponse(response);

        if (!response.ok) {
            /*
                Se adjunta el codigo HTTP al error para que la capa de UI pueda tomar decisiones
                mas precisas, por ejemplo distinguir entre un duplicado y un fallo generico.
            */
            const error = new Error(data?.error || data?.message || defaultMessage);
            error.status = response.status;
            throw error;
        }

        return data;
    }

    /*
        Traduce mensajes tecnicos del backend a mensajes amables para el usuario final.
        Esto evita mostrar detalles internos de PostgreSQL o trazas de negocio poco claras.
    */
    function normalizeUserApiErrorMessage(message, fallbackMessage) {
        const normalizedMessage = String(message || "").toLowerCase();

        if (
            normalizedMessage.includes("duplicate")
            || normalizedMessage.includes("already exists")
            || normalizedMessage.includes("unique constraint")
            || normalizedMessage.includes("duplicate key")
        ) {
            return "Este usuario ya esta registrado. Inicia sesion.";
        }

        if (normalizedMessage.includes("not found")) {
            return "No encontramos un usuario con los datos suministrados.";
        }

        return fallbackMessage;
    }

    /*
        Determina si un error recibido desde ms-users corresponde a un caso de duplicidad.
        Esto permite responder con un mensaje de negocio claro en lugar de mostrar detalles tecnicos.
    */
    function isDuplicateUserError(error) {
        const normalizedMessage = String(error?.message || "").toLowerCase();
        const status = Number(error?.status || 0);

        return (
            status === 409
            || status === 500
            || normalizedMessage.includes("duplicate")
            || normalizedMessage.includes("already exists")
            || normalizedMessage.includes("unique constraint")
            || normalizedMessage.includes("duplicate key")
        );
    }

    /*
        Obtiene el catalogo completo desde ms-products.
    */
    async function fetchProducts() {
        const response = await fetch(PRODUCTS_API_URL);
        return handleResponse(response, "No fue posible obtener los productos");
    }

    /*
        Consulta un usuario por cedula y telefono.
        Esta es la forma de login disponible segun el backend actual.
    */
    async function findUserByDocument(cedula, telefono) {
        const response = await fetch(`${USERS_API_URL}/validar/${cedula}/${telefono}`);

        try {
            return await handleResponse(response, "No fue posible encontrar el usuario");
        } catch (error) {
            throw new Error(normalizeUserApiErrorMessage(error.message, "No encontramos un usuario con esa cedula y telefono."));
        }
    }

    /*
        Consulta un usuario unicamente por cedula.
        Se usa antes del registro para evitar intentar crear usuarios duplicados.
    */
    async function findUserByCedula(cedula) {
        /*
            El backend real expone la busqueda por cedula en /users/cedula/{cedula}.
            Se usa este contrato para verificar existencia antes de registrar y evitar duplicados.
        */
        const response = await fetch(`${USERS_API_URL}/cedula/${cedula}`);

        if (response.status === 404) {
            return null;
        }

        try {
            return await handleResponse(response, "No fue posible verificar el usuario");
        } catch (error) {
            throw new Error(normalizeUserApiErrorMessage(error.message, "No fue posible verificar si el usuario ya existe."));
        }
    }

    /*
        Crea un usuario nuevo via POST a USERS_API_URL (API Gateway → /users).
        El cuerpo es JSON (payload con password fija); Content-Type y Accept explicitos para el gateway y CORS.
    */
    async function createUser(userData) {
        const payload = {
            ...userData,
            password: "123456"
        };

        try {
            const existingUser = await findUserByCedula(userData.cedula);

            if (existingUser) {
                throw new Error("El usuario ya existe, puedes actualizar sus datos");
            }

            let response;

            try {
                response = await fetch(USERS_API_URL, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json"
                    },
                    body: JSON.stringify(payload)
                });
            } catch (networkError) {
                console.error("createUser: fallo de red o CORS al POST", USERS_API_URL, networkError);
                throw networkError;
            }

            if (!response.ok) {
                try {
                    const errBody = await response.clone().text();
                    console.error("createUser: respuesta HTTP no exitosa", response.status, errBody);
                } catch (logErr) {
                    console.error("createUser: respuesta HTTP no exitosa", response.status, logErr);
                }
            }

            return await handleResponse(response, "No fue posible registrar el usuario");
        } catch (error) {
            console.error("createUser:", error);

            if (isDuplicateUserError(error)) {
                throw new Error("Este usuario ya esta registrado");
            }

            throw new Error(normalizeUserApiErrorMessage(error.message, "No fue posible registrar el usuario."));
        }
    }

    /*
        Actualiza un usuario existente en ms-users.
    */
    async function updateUser(userId, userData) {
        const payload = {
            ...userData,
            password: "123456"
        };

        try {
            const existingUser = await findUserByCedula(userData.cedula);

            if (existingUser && Number(existingUser.id) !== Number(userId)) {
                throw new Error("Este usuario ya esta registrado");
            }

            const response = await fetch(`${USERS_API_URL}/${userId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            return await handleResponse(response, "No fue posible actualizar el usuario");
        } catch (error) {
            if (isDuplicateUserError(error)) {
                throw new Error("Este usuario ya esta registrado");
            }

            throw new Error(normalizeUserApiErrorMessage(error.message, "No fue posible actualizar el usuario."));
        }
    }

    /*
        Crea un pedido real en ms-orders.
        El payload debe cumplir exactamente con el contrato del microservicio.
    */
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

    /*
        Lee el carrito persistido.
        Si no existe informacion previa, devuelve un arreglo vacio.
    */
    function getCart() {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.cart) || "[]");
    }

    /*
        Guarda el carrito actual en localStorage.
    */
    function saveCart(cart) {
        localStorage.setItem(STORAGE_KEYS.cart, JSON.stringify(cart));
    }

    /*
        Obtiene el usuario autenticado localmente.
        Devuelve null cuando el usuario aun no se ha consultado o registrado.
    */
    function getCurrentUser() {
        return JSON.parse(localStorage.getItem(STORAGE_KEYS.user) || "null");
    }

    /*
        Persiste el usuario actual para reutilizarlo en otras paginas como carrito o login.
    */
    function saveCurrentUser(user) {
        localStorage.setItem(STORAGE_KEYS.user, JSON.stringify(user));
    }

    /*
        Limpia el usuario autenticado localmente.
    */
    function clearCurrentUser() {
        localStorage.removeItem(STORAGE_KEYS.user);
    }

    /*
        Exponemos un unico objeto global para que app.js consuma servicios
        sin contaminar el espacio global con multiples funciones sueltas.
    */
    global.PulpAppServices = {
        fetchProducts,
        findUserByDocument,
        findUserByCedula,
        createUser,
        updateUser,
        createOrder,
        getCart,
        saveCart,
        getCurrentUser,
        saveCurrentUser,
        clearCurrentUser
    };
})(window);
