let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

/* ELEMENTOS */

const contador = document.getElementById("contador");
const lista = document.getElementById("listaCarrito");
const totalHTML = document.getElementById("total");

/* GUARDAR CARRITO */

function guardarCarrito(){
    localStorage.setItem("carrito", JSON.stringify(carrito));
}

/* ACTUALIZAR CARRITO */

function actualizarCarrito(){

    if(!lista) return;

    lista.innerHTML = "";

    let total = 0;
    let cantidadTotal = 0;

    carrito.forEach((item, index) => {

        let li = document.createElement("li");

        li.innerHTML = `
        <span>${item.nombre} - $${item.precio}</span>

        <div class="cantidad">

            <button onclick="restarCantidad(${index})">−</button>

            <span>${item.cantidad}</span>

            <button onclick="sumarCantidad(${index})">+</button>

            <button onclick="eliminarProducto(${index})" class="eliminar">✖</button>

        </div>
        `;

        lista.appendChild(li);

        total += item.precio * item.cantidad;
        cantidadTotal += item.cantidad;

    });

    if(contador) contador.textContent = cantidadTotal;
    if(totalHTML) totalHTML.textContent = total;

    guardarCarrito();
}

/* AGREGAR PRODUCTO */

function agregarCarrito(nombre, precio){

    let producto = carrito.find(p => p.nombre === nombre);

    if(producto){
        producto.cantidad++;
    }else{
        carrito.push({
            nombre: nombre,
            precio: precio,
            cantidad: 1
        });
    }

    animarCarrito();
    mostrarNotificacion(nombre);

    actualizarCarrito();
}

/* SUMAR CANTIDAD */

function sumarCantidad(index){
    carrito[index].cantidad++;
    actualizarCarrito();
}

/* RESTAR CANTIDAD */

function restarCantidad(index){

    if(carrito[index].cantidad > 1){
        carrito[index].cantidad--;
    }else{
        carrito.splice(index, 1);
    }

    actualizarCarrito();
}

/* ELIMINAR PRODUCTO */

function eliminarProducto(index){
    carrito.splice(index, 1);
    actualizarCarrito();
}

/* ABRIR CARRITO */

function abrirCarrito(){

    const panel = document.getElementById("panelCarrito");

    if(panel){
        panel.classList.remove("hidden");
    }
}

/* CERRAR CARRITO */

function cerrarCarrito(){

    const panel = document.getElementById("panelCarrito");

    if(panel){
        panel.classList.add("hidden");
    }
}

/* ENVIAR PEDIDO WHATSAPP */

function enviarPedido(){

    if(carrito.length === 0){
        alert("Tu carrito está vacío");
        return;
    }

    let direccionInput = document.getElementById("direccion");

    if(!direccionInput) return;

    let direccion = direccionInput.value.trim();

    if(direccion === ""){
        alert("Por favor ingresa tu dirección");
        return;
    }

    let mensaje = "🛒 Pedido PulpApp:%0A%0A";

    let total = 0;

    carrito.forEach(p => {

        mensaje += `• ${p.nombre} x${p.cantidad} - $${p.precio}%0A`;

        total += p.precio * p.cantidad;

    });

    mensaje += `%0A💰 Total: $${total}%0A`;
    mensaje += `📍 Dirección: ${direccion}`;

    const telefono = "573103313705";

    window.open(`https://wa.me/${telefono}?text=${mensaje}`);
}

/* BUSCADOR */

function buscarProducto(){

    let input = document.getElementById("buscador");

    if(!input) return;

    let texto = input.value.toLowerCase();

    let productos = document.querySelectorAll(".producto");

    productos.forEach(producto => {

        let nombre = producto.querySelector("h3").textContent.toLowerCase();

        producto.style.display = nombre.includes(texto) ? "block" : "none";

    });
}

/* MODAL IMAGEN */

function verImagen(src){

    const modal = document.getElementById("modalImagen");
    const img = document.getElementById("imagenGrande");

    if(modal && img){

        img.src = src;
        modal.classList.remove("hidden");

    }
}

function cerrarImagen(){

    const modal = document.getElementById("modalImagen");

    if(modal){
        modal.classList.add("hidden");
    }
}

/* ANIMACION CARRITO */

function animarCarrito(){

    const carritoIcon = document.querySelector(".carrito-flotante");

    if(!carritoIcon) return;

    carritoIcon.classList.add("animar");

    setTimeout(() => {
        carritoIcon.classList.remove("animar");
    }, 400);
}

/* NOTIFICACION */

function mostrarNotificacion(producto){

    const notificacion = document.getElementById("notificacion");

    if(!notificacion) return;

    notificacion.textContent = producto + " agregado al carrito";

    notificacion.classList.add("mostrar");

    setTimeout(() => {
        notificacion.classList.remove("mostrar");
    }, 2000);
}

/* CARGA INICIAL */

window.addEventListener("load", () => {

    setTimeout(() => {

        const splash = document.getElementById("splash");
        const app = document.getElementById("app");

        if(splash){
            splash.style.display = "none";
        }

        if(app){
            app.classList.remove("hidden");
        }

        actualizarCarrito();

    }, 2500);

});