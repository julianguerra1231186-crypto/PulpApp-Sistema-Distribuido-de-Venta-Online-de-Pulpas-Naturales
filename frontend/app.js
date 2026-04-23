/*
    Archivo principal del frontend de PulpApp.
    Su responsabilidad es inicializar cada pagina, conectar la interfaz con services.js
    y mantener la experiencia de compra coherente en toda la navegacion.
*/

/*
    Referencia directa al modulo global de servicios.
    El archivo services.js expone todas las operaciones de API y almacenamiento local.
*/
const api = window.PulpAppServices;

/*
    Persiste un usuario en localStorage usando tanto la clave "user" solicitada en varios flujos
    como la clave historica del proyecto. Esto asegura compatibilidad con el resto del frontend
    y hace que los datos permanezcan aunque se recargue la pagina.
*/
function saveUserToLocalStorage(user) {
    localStorage.setItem("user", JSON.stringify(user));
    api.saveCurrentUser(user);
}

/*
    Recupera el usuario guardado localmente.
    Primero intenta leer la clave "user" y luego usa la clave historica "pulpapp_user"
    para no perder sesiones que ya estuvieran almacenadas.
*/
function loadUserFromLocalStorage() {
    return JSON.parse(localStorage.getItem("user") || localStorage.getItem("pulpapp_user") || "null");
}

/*
    Referencias a imagenes locales para dar una mejor presentacion al catalogo
    cuando un producto no expone una URL de imagen desde backend.
*/
const FALLBACK_IMAGES = [
    "img/fresa.png",
    "img/mora.png",
    "img/maracuya.png",
    "img/Lulo.png",
    "img/coco.png",
    "img/durazno.png",
    "img/mango.png",
    "img/manzana.png",
    "img/Piña.png"
];

/*
    Estado compartido del frontend.
    Se carga una vez y luego se sincroniza contra el DOM y localStorage.
*/
const state = {
    cart: api.getCart(),
    user: loadUserFromLocalStorage(),
    products: [],
    toastTimer: null
};

/*
    Helper para acceder al atributo data-page de cada documento.
    Esto permite que un solo app.js inicialice varias paginas sin duplicar logica.
*/
function getCurrentPage() {
    return document.body?.dataset?.page || "";
}

/*
    Convierte numeros a formato colombiano para presentar precios
    de una manera mas profesional y entendible para el usuario final.
*/
function formatCurrency(value) {
    return `$${new Intl.NumberFormat("es-CO").format(Number(value || 0))}`;
}

/*
    Normaliza texto para comparar nombres de productos ignorando tildes,
    mayusculas y signos especiales cuando se usa la busqueda del catalogo.
*/
function normalizeText(text) {
    return (text || "")
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^\w\s]/g, "")
        .trim()
        .toLowerCase();
}

/*
    Muestra un mensaje visual temporal.
    Reemplaza el uso de alert() para mantener una experiencia mas profesional y menos invasiva.
    El parametro tipo permite diferenciar entre mensajes de exito y error.
*/
function mostrarMensaje(message, tipo = "success") {
    const toast = document.getElementById("appToast");

    if (!toast) {
        return;
    }

    toast.textContent = message;
    toast.classList.add("show");
    toast.classList.toggle("error", tipo === "error");

    clearTimeout(state.toastTimer);
    state.toastTimer = setTimeout(() => {
        toast.classList.remove("show");
        toast.classList.remove("error");
    }, 2800);
}

window.mostrarMensaje = mostrarMensaje;

/*
    Devuelve una imagen valida para cada producto.
    Si el backend no envia imagen, el frontend asigna una ilustracion local.
*/
function getProductImage(product, index) {
    if (product.imageUrl && product.imageUrl.trim()) {
        return product.imageUrl;
    }

    return FALLBACK_IMAGES[index % FALLBACK_IMAGES.length];
}

/*
    Calcula el total y la cantidad total del carrito.
    Este resumen se reutiliza en cabecera, landing y checkout.
*/
function getCartSummary() {
    return state.cart.reduce((summary, item) => {
        summary.quantity += Number(item.cantidad || 0);
        summary.total += Number(item.precio || 0) * Number(item.cantidad || 0);
        return summary;
    }, { quantity: 0, total: 0 });
}

/*
    Persiste el carrito actualizado y refresca los indicadores globales del sitio.
*/
function persistCart() {
    api.saveCart(state.cart);
    renderGlobalIndicators();
}

/*
    Persiste el usuario actual y mantiene sincronizada la sesion local del navegador.
*/
function persistUser(user) {
    state.user = user;
    saveUserToLocalStorage(user);
    renderGlobalIndicators();
}

/*
    Renderiza los indicadores visibles en todas las paginas, como contador del carrito
    y estado general del usuario autenticado localmente.
*/
function renderGlobalIndicators() {
    const cartSummary = getCartSummary();
    const navCount = document.getElementById("navCartCount");

    if (navCount) {
        navCount.textContent = String(cartSummary.quantity);
    }

    if (getCurrentPage() === "home") {
        const homeUserStatus = document.getElementById("homeUserStatus");
        const homeUserDetail = document.getElementById("homeUserDetail");
        const homeCartStatus = document.getElementById("homeCartStatus");
        const homeCartDetail = document.getElementById("homeCartDetail");

        if (homeUserStatus && homeUserDetail) {
            if (state.user?.id) {
                homeUserStatus.textContent = `Sesion activa: ${state.user.name}`;
                homeUserDetail.textContent = `${state.user.email || "Sin correo"} | ${state.user.direccion || "Sin direccion"}`;
            } else {
                homeUserStatus.textContent = "No has iniciado sesion";
                homeUserDetail.textContent = "Ingresa en la seccion Mi cuenta para autocompletar tu compra.";
            }
        }

        if (homeCartStatus && homeCartDetail) {
            homeCartStatus.textContent = `${cartSummary.quantity} producto${cartSummary.quantity === 1 ? "" : "s"} agregados`;
            homeCartDetail.textContent = cartSummary.quantity
                ? `Total acumulado: ${formatCurrency(cartSummary.total)}`
                : "Tu resumen se actualiza automaticamente mientras navegas.";
        }
    }

    if (getCurrentPage() === "catalog") {
        const summaryTitle = document.getElementById("catalogSummaryTitle");
        const summaryText = document.getElementById("catalogSummaryText");

        if (summaryTitle && summaryText) {
            summaryTitle.textContent = `${cartSummary.quantity} producto${cartSummary.quantity === 1 ? "" : "s"} en el carrito`;
            summaryText.textContent = cartSummary.quantity
                ? `Llevas ${formatCurrency(cartSummary.total)} acumulados en tu compra.`
                : "Agrega productos para construir tu pedido.";
        }
    }

    if (getCurrentPage() === "login") {
        const statusTitle = document.getElementById("loginStatusTitle");
        const statusText = document.getElementById("loginStatusText");

        if (statusTitle && statusText) {
            if (state.user?.id) {
                statusTitle.textContent = `Sesion activa con ${state.user.name}`;
                statusText.textContent = `El checkout podra autocompletar ${state.user.cedula} y ${state.user.direccion || "sin direccion registrada"}.`;
            } else {
                statusTitle.textContent = "Sin sesion activa";
                statusText.textContent = "Consulta o registra un usuario para continuar con el checkout.";
            }
        }
    }
}

/*
    Intenta alinear el carrito local con la informacion actual del catalogo remoto.
    Esto evita inconsistencias cuando cambian nombre o precio de un producto.
*/
function syncCartWithProducts() {
    if (!state.products.length) {
        return;
    }

    state.cart = state.cart.map((item) => {
        const product = state.products.find((candidate) => candidate.id === item.id);

        if (product) {
            return {
                ...item,
                nombre: product.name,
                precio: Number(product.price)
            };
        }

        const byName = state.products.find((candidate) => normalizeText(candidate.name) === normalizeText(item.nombre));

        if (!byName) {
            return item;
        }

        return {
            ...item,
            id: byName.id,
            nombre: byName.name,
            precio: Number(byName.price)
        };
    });

    persistCart();
}

/*
    Agrega un producto al carrito usando su identificador real de ms-products.
*/
function addToCart(productId) {
    const product = state.products.find((item) => item.id === productId);

    if (!product) {
        mostrarMensaje("No fue posible agregar el producto", "error");
        return;
    }

    const existingItem = state.cart.find((item) => item.id === productId);

    if (existingItem) {
        existingItem.cantidad += 1;
    } else {
        state.cart.push({
            id: product.id,
            nombre: product.name,
            precio: Number(product.price),
            cantidad: 1
        });
    }

    persistCart();
    renderCartPage();
    mostrarMensaje("Producto agregado al carrito", "success");
}

/*
    Disminuye la cantidad de un item. Si llega a cero, elimina la linea del carrito.
*/
function decreaseCartItem(index) {
    if (!state.cart[index]) {
        return;
    }

    if (state.cart[index].cantidad > 1) {
        state.cart[index].cantidad -= 1;
    } else {
        state.cart.splice(index, 1);
    }

    persistCart();
    renderCartPage();
}

/*
    Incrementa la cantidad de un producto dentro del carrito.
*/
function increaseCartItem(index) {
    if (!state.cart[index]) {
        return;
    }

    state.cart[index].cantidad += 1;
    persistCart();
    renderCartPage();
}

/*
    Elimina completamente un item del carrito.
*/
function removeCartItem(index) {
    state.cart.splice(index, 1);
    persistCart();
    renderCartPage();
    mostrarMensaje("Producto eliminado del carrito", "success");
}

/*
    Vacia el carrito de compras y refresca la interfaz.
*/
function clearCart() {
    state.cart = [];
    persistCart();
    renderCartPage();
    mostrarMensaje("Carrito vaciado", "success");
}

/*
    Carga el catalogo desde ms-products y conserva la informacion en memoria
    para reutilizarla en busquedas, carrito y checkout.
*/
function loadProducts() {
    return api.fetchProducts()
        .then((products) => {
            const list = Array.isArray(products) ? products : [];

            if (products != null && !Array.isArray(products)) {
                console.warn("loadProducts: se esperaba un array de productos, se recibio:", typeof products, products);
            }

            state.products = list;
            syncCartWithProducts();
            renderHomePage();
            renderCatalogPage(list);
            renderCartPage();
        })
        .catch((error) => {
            console.error("Error al cargar productos:", error);
            console.error("Detalle backend / red:", error.message, error.status || "");
            mostrarMensaje(error.message || "No fue posible cargar el catalogo", "error");
            state.products = [];
            renderCatalogPage([]);
        });
}

/*
    Renderiza la landing con el total de productos publicados.
*/
function renderHomePage() {
    if (getCurrentPage() !== "home") {
        return;
    }

    const homeProductCount = document.getElementById("homeProductCount");

    if (homeProductCount) {
        homeProductCount.textContent = String(state.products.length);
    }
}

/*
    Dibuja el grid de productos del catalogo.
    Cada tarjeta incluye nombre, precio, stock y accion para agregar al carrito.
*/
function renderCatalogPage(products = state.products) {
    if (getCurrentPage() !== "catalog") {
        return;
    }

    const catalogGrid = document.getElementById("catalogGrid");

    if (!catalogGrid) {
        return;
    }

    const items = Array.isArray(products) ? products : [];

    if (!items.length) {
        catalogGrid.innerHTML = `
            <article class="empty-state">
                <h3>No hay productos disponibles</h3>
                <p>El microservicio ms-products no devolvio productos para mostrar.</p>
            </article>
        `;
        renderGlobalIndicators();
        return;
    }

    catalogGrid.innerHTML = items.map((product, index) => {
        const image = getProductImage(product, index);
        const isAvailable = product.available !== false;

        return `
            <article class="product-card" data-name="${normalizeText(product.name)}">
                <div class="product-media">
                    <img src="${image}" alt="${product.name}" data-image="${image}">
                </div>
                <div class="product-body">
                    <span class="stock-badge">${isAvailable ? "Disponible" : "Agotado"}</span>
                    <h3>${product.name}</h3>
                    <p>${product.description || "Pulpa natural lista para compra en linea."}</p>
                    <div class="product-meta">
                        <strong class="price-tag">${formatCurrency(product.price)}</strong>
                        <span class="stock-text">Stock: ${product.stock ?? "No informado"}</span>
                    </div>
                    <button class="btn btn-primary" type="button" data-add-product="${product.id}" ${isAvailable ? "" : "disabled"}>
                        ${isAvailable ? "Agregar al carrito" : "No disponible"}
                    </button>
                </div>
            </article>
        `;
    }).join("");

    bindCatalogEvents();
    renderGlobalIndicators();
}

/*
    Enlaza los eventos de las tarjetas del catalogo y del filtro de busqueda.
*/
function bindCatalogEvents() {
    if (getCurrentPage() !== "catalog") {
        return;
    }

    document.querySelectorAll("[data-add-product]").forEach((button) => {
        button.addEventListener("click", () => {
            addToCart(Number(button.dataset.addProduct));
        });
    });

    document.querySelectorAll(".product-media img").forEach((image) => {
        image.addEventListener("click", () => openProductModal(image.dataset.image));
    });
}

/*
    Aplica el filtro de busqueda sin volver a consultar backend.
*/
function filterCatalog(query) {
    const normalizedQuery = normalizeText(query);
    const filteredProducts = state.products.filter((product) => normalizeText(product.name).includes(normalizedQuery));
    renderCatalogPage(filteredProducts);
}

/*
    Abre el modal de imagen ampliada en el catalogo.
*/
function openProductModal(imageSrc) {
    const modal = document.getElementById("productModal");
    const modalImage = document.getElementById("modalImage");

    if (!modal || !modalImage) {
        return;
    }

    modalImage.src = imageSrc;
    modal.classList.remove("hidden");
    modal.setAttribute("aria-hidden", "false");
}

/*
    Cierra el modal de detalle de imagen.
*/
function closeProductModal() {
    const modal = document.getElementById("productModal");

    if (!modal) {
        return;
    }

    modal.classList.add("hidden");
    modal.setAttribute("aria-hidden", "true");
}

/*
    Dibuja los productos del carrito dentro de cart.html.
    Incluye controles de cantidad y resumen final.
*/
function renderCartPage() {
    if (getCurrentPage() !== "cart") {
        renderGlobalIndicators();
        return;
    }

    const cartList = document.getElementById("cartList");
    const cartItemsCount = document.getElementById("cartItemsCount");
    const cartGrandTotal = document.getElementById("cartGrandTotal");

    if (!cartList || !cartItemsCount || !cartGrandTotal) {
        return;
    }

    if (!state.cart.length) {
        cartList.innerHTML = `
            <article class="empty-state">
                <h3>Tu carrito esta vacio</h3>
                <p>Ve al catalogo, agrega productos y vuelve para finalizar tu compra.</p>
            </article>
        `;
    } else {
        cartList.innerHTML = state.cart.map((item, index) => `
            <article class="cart-item">
                <div>
                    <strong>${item.nombre}</strong>
                    <p>${formatCurrency(item.precio)} por unidad</p>
                    <small>Subtotal: ${formatCurrency(Number(item.precio) * Number(item.cantidad))}</small>
                </div>
                <div class="quantity-actions">
                    <button type="button" data-cart-action="decrease" data-index="${index}">-</button>
                    <span>${item.cantidad}</span>
                    <button type="button" data-cart-action="increase" data-index="${index}">+</button>
                    <button type="button" data-cart-action="remove" data-index="${index}">X</button>
                </div>
            </article>
        `).join("");
    }

    const summary = getCartSummary();
    cartItemsCount.textContent = String(summary.quantity);
    cartGrandTotal.textContent = formatCurrency(summary.total);

    fillCheckoutForm();
    bindCartEvents();
    renderGlobalIndicators();
}

/*
    Conecta los botones de carrito y de acciones del checkout.
*/
function bindCartEvents() {
    if (getCurrentPage() !== "cart") {
        return;
    }

    document.querySelectorAll("[data-cart-action]").forEach((button) => {
        button.addEventListener("click", () => {
            const index = Number(button.dataset.index);
            const action = button.dataset.cartAction;

            if (action === "increase") {
                increaseCartItem(index);
            }

            if (action === "decrease") {
                decreaseCartItem(index);
            }

            if (action === "remove") {
                removeCartItem(index);
            }
        });
    });
}

/*
    Toma el usuario guardado localmente y autocompleta el formulario del checkout.
    Solo actúa si el script inline del carrito no tomó el control (no hay sesión JWT activa).
*/
function fillCheckoutForm() {
    if (getCurrentPage() !== "cart") {
        return;
    }

    // Si hay sesión JWT válida, el script inline de cart.html maneja el formulario
    if (window.PulpAuth?.isTokenValid()) {
        return;
    }

    const fields = {
        checkoutCedula: state.user?.cedula || "",
        checkoutTelefono: state.user?.telefono || "",
        checkoutNombre: state.user?.name || "",
        checkoutCorreo: state.user?.email || "",
        checkoutDireccion: state.user?.direccion || ""
    };

    Object.entries(fields).forEach(([fieldId, value]) => {
        const input = document.getElementById(fieldId);

        if (input) {
            input.value = value;
        }
    });
}

/*
    Controla el estado visual del formulario del checkout.
    Solo actúa si no hay sesión JWT activa (el script inline de cart.html maneja ese caso).
*/
function syncCheckoutAccessState() {
    if (getCurrentPage() !== "cart") {
        return;
    }

    // Si hay sesión JWT válida, el script inline de cart.html maneja todo
    if (window.PulpAuth?.isTokenValid()) {
        return;
    }

    // Sin JWT: flujo legacy con state.user
    const activeUser = JSON.parse(localStorage.getItem("user") || "null") || state.user;
    const loginNotice = document.getElementById("checkoutLoginNotice");
    const loginRedirectButton = document.getElementById("checkoutLoginRedirectButton");
    const updateUserButton = document.getElementById("updateUserButton");
    const checkoutInputs = [
        document.getElementById("checkoutCedula"),
        document.getElementById("checkoutTelefono"),
        document.getElementById("checkoutNombre"),
        document.getElementById("checkoutCorreo"),
        document.getElementById("checkoutDireccion")
    ];

    if (!activeUser?.id) {
        checkoutInputs.forEach((input) => { if (input) input.disabled = true; });
        loginNotice?.classList.remove("hidden");
        loginRedirectButton?.classList.remove("hidden");
        updateUserButton?.classList.add("hidden");
        return;
    }

    state.user = activeUser;
    checkoutInputs.forEach((input) => { if (input) input.disabled = false; });
    loginNotice?.classList.add("hidden");
    loginRedirectButton?.classList.add("hidden");
    updateUserButton?.classList.remove("hidden");
    fillCheckoutForm();
}

/*
    Obtiene los datos actuales del formulario del checkout.
    Se usa tanto para registrar como para actualizar usuarios.
*/
function getCheckoutFormData() {
    return {
        cedula: document.getElementById("checkoutCedula")?.value.trim() || "",
        telefono: document.getElementById("checkoutTelefono")?.value.trim() || "",
        name: document.getElementById("checkoutNombre")?.value.trim() || "",
        email: document.getElementById("checkoutCorreo")?.value.trim() || "",
        direccion: document.getElementById("checkoutDireccion")?.value.trim() || ""
    };
}

/*
    Obtiene los datos del formulario de login/registro.
*/
function getAuthFormData() {
    return {
        cedula: document.getElementById("authCedula")?.value.trim() || "",
        telefono: document.getElementById("authTelefono")?.value.trim() || "",
        name: document.getElementById("authNombre")?.value.trim() || "",
        email: document.getElementById("authCorreo")?.value.trim() || "",
        direccion: document.getElementById("authDireccion")?.value.trim() || ""
    };
}

/*
    Verifica que el formulario tenga los campos minimos necesarios.
    Se usa antes de registrar o actualizar un usuario.
*/
function validateUserData(userData) {
    if (!userData.cedula || !userData.telefono || !userData.name || !userData.email || !userData.direccion) {
        throw new Error("Completa todos los datos del usuario");
    }
}

/*
    Sincroniza el estado visual de los botones de autenticacion.
    Cuando el usuario ya existe, registrar se bloquea y actualizar queda habilitado para
    conducir al usuario por el flujo correcto en lugar de intentar un alta duplicada.
*/
function setAuthActionState({ userExists, hasSession }) {
    const registerButton = document.getElementById("registerButton") || document.getElementById("saveUserButton");
    const updateButton = document.getElementById("updateButton") || document.getElementById("updateUserButton");
    const consultButton = document.getElementById("loginButton") || document.getElementById("findUserButton");

    if (registerButton) {
        registerButton.disabled = Boolean(userExists);
    }

    if (updateButton) {
        updateButton.disabled = !(Boolean(hasSession) || Boolean(userExists));
        updateButton.classList.toggle("hidden", !(Boolean(hasSession) || Boolean(userExists)));
    }

    /*
        El boton de consulta se mantiene habilitado siempre que exista interfaz disponible,
        porque consultar es la puerta de entrada para cargar datos ya registrados.
    */
    if (consultButton) {
        consultButton.disabled = false;
    }
}

/*
    Verifica si ya existe un usuario con la cedula proporcionada.
    Esta consulta preventiva evita disparar un POST innecesario cuando la base de datos
    ya tiene registrado ese documento.
*/
async function checkUserExists(cedula) {
    if (!cedula) {
        return null;
    }

    try {
        const existingUser = await api.findUserByCedula(cedula);
        setAuthActionState({
            userExists: Boolean(existingUser),
            hasSession: Boolean(state.user?.id || existingUser?.id)
        });
        return existingUser;
    } catch (error) {
        /*
            Si falla la consulta y no es un caso de "no encontrado", se propaga
            un mensaje claro de conectividad o verificacion.
        */
        const message = String(error.message || "").toLowerCase();

        if (message.includes("failed to fetch") || message.includes("network") || message.includes("connection")) {
            throw new Error("Error de conexion con el servidor");
        }

        throw error;
    }
}

/*
    Consulta un usuario por cedula para llenar el formulario, persistirlo localmente y
    permitir que el usuario siga con el flujo de compra o actualizacion.
*/
async function findUserByCedulaFlow(cedula) {
    if (!cedula) {
        throw new Error("Ingresa la cedula del usuario");
    }

    try {
        const user = await api.findUserByCedula(cedula);
        persistUser(user);
        setAuthActionState({
            userExists: true,
            hasSession: true
        });
        return user;
    } catch (error) {
        const message = String(error.message || "").toLowerCase();

        if (message.includes("not found") || message.includes("no encontramos")) {
            setAuthActionState({
                userExists: false,
                hasSession: false
            });
            throw new Error("Usuario no encontrado");
        }

        if (message.includes("failed to fetch") || message.includes("network") || message.includes("connection")) {
            throw new Error("Error de conexion con el servidor");
        }

        throw error;
    }
}

/*
    Conserva el flujo de login por cedula y telefono para pantallas donde todavia se requiere
    una validacion mas estricta antes de considerar al usuario autenticado.
*/
async function loginUser(cedula, telefono) {
    if (!cedula || !telefono) {
        throw new Error("Ingresa cedula y telefono");
    }

    try {
        const user = await api.findUserByDocument(cedula, telefono);
        persistUser(user);
        setAuthActionState({
            userExists: true,
            hasSession: true
        });
        return user;
    } catch (error) {
        const message = String(error.message || "").toLowerCase();

        if (message.includes("failed to fetch") || message.includes("network") || message.includes("connection")) {
            throw new Error("Error de conexion con el servidor");
        }

        throw error;
    }
}

/*
    Crea un usuario nuevo en ms-users solo cuando la cedula aun no existe.
    Si el usuario ya esta registrado, se evita el POST y se orienta la interfaz hacia actualizacion.
*/
async function createUser(userData) {
    validateUserData(userData);

    try {
        const existingUser = await checkUserExists(userData.cedula);

        if (existingUser) {
            persistUser(existingUser);
            setAuthActionState({
                userExists: true,
                hasSession: true
            });
            throw new Error("El usuario ya existe, puedes actualizar sus datos");
        }

        const createdUser = await api.createUser(userData);
        persistUser(createdUser);
        setAuthActionState({
            userExists: true,
            hasSession: true
        });
        return createdUser;
    } catch (error) {
        const message = String(error.message || "").toLowerCase();

        if (message.includes("duplicado") || message.includes("duplicate") || message.includes("registrado")) {
            throw new Error("Este usuario ya esta registrado");
        }

        if (message.includes("failed to fetch") || message.includes("network") || message.includes("connection")) {
            throw new Error("Error de conexion con el servidor");
        }

        throw error;
    }
}

/*
    Actualiza un usuario existente dentro de ms-users.
    Si no hay sesion activa pero la cedula existe, primero adopta ese usuario como contexto local
    para habilitar un flujo de actualizacion mas directo.
*/
async function updateUser(cedula, userData) {
    validateUserData(userData);

    try {
        if (!state.user?.id) {
            const existingUser = await checkUserExists(cedula);

            if (!existingUser?.id) {
                throw new Error("Usuario no encontrado");
            }

            persistUser(existingUser);
        }

        const updatedUser = await api.updateUser(state.user.id, userData);
        persistUser(updatedUser);
        setAuthActionState({
            userExists: true,
            hasSession: true
        });
        return updatedUser;
    } catch (error) {
        const message = String(error.message || "").toLowerCase();

        if (message.includes("failed to fetch") || message.includes("network") || message.includes("connection")) {
            throw new Error("Error de conexion con el servidor");
        }

        throw error;
    }
}

/*
    Copia el usuario autenticado al formulario de login cuando la pagina carga,
    de modo que el usuario vea claramente su sesion actual.
*/
function fillAuthForm() {
    if (getCurrentPage() !== "login") {
        return;
    }

    const fields = {
        authCedula: state.user?.cedula || "",
        authTelefono: state.user?.telefono || "",
        authNombre: state.user?.name || "",
        authCorreo: state.user?.email || "",
        authDireccion: state.user?.direccion || ""
    };

    Object.entries(fields).forEach(([fieldId, value]) => {
        const input = document.getElementById(fieldId);

        if (input) {
            input.value = value;
        }
    });

    setAuthActionState({
        userExists: Boolean(state.user?.id),
        hasSession: Boolean(state.user?.id)
    });
}

/*
    Cierra la sesion local del frontend sin tocar backend.
    La evidencia queda en que el autocompletado y el checkout dejan de tener usuario activo.
*/
function logoutUser() {
    state.user = null;
    localStorage.removeItem("user");
    api.clearCurrentUser();
    renderGlobalIndicators();
    fillAuthForm();
    fillCheckoutForm();
    syncCheckoutAccessState();
    setAuthActionState({
        userExists: false,
        hasSession: false
    });
    mostrarMensaje("Sesion local cerrada", "success");
}

/*
    Construye el payload exacto que espera ms-orders a partir del carrito actual.
*/
function buildOrderPayload() {
    // Intenta obtener el usuario desde state, localStorage o JWT
    if (!state.user?.id) {
        const stored = JSON.parse(localStorage.getItem("pulpapp_user") || localStorage.getItem("user") || "null");
        if (stored?.id) {
            state.user = stored;
        }
    }

    if (!state.user?.id) {
        throw new Error("Debes iniciar sesion antes de comprar");
    }

    if (!state.cart.length) {
        throw new Error("Tu carrito esta vacio");
    }

    const payload = {
        userId: state.user.id,
        items: state.cart.map((item) => ({
            productId: item.id,
            cantidad: item.cantidad
        }))
    };

    const invalidItem = payload.items.find((item) => !item.productId || !item.cantidad);

    if (invalidItem) {
        throw new Error("Uno o mas productos del carrito no tienen identificador valido");
    }

    return payload;
}

/*
    Registra el pedido real en ms-orders y redirige a la página de confirmación
    donde el cliente elige el método de pago y se envía el mensaje de WhatsApp.
*/
async function submitOrder() {
    const checkoutData = getCheckoutFormData();

    if (!checkoutData.direccion) {
        throw new Error("Ingresa la direccion de entrega antes de finalizar");
    }

    // Sincroniza state.user desde localStorage si viene de flujo JWT
    if (!state.user?.id) {
        const stored = JSON.parse(localStorage.getItem("pulpapp_user") || localStorage.getItem("user") || "null");
        if (stored?.id) state.user = stored;
    }

    if (state.user?.direccion !== checkoutData.direccion) {
        state.user = { ...state.user, ...checkoutData };
        persistUser(state.user);
    }

    const payload = buildOrderPayload();
    const createdOrder = await api.createOrder(payload);

    // Limpiar carrito
    state.cart = [];
    persistCart();
    renderCartPage();

    // Redirigir a la página de confirmación con el ID del pedido
    // La página de confirmación maneja el método de pago y el WhatsApp
    window.location.href = `pedido-confirmado.html?id=${createdOrder?.id || ""}`;

    return createdOrder;
}

/*
    Inicializa la pagina de catalogo.
    Configura busqueda, modal y carga remota de productos.
*/
function initCatalogPage() {
    if (getCurrentPage() !== "catalog") {
        return;
    }

    const searchInput = document.getElementById("catalogSearchInput");
    const closeModalButton = document.getElementById("closeProductModal");
    const modal = document.getElementById("productModal");

    if (searchInput) {
        searchInput.addEventListener("input", (event) => filterCatalog(event.target.value));
    }

    if (closeModalButton) {
        closeModalButton.addEventListener("click", closeProductModal);
    }

    if (modal) {
        modal.addEventListener("click", (event) => {
            if (event.target === modal) {
                closeProductModal();
            }
        });
    }

    loadProducts();
}

/*
    Inicializa la pagina del carrito.
    Enlaza los botones de usuario y el checkout final.
*/
function initCartPage() {
    if (getCurrentPage() !== "cart") {
        return;
    }

    const clearCartButton = document.getElementById("clearCartButton");
    const updateUserButton = document.getElementById("updateUserButton");
    const checkoutButton = document.getElementById("checkoutButton");
    const checkoutLoginRedirectButton = document.getElementById("checkoutLoginRedirectButton");

    if (clearCartButton) {
        clearCartButton.addEventListener("click", clearCart);
    }

    /*
        Este boton dirige al flujo de autenticacion cuando el usuario aun no ha iniciado sesion.
        Se evita permitir escritura manual en checkout sin una identidad valida.
    */
    if (checkoutLoginRedirectButton) {
        checkoutLoginRedirectButton.addEventListener("click", () => {
            window.location.href = "login.html";
        });
    }

    if (updateUserButton) {
        updateUserButton.addEventListener("click", async () => {
            try {
                const checkoutData = getCheckoutFormData();
                const updated = await updateUser(checkoutData.cedula, checkoutData);
                fillCheckoutForm();
                syncCheckoutAccessState();
                mostrarMensaje(`Usuario ${updated.name} actualizado correctamente`, "success");
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible actualizar el usuario", "error");
            }
        });
    }

    if (checkoutButton) {
        checkoutButton.addEventListener("click", async () => {
            try {
                await submitOrder();
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible registrar el pedido", "error");
            }
        });
    }

    loadProducts();
    renderCartPage();
    syncCheckoutAccessState();
}

/*
    Inicializa la pagina de autenticacion.
    Reutiliza ms-users para consulta, registro y actualizacion.
*/
function initLoginPage() {
    if (getCurrentPage() !== "login") {
        return;
    }

    const loginButton = document.getElementById("loginButton") || document.getElementById("findUserButton");
    const registerButton = document.getElementById("registerButton") || document.getElementById("saveUserButton");
    const updateButton = document.getElementById("updateButton") || document.getElementById("updateUserButton");
    const logoutButton = document.getElementById("logoutButton");
    const authCedulaInput = document.getElementById("authCedula");

    fillAuthForm();

    /*
        Revisa la cedula mientras el usuario interactua con el formulario.
        Si la cedula ya existe, se bloquea registrar y se habilita actualizar para reforzar el flujo correcto.
    */
    if (authCedulaInput) {
        authCedulaInput.addEventListener("blur", async () => {
            try {
                const existingUser = await checkUserExists(authCedulaInput.value.trim());

                if (existingUser) {
                    mostrarMensaje("El usuario ya existe, puedes actualizar sus datos", "success");
                } else if (!state.user?.id) {
                    setAuthActionState({
                        userExists: false,
                        hasSession: false
                    });
                }
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible verificar la cedula", "error");
            }
        });
    }

    if (loginButton) {
        loginButton.addEventListener("click", async () => {
            try {
                const formData = getAuthFormData();
                const user = await findUserByCedulaFlow(formData.cedula);
                fillAuthForm();
                mostrarMensaje(`Usuario encontrado: ${user.name}`, "success");
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible consultar el usuario", "error");
            }
        });
    }

    if (registerButton) {
        registerButton.addEventListener("click", async () => {
            try {
                const created = await createUser(getAuthFormData());
                fillAuthForm();
                mostrarMensaje(`Usuario ${created.name} registrado correctamente. Tu sesion ya quedo iniciada.`, "success");
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible registrar el usuario", "error");
            }
        });
    }

    if (updateButton) {
        updateButton.addEventListener("click", async () => {
            try {
                const formData = getAuthFormData();
                const updated = await updateUser(formData.cedula, formData);
                fillAuthForm();
                mostrarMensaje(`Usuario ${updated.name} actualizado correctamente`, "success");
            } catch (error) {
                mostrarMensaje(error.message || "No fue posible actualizar el usuario", "error");
            }
        });
    }

    if (logoutButton) {
        logoutButton.addEventListener("click", logoutUser);
    }

    renderGlobalIndicators();
}

/*
    Inicializa la landing.
    Solo necesita refrescar indicadores y consultar productos para mostrar volumen del catalogo.
*/
function initHomePage() {
    if (getCurrentPage() !== "home") {
        return;
    }

    renderGlobalIndicators();
    loadProducts();
}

/*
    Punto de entrada principal del frontend.
    Detecta que pagina esta abierta y ejecuta solo la logica necesaria.
*/
document.addEventListener("DOMContentLoaded", () => {
    renderGlobalIndicators();
    initHomePage();
    initCatalogPage();
    initCartPage();
    initLoginPage();
});
