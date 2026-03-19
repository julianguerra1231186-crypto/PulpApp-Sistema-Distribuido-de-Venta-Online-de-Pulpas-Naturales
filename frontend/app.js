let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

const contador = document.getElementById("contador");
const lista = document.getElementById("listaCarrito");
const totalHTML = document.getElementById("total");

function guardarCarrito(){
    localStorage.setItem("carrito", JSON.stringify(carrito));
}

function actualizarCarrito(){

    if(!lista) return;

    lista.innerHTML="";

    let total=0;
    let cantidadTotal=0;

    carrito.forEach((item,index)=>{

        const li=document.createElement("li");

        li.innerHTML=`
<span>${item.nombre} - $${item.precio}</span>

<div class="cantidad">
<button onclick="restarCantidad(${index})">−</button>
<span>${item.cantidad}</span>
<button onclick="sumarCantidad(${index})">+</button>
<button onclick="eliminarProducto(${index})">✖</button>
</div>
`;

        lista.appendChild(li);

        total+=item.precio*item.cantidad;
        cantidadTotal+=item.cantidad;

    });

    contador.textContent=cantidadTotal;
    totalHTML.textContent=total;

    guardarCarrito();

}

function agregarCarrito(nombre,precio){

    let producto=carrito.find(p=>p.nombre===nombre);

    if(producto){
        producto.cantidad++;
    }else{
        carrito.push({
            nombre:nombre,
            precio:precio,
            cantidad:1
        });
    }

    actualizarCarrito();
}

function sumarCantidad(index){
    carrito[index].cantidad++;
    actualizarCarrito();
}

function restarCantidad(index){

    if(carrito[index].cantidad>1){
        carrito[index].cantidad--;
    }else{
        carrito.splice(index,1);
    }

    actualizarCarrito();

}

function eliminarProducto(index){
    carrito.splice(index,1);
    actualizarCarrito();
}

function abrirCarrito(){
    document.getElementById("panelCarrito").classList.remove("hidden");
}

function cerrarCarrito(){
    document.getElementById("panelCarrito").classList.add("hidden");
}

function enviarPedido(){

    if(carrito.length===0){
        alert("Tu carrito está vacío");
        return;
    }

    const direccion=document.getElementById("direccion").value;

    if(!direccion){
        alert("Ingrese su dirección");
        return;
    }

    let mensaje="🛒 Pedido PulpApp:%0A%0A";
    let total=0;

    carrito.forEach(p=>{

        mensaje+=`• ${p.nombre} x${p.cantidad} - $${p.precio}%0A`;

        total+=p.precio*p.cantidad;

    });

    mensaje+=`%0A💰 Total: $${total}`;
    mensaje+=`%0A📍 Dirección: ${direccion}`;

    const telefono="573103313705";

    window.open(`https://wa.me/${telefono}?text=${mensaje}`);

}

function buscarProducto(){

    const texto=document.getElementById("buscador").value.toLowerCase();

    document.querySelectorAll(".producto").forEach(producto=>{

        const nombre=producto.querySelector("h3").textContent.toLowerCase();

        producto.style.display=nombre.includes(texto)?"block":"none";

    });

}

function verImagen(src){

    document.getElementById("imagenGrande").src=src;
    document.getElementById("modalImagen").classList.remove("hidden");

}

function cerrarImagen(){
    document.getElementById("modalImagen").classList.add("hidden");
}

/* =========================
USUARIOS API
========================= */

let usuarioActual=null;

function buscarUsuario(){

    const cedula=document.getElementById("cedula").value.trim();
    const telefono=document.getElementById("telefono").value.trim();

    if(!cedula || !telefono){
        alert("Ingrese cédula y teléfono");
        return;
    }

    fetch(`http://localhost:8081/users/validar/${cedula}/${telefono}`)

        .then(res=>{

            if(!res.ok){
                throw new Error("Usuario no encontrado");
            }

            return res.json();

        })

        .then(data=>{

            usuarioActual=data;

            document.getElementById("nombre").value=data.name;
            document.getElementById("correo").value=data.email;
            document.getElementById("direccion").value=data.direccion;

        })

        .catch(()=>{

            usuarioActual=null;
            alert("Cédula o teléfono incorrectos");

        });

}

function crearUsuario(){

    const cedula=document.getElementById("cedula").value.trim();
    const telefono=document.getElementById("telefono").value.trim();
    const nombre=document.getElementById("nombre").value.trim();
    const correo=document.getElementById("correo").value.trim();
    const direccion=document.getElementById("direccion").value.trim();

    if(!cedula||!telefono||!nombre||!correo||!direccion){
        alert("Complete todos los campos");
        return;
    }

    const usuario={
        cedula:cedula,
        telefono:telefono,
        name:nombre,
        email:correo,
        password:"123456",
        direccion:direccion
    };

    fetch("http://localhost:8081/users",{

        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify(usuario)

    })

        .then(res=>res.json())

        .then(data=>{
            usuarioActual=data;
            alert("Usuario registrado correctamente");
        })

        .catch(()=>{
            alert("Error al registrar usuario");
        });

}

function modificarUsuario(){

    if(!usuarioActual){
        alert("Primero consulte un usuario");
        return;
    }

    const usuario={

        cedula:document.getElementById("cedula").value,
        telefono:document.getElementById("telefono").value,
        name:document.getElementById("nombre").value,
        email:document.getElementById("correo").value,
        password:"123456",
        direccion:document.getElementById("direccion").value

    };

    fetch(`http://localhost:8081/users/${usuarioActual.id}`,{

        method:"PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify(usuario)

    })

        .then(res=>res.json())

        .then(()=>{
            alert("Usuario actualizado");
        });

}

window.addEventListener("load",()=>{

    setTimeout(()=>{

        document.getElementById("splash").style.display="none";
        document.getElementById("app").classList.remove("hidden");

        actualizarCarrito();

    },2000);

});