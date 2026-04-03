<!--
CONFIG
FULL_NAME:Julian Andres Guerra Garcia
GITHUB_USER: julianguerra1231186@gmail.com
CODE_ORGANIZATION: code-corhuila
-->


# 🍊 PulpApp – Sistema Distribuido de Venta Online 🍊
Plataforma escalable de gestión distribuida para la comercialización inteligente y logística de pulpas frutales naturales.

#mesa de trabajo : https://corhuila517226.monday.com/boards/18401580258
<hr>
## 👥 Integrantes

- Julian Guerra
- Edwin Menendez
- -Grupo 5 

<hr>
## 📌 Arquitectura Del Backend

El sistema está dividido en microservicios independientes que se comunican mediante API REST, permitiendo escalabilidad, mantenimiento modular y separación de responsabilidades.

- 📦 [ms-users](https://github.com/julianguerra1231186-crypto/ms-users)
- 🛒 [ms-Api](https://github.com/julianguerra1231186-crypto/ms-products)
- 🖥️ [front](https://github.com/julianguerra1231186-crypto/Frontend)
### se deja evidencia de todas las Historias de usuarios en la mesa de trabajo : https://corhuila517226.monday.com/boards/18401580258
Cada microservicio implementará internamente el patrón arquitectónico MVC (Modelo – Vista – Controlador), permitiendo la separación entre la lógica de negocio, la capa de acceso a datos y la gestión de solicitudes HTTP. Esta estructura mejora la mantenibilidad y escalabilidad del sistema distribuido.
<hr>

# ADR

🖥️ [ADR](https://github.com/julianguerra1231186-crypto/ADR/blob/main/1-ADR.md) 

### se deja evidencia de todas las Historias de usuarios en la mesa de trabajo Yira :  

https://julianguerra1231186-1773894024267.atlassian.net/jira/software/projects/KAN/list?jql=project+%3D+KAN+ORDER+BY+created+DESC&atlOrigin=eyJpIjoiMWEwMjhmNmU1OWJhNDhjNGJmZDViYzA3OTM3MzRkNjAiLCJwIjoiaiJ9

<hr>

# Actividad: Desarrollo de Carrito de Compras (MVP)

 - Como parte del proceso de construcción del sistema, se desarrolló un MVP (Minimum Viable Product) de un carrito de compras, con el objetivo de validar la lógica base del manejo de productos dentro de una posible solución de comercio electrónico.

### El desarrollo se llevó a cabo utilizando un enfoque práctico basado en:

 - Implementación de historias de usuario
 - Uso de ramas por funcionalidad (feature branches)
 - Integración progresiva del código

### Durante esta actividad se implementaron funcionalidades clave como:

 - Gestión de productos dentro del carrito
 - Visualización de los elementos seleccionados
 - Cálculo del total de la compra
 - Cada funcionalidad fue desarrollada de forma independiente, permitiendo un mejor control del código, organización del proyecto y simulación de un entorno real de desarrollo.

Este MVP representa una base sólida para futuras integraciones con otros componentes del sistema, como usuarios, productos y autenticación.

### Acceso al repositorio

Para ver la implementación completa, las historias de usuario y la estructura de ramas utilizadas:

 - Dejo todo debidamente evidenciado con capturas de pantalla : https://github.com/julianguerra1231186-crypto/carrito-compras-mvp/tree/main/docs

 - https://github.com/julianguerra1231186-crypto/carrito-compras-mvp

# Finalización Exitosa del Curso de GitHub

Nos complace informar que se ha completado satisfactoriamente el curso **"Introduction to GitHub"**, fortaleciendo conocimientos clave en control de versiones y trabajo colaborativo en entornos de desarrollo.

## Objetivo del Curso
El curso tuvo como propósito introducir y consolidar conceptos fundamentales de GitHub, permitiendo:
- Gestionar repositorios de manera eficiente  
- Trabajar con control de versiones usando Git  
- Colaborar en proyectos mediante pull requests  
- Aplicar buenas prácticas en desarrollo colaborativo  

## Contenido Desarrollado
Durante el proceso se abordaron los siguientes temas:
- Creación y administración de repositorios  
- Uso de commits y control de cambios  
- Manejo de ramas (branches)  
- Pull requests y revisión de código  
- Resolución de conflictos  

## Resultado
El curso fue finalizado con éxito, logrando:
- Mayor dominio de herramientas de desarrollo colaborativo  
- Fortalecimiento de habilidades técnicas en GitHub  
- Aplicación práctica en proyectos reales  

## Evidencia del Curso
Puedes consultar el repositorio trabajado en el siguiente enlace:

https://github.com/julianguerra1231186-crypto/skills-introduction-to-github

<hr>

## Descripción de los Microservicios 


1.  ms-users:
Microservicio encargado de la gestión de usuarios del sistema. Permite el registro, autenticación e inicio de sesión de los clientes, así como la administración de su información personal. Se conecta a la base de datos MySQL para almacenar y validar los datos.

2.  ms-products:
Microservicio responsable de la gestión del catálogo de pulpas. Administra la información de los productos, incluyendo nombre, descripción, precio, disponibilidad y stock.

3.  ms-orders:
Microservicio encargado de la gestión de pedidos. Permite crear órdenes de compra, registrar la dirección de entrega, calcular el total del pedido y actualizar su estado (pendiente, gestionado, entregado).

4.  ms-notifications:
Microservicio responsable de la notificación y gestión interna de los pedidos. Recibe la información de las órdenes generadas y la envía al asesor encargado para su validación y coordinación de entrega.

5. Frontend:
Registrarse e iniciar sesión Aplicación web que funciona como interfaz de usuario. Permite a los clientes registrarse, iniciar sesión, consultar el catálogo de productos y realizar pedidos, comunicándose con los microservicios a través de API REST.

![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Diagrama%20de%20microservicios.png)
<hr>

## Como Haremos Nuestro Backend:

* **Lenguaje:** Java 17
* **Framework Backend:** Spring Boot 3.x
* **Herramienta de Construcción:** Maven
* **IDE:** IntelliJ IDEA
* **Frontend:** Angular

1.  Backend:
Se implementarán microservicios orientados a la creación de APIs REST, permitiendo la comunicación entre los diferentes componentes del sistema de manera independiente.

2.  Base de Datos:
Se utilizará MySQL como sistema de gestión de base de datos relacional para almacenar la información de usuarios, productos y pedidos.

3.  Frontend:
Se desarrollará una aplicación web como interfaz de usuario, la cual consumirá los servicios REST del backend.

4.  Control de Versiones:
Se empleará Git como sistema de control de versiones distribuido, siguiendo una estrategia de ramas estructurada (develop, qa, release y main) para garantizar un desarrollo organizado.

5.  Contenedores:
Se implementará Docker para la contenedorización de los microservicios, facilitando su despliegue y escalabilidad.

![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/diagrama%20de%20tecnologias.png)

<hr>

## Dependencias que utlizaremos

🔹 Spring Boot Starter Web
Permite la creación de APIs REST para la comunicación entre los microservicios del sistema.

🔹 Spring Boot Starter Data JPA
Facilita la persistencia de datos y la interacción con la base de datos MySQL mediante el uso de entidades y repositorios.

🔹 MySQL Connector
Permite la conexión entre los microservicios y la base de datos relacional MySQL.

🔹 Spring Boot Starter Security
Se utilizará para implementar mecanismos de seguridad y control de acceso a los endpoints del sistema.

🔹 JWT (JSON Web Token)
Se empleará para la autenticación y autorización de usuarios dentro del entorno distribuido.

🔹 Spring Boot Starter Validation
Permitirá validar los datos enviados por los usuarios (correo electrónico, campos obligatorios, formatos, etc.).

🔹 Lombok
Reducirá código repetitivo, facilitando la escritura y mantenimiento del proyecto.

🔹 Docker
Se utilizará para la contenedorización de los microservicios, permitiendo su despliegue independiente y escalabilidad.

🔹 Git
Sistema de control de versiones distribuido que permitirá la gestión colaborativa del código bajo una estrategia estructurada de ramas.

![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/04-week/Diagramade%20dependencias.png)





