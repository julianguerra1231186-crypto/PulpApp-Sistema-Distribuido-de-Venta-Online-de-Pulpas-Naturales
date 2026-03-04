const API_URL = "http://localhost:8081/users";

let carrito = [];

fetch(API_URL)
    .then(res => res.json())
    .then(data => {

        const contenedor = document.getElementById("productos");

        data.forEach(item => {

            const div = document.createElement("div");

            div.innerHTML = `
        <p>${item.name}</p>
        <button onclick="agregarCarrito('${item.name}')">
        Agregar
        </button>
      `;

            contenedor.appendChild(div);

        });

    });

function agregarCarrito(nombre){

    carrito.push(nombre);

    const lista = document.getElementById("carrito");

    const item = document.createElement("li");
    item.textContent = nombre;

    lista.appendChild(item);

}

function enviarPedido(){

    let mensaje = "Pedido PulpApp:%0A";

    carrito.forEach(p=>{
        mensaje += "- " + p + "%0A";
    });

    const telefono = "+573103313705";

    window.open(`https://wa.me/${telefono}?text=${mensaje}`);

}