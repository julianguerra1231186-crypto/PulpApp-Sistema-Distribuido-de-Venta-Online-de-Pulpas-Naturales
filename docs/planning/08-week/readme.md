# 🧃 PulpApp - Sistema Distribuido de Venta Online de Pulpas Naturales

## 📌 Descripción del proyecto

PulpApp es una aplicación distribuida orientada a la comercialización de pulpas naturales, diseñada bajo una arquitectura de microservicios. El sistema permite gestionar productos, usuarios y pedidos de manera desacoplada, garantizando escalabilidad, mantenibilidad y coherencia entre frontend y backend.

El proyecto implementa un flujo completo de e-commerce, integrando múltiples servicios independientes que se comunican mediante APIs REST.

---

## 🏗️ Arquitectura del sistema

El sistema está compuesto por los siguientes componentes:

* **Frontend:** Aplicación web dinámica (HTML, CSS, JS)
* **ms-users:** Gestión de usuarios y pedidos
* **ms-products:** Gestión de catálogo de productos
* **Base de datos:** PostgreSQL
* **Orquestación:** Docker Compose

---

## 🚀 Ejecución del proyecto

### 🔧 Requisitos

* Docker
* Docker Compose
* Git

### ▶️ Pasos para ejecutar

```bash
git clone <URL_DEL_REPOSITORIO>
cd PulpApp
docker-compose up --build
```

Luego abrir en el navegador:

```
http://localhost:3000 (o el puerto configurado)
```

---

## 📌 Planificación del proyecto

Toda la planificación, backlog y evolución técnica del sistema se basa en historias de usuario priorizadas.

---

## 📋 Backlog priorizado

### 🔴 Alta prioridad

* HU1 Consultar catálogo desde API
* HU4 Registrar pedidos reales
* HU5 Validar datos en frontend y backend
* HU7 Contratos consistentes frontend/backend
* HU9 Servicios distribuidos realmente operativos

---

### 🟡 Media prioridad

* HU2 Crear productos
* HU3 Actualizar productos
* HU6 Consultar pedidos registrados

---

### ⚪ Baja prioridad

* HU8 Mejoras de seguridad progresivas

---

## 🔗 Relación historia → mejora técnica

* **HU1** → Implementación de `ms-products` con endpoint `GET /products` y conexión al frontend
* **HU2** → Implementación de `POST /products`
* **HU3** → Implementación de `PUT /products/{id}` y `DELETE`
* **HU4** → Integración del flujo de pedidos con persistencia real (`PedidoController`)
* **HU5** → Validaciones en frontend y backend
* **HU6** → Endpoints de consulta de pedidos (`GET /pedidos`)
* **HU7** → Manejo consistente de errores y contratos API
* **HU8** → Mejora progresiva de seguridad
* **HU9** → Activación real del sistema distribuido

---

## 🧩 Fase 1: Mejora del Backend (ms-products)

Se implementa un microservicio completo para gestionar productos, resolviendo el problema de datos estáticos en frontend.

### ✔ Cambios realizados

* Configuración de base de datos y puerto independiente
* Creación de entidad `Product`
* Implementación de DTOs con validaciones
* Creación de repositorio JPA
* Lógica de negocio en servicio
* Controlador REST con endpoints CRUD
* Configuración de CORS
* Manejo global de excepciones

---

## 🔄 Fase 2: Integración Frontend - Backend

Se reemplaza el catálogo estático por render dinámico consumiendo la API.

### ✔ Cambios realizados

* Eliminación de datos quemados en HTML
* Render dinámico desde `app.js`
* Consumo de API `ms-products`
* Mantenimiento de funcionalidades existentes (búsqueda, carrito, modal)
* Implementación de fallback en caso de error

---

## 🛒 Fase 3: Flujo real de pedidos

Se reemplaza el flujo informal de WhatsApp por persistencia real en backend.

### ✔ Cambios realizados

* Validación de usuario autenticado
* Construcción dinámica del pedido desde el carrito
* Consumo de `POST /pedidos`
* Limpieza automática del carrito
* WhatsApp como opción secundaria

---

## 🧪 Fase 4: Mejora de calidad y estabilidad

Se fortalecen validaciones y manejo de errores.

### ✔ Cambios realizados

* Validación de respuestas HTTP (`res.ok`)
* Manejo seguro de errores JSON
* Validaciones en frontend
* Uso de `@Valid` en backend
* Estandarización de respuestas de error
* Mejora en mensajes al usuario

---

## 🧠 Buenas prácticas aplicadas

* Arquitectura de microservicios
* Separación de responsabilidades
* Uso de DTOs
* Manejo global de excepciones
* Persistencia con JPA/Hibernate
* Integración frontend-backend desacoplada
* Control de versiones con Git (commits estructurados + Pull Requests)

---

## 🔀 Flujo de trabajo con Git

El desarrollo se realizó utilizando un flujo basado en ramas:

* `feature/*` → desarrollo de funcionalidades
* `develop-actividad` → integración
* `main` → versión estable

Se utilizaron Pull Requests para integración controlada de cambios.

---

## 🎯 Conclusión

PulpApp no solo implementa funcionalidades básicas de un e-commerce, sino que demuestra:

* Diseño de sistemas distribuidos
* Integración real entre servicios
* Flujo profesional de desarrollo
* Aplicación de buenas prácticas de ingeniería de software

---

## 👨‍💻 Autor

Julian Guerra
Proyecto académico – Sistemas Distribuidos

