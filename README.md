<!--
CONFIG
FULL_NAME:Julian Andres Guerra Garcia
GITHUB_USER: julianguerra1231186@gmail.com
CODE_ORGANIZATION: code-corhuila
-->


# 🍊 PulpApp – Sistema Distribuido de Venta Online 🍊
Plataforma escalable de gestión distribuida para la comercialización inteligente y logística de pulpas frutales naturales.

   - [Mesa De Trabajo](https://julianguerra1231186-1773894024267.atlassian.net/?continue=https%3A%2F%2Fjulianguerra1231186-1773894024267.atlassian.net%2Fwelcome%2Fsoftware%3FprojectId%3D10000&atlOrigin=eyJpIjoiOTdhMWY4ZGU5N2YwNDQ0MDk3NTZjODkxYTU5ZWVlZWQiLCJwIjoiamlyYS1zb2Z0d2FyZSJ9)
     
<hr>

# Integrantes
- Julian Guerra
- Edwin Menendez
- -Grupo 5 
<hr>

## 📌 Arquitectura Del Backend

El sistema está dividido en microservicios independientes que se comunican mediante API REST, permitiendo escalabilidad, mantenimiento modular y separación de responsabilidades.

-  [ms-users](https://github.com/julianguerra1231186-crypto/ms-users)
-  [ms-products](https://github.com/julianguerra1231186-crypto/ms-products)
-  [ms-orders](https://github.com/julianguerra1231186-crypto/ms-orders)
-  [front](https://github.com/julianguerra1231186-crypto/Frontend)
-  [BaseDeDatos](https://github.com/julianguerra1231186-crypto/BaseDeDatos)
-  [Api-Gatawey](https://github.com/julianguerra1231186-crypto/Api-Gatawey)

Cada microservicio se ejecuta de forma independiente en su propio contenedor Docker y expone su API en un puerto específico (8081, 8082 y 8083 respectivamente). Esto permite que cada servicio funcione de manera desacoplada, facilitando su desarrollo, despliegue y mantenimiento.
La comunicación entre microservicios se realiza mediante HTTP utilizando los nombres de servicio dentro de la red Docker, lo que permite una interacción correcta sin depender de localhost, garantizando una arquitectura distribuida real.
### Se deja evidencia de todas las Historias de Usuario en la mesa de trabajo:
   - [Mesa De Trabajo](https://julianguerra1231186-1773894024267.atlassian.net/?continue=https%3A%2F%2Fjulianguerra1231186-1773894024267.atlassian.net%2Fwelcome%2Fsoftware%3FprojectId%3D10000&atlOrigin=eyJpIjoiOTdhMWY4ZGU5N2YwNDQ0MDk3NTZjODkxYTU5ZWVlZWQiLCJwIjoiamlyYS1zb2Z0d2FyZSJ9)

Cada microservicio implementa internamente el patrón arquitectónico MVC (Modelo – Vista – Controlador), permitiendo la separación entre la lógica de negocio, el acceso a datos y la gestión de solicitudes HTTP. 
Esta estructura mejora la mantenibilidad, escalabilidad y organización del sistema distribuido.

Adicionalmente, el sistema utiliza PostgreSQL como base de datos y Docker Compose para la orquestación de los servicios, permitiendo levantar toda la arquitectura de manera controlada y reproducible.

# El sistema se encuentra completamente funcional:
- Backend distribuido.
- API Gateway.
- Frontend conectado.
- Base de datos persistente.
- Docker Compose.

  ████████████████████████████████████ 100%
- Kubernetes: Pendiente , En Revisicion para implementacion
  
<hr>

# ADR

🖥️ [ADR](https://github.com/julianguerra1231186-crypto/ADR/blob/main/1-ADR.md) 

### se deja evidencia de todas las Historias de usuarios en la mesa de trabajo Yira :  

   - [Mesa De Trabajo](https://julianguerra1231186-1773894024267.atlassian.net/jira/software/projects/KAN/list?jql=project+%3D+KAN+ORDER+BY+created+DESC&atlOrigin=eyJpIjoiMWEwMjhmNmU1OWJhNDhjNGJmZDViYzA3OTM3MzRkNjAiLCJwIjoiaiJ9)

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





