# Avanze del proyecto 
Se organiza la mesa de proyecto y se definen ramas
<hr>

## Se organiza y se crea el equipo en Monday Dev:

- Gestión centralizada del proyecto PulpApp, organizando las tareas de los microservicios (ms-users, ms-products, ms-orders, ms-notifications y frontend).
- Visualización clara del flujo de trabajo, desde la creación de historias de usuario hasta su despliegue en producción.
- Organización por sprints, dividiendo el desarrollo en iteraciones enfocadas (Infraestructura, Usuarios, Productos, Pedidos).
- Trazabilidad de actividades, permitiendo relacionar cada historia de usuario con su respectiva rama en Git y sus commits en los repositorios.

![](https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales/blob/main/docs/planning/03-week/Captura%20de%20pantalla%202026-02-25%20122503.png)

<hr>

## Organizamos la estrucutura del proyecto utilizamos MVC(Modelo,Vista,Controlador)

- Controladores  
- Servicios  
- Repositorios  
- Entidades  
- DTOs  
- Configuración

<hr>

## Creamos las Ramas

# ramas principales

- main → Producción
- release → Versión candidata
- qa → Pruebas
- develop → Integración de desarrollo

# Ramas por Historia de Usuario (temporales)

Por cada HU se crea una rama nueva desde develop.

- HU-01 Registro/Login
- HU-02 Gestión Productos
- HU-03 Gestión Pedidos

<center>Recordemos que las ramas temporales se les realiza un merge a develop respetando siempre los lineamientos del docente </center>
