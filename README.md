# 🍊 PulpApp – Sistema Distribuido de Venta Online 🍊
Plataforma escalable de gestión distribuida para la comercialización inteligente y logística de pulpas frutales naturales.
<hr>
##👥 Integrantes

- Julian Guerra
- Edwin Menendez

<hr>
##1️⃣ Descripción General:

1.  Registrarse e iniciar sesión
2.  Consultar catálogo de productos
3.  Agregar productos al carrito
4.  Realizar pedidos
5.  Seleccionar dirección de entrega
6.  Confirmar el pedido vía WhatsApp
7.  Pagar contra entrega

El sistema estará basado en una arquitectura de microservicios con base de datos MySQL.
<hr>
##📌 Arquitectura Del Backend

El sistema está dividido en microservicios independientes que se comunican mediante API REST, permitiendo escalabilidad, mantenimiento modular y separación de responsabilidades.

- 📦 [ms-users](https://github.com/julianguerra1231186-crypto/ms-users)
- 🛒 [ms-products](https://github.com/julianguerra1231186-crypto/ms-products)
- 📦 [ms-orders](https://github.com/julianguerra1231186-crypto/ms-orders)
- 📦 [ms-notifications](https://github.com/julianguerra1231186-crypto/ms-notifications)
- 🖥️ [front](https://github.com/julianguerra1231186-crypto/Frontend)

<hr>

##Descripción de los Microservicios 


1.  ms-users
Microservicio encargado de la gestión de usuarios del sistema. Permite el registro, autenticación e inicio de sesión de los clientes, así como la administración de su información personal. Se conecta a la base de datos MySQL para almacenar y validar los datos.

2.  ms-products
Microservicio responsable de la gestión del catálogo de pulpas. Administra la información de los productos, incluyendo nombre, descripción, precio, disponibilidad y stock.

3.  ms-orders
Microservicio encargado de la gestión de pedidos. Permite crear órdenes de compra, registrar la dirección de entrega, calcular el total del pedido y actualizar su estado (pendiente, gestionado, entregado).

4.  ms-notifications
Microservicio responsable de la notificación y gestión interna de los pedidos. Recibe la información de las órdenes generadas y la envía al asesor encargado para su validación y coordinación de entrega.

5. Registrarse e iniciar sesión
Aplicación web que funciona como interfaz de usuario. Permite a los clientes registrarse, iniciar sesión, consultar el catálogo de productos y realizar pedidos, comunicándose con los microservicios a través de API REST.

<hr>




