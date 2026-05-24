<!--
CONFIG
FULL_NAME:Julian Andres Guerra Garcia
GITHUB_USER: julianguerra1231186@gmail.com
CODE_ORGANIZATION: code-corhuila
-->

[![Estado](https://img.shields.io/badge/Estado-Funcional%20100%25-brightgreen?style=for-the-badge)](https://github.com)
[![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)](https://www.java.com)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)](https://www.docker.com)


# 🍊 PulpApp – Sistema Distribuido de Venta Online 🍊

PulpApp es una plataforma de comercio electrónico construida con arquitectura de **microservicios**, diseñada para la venta online de pulpas de fruta naturales. El sistema permite a los clientes explorar el catálogo, registrarse, iniciar sesión con autenticación JWT, agregar productos al carrito y realizar pedidos reales que se persisten en base de datos.

El sistema también incluye un módulo de reclutamiento para gestionar postulaciones laborales, y un panel de administración con control de acceso basado en roles (RBAC).

   - [Mesa De Trabajo](https://julianguerra1231186-1773894024267.atlassian.net/?continue=https%3A%2F%2Fjulianguerra1231186-1773894024267.atlassian.net%2Fwelcome%2Fsoftware%3FprojectId%3D10000&atlOrigin=eyJpIjoiOTdhMWY4ZGU5N2YwNDQ0MDk3NTZjODkxYTU5ZWVlZWQiLCJwIjoiamlyYS1zb2Z0d2FyZSJ9)
     
<hr>

# Integrantes
- Julian Guerra
- Edwin Menendez
- -Grupo 5 
<hr>


## 📚 ADR — Registro de Decisiones de Arquitectura

Las decisiones técnicas clave del proyecto están documentadas en el ADR:

🖥️ [Ver ADR](https://github.com/julianguerra1231186-crypto/ADR/blob/main/1-ADR.md)

---

## 🛒 MVP — Carrito de Compras

Como parte del proceso de construcción del sistema, se desarrolló un MVP del carrito de compras para validar la lógica base antes de integrar con los microservicios.

### Funcionalidades implementadas en el MVP

- Gestión de productos dentro del carrito
- Visualización de elementos seleccionados
- Cálculo del total de la compra
- Desarrollo por feature branches con integración progresiva

### Repositorio del MVP

- 📸 [Evidencias con capturas de pantalla](https://github.com/julianguerra1231186-crypto/carrito-compras-mvp/tree/main/docs)
- 💻 [Repositorio completo](https://github.com/julianguerra1231186-crypto/carrito-compras-mvp)

---

## 🎓 Curso Introduction to GitHub

Se completó satisfactoriamente el curso **"Introduction to GitHub"**, fortaleciendo conocimientos en control de versiones y trabajo colaborativo.

### Temas abordados

- Creación y administración de repositorios
- Commits y control de cambios
- Manejo de ramas (branches)
- Pull requests y revisión de código
- Resolución de conflictos

### Evidencia

💻 [Ver repositorio del curso](https://github.com/julianguerra1231186-crypto/skills-introduction-to-github)

---

<div align="center">

**PulpApp** — Desarrollado con ❤️ por Julian Guerra y Edwin Menéndez · Grupo 5

</div>

# Arquitectura Del Backend
```
                        ┌─────────────────────────────────┐
                        │         FRONTEND                │
                        │   HTML + CSS + JavaScript       │
                        └──────────────┬──────────────────┘
                                       │ HTTP
                                       ▼
                        ┌─────────────────────────────────┐
                        │         API GATEWAY             │
                        │   Spring Cloud Gateway          │
                        │   Puerto: 8090                  │
                        └──────┬──────────┬───────┬───────┘
                               │          │       │
                    /auth/**   │  /prod** │       │ /orders/**
                    /users/**  │          │       │
                    /job-app** │          │       │
                               ▼          ▼       ▼
              ┌────────────────┐  ┌───────────┐  ┌───────────────┐
              │   ms-users     │  │ms-products│  │   ms-orders   │
              │   Puerto 8081  │  │Puerto 8082│  │   Puerto 8083 │
              │                │  │           │  │               │
              │ - Auth JWT     │  │ - Catálogo│  │ - Pedidos     │
              │ - Usuarios     │  │ - Categorías  │ - Items       │
              │ - Postulaciones│  │           │  │               │
              └───────┬────────┘  └─────┬─────┘  └───────┬───────┘
                      │                 │                 │
                      └─────────────────┴─────────────────┘
                                        │
                                        ▼
                        ┌─────────────────────────────────┐
                        │         PostgreSQL 15           │
                        │         pulpapp_db              │
                        │         Puerto: 5434            │
                        └─────────────────────────────────┘
```

Cada microservicio se ejecuta en su propio contenedor Docker, se comunica mediante HTTP usando los nombres de servicio de la red interna de Docker (`ms-users:8081`, `ms-products:8082`, `ms-orders:8083`), y gestiona su propio esquema de base de datos con Liquibase.

---
# Microservicios

| Servicio | Puerto | Responsabilidad | Repositorio |
|---------|--------|-----------------|-------------|
| ms-users | 8081 | Autenticación JWT, usuarios, postulaciones | [🔗 Ver](https://github.com/julianguerra1231186-crypto/ms-users) |
| ms-products | 8082 | Catálogo de productos y categorías | [🔗 Ver](https://github.com/julianguerra1231186-crypto/ms-products) |
| ms-orders | 8083 | Gestión de pedidos con precio histórico | [🔗 Ver](https://github.com/julianguerra1231186-crypto/ms-orders) |
| api-gateway | 8090 | Enrutamiento centralizado y CORS | [🔗 Ver](https://github.com/julianguerra1231186-crypto/Api-Gatawey) |
| frontend | — | Interfaz de usuario HTML/CSS/JS | [🔗 Ver](https://github.com/julianguerra1231186-crypto/Frontend) |
| base de datos | 5434 | PostgreSQL + pgAdmin | [🔗 Ver](https://github.com/julianguerra1231186-crypto/BaseDeDatos) |

# Seguridad — JWT y Control de Roles (RBAC)

El sistema implementa autenticación y autorización basada en **JSON Web Tokens (JWT)** con dos roles diferenciados:

| Rol | Permisos |
|-----|----------|
| `ROLE_ADMIN` | CRUD de productos, gestión de usuarios, ver todos los pedidos, gestionar postulaciones |
| `ROLE_SELLER` | Consultar productos, crear pedidos, ver sus propios pedidos |

# Flujo de autenticación

```
1. POST /auth/register o /auth/login
2. Backend valida credenciales con BCrypt
3. Genera token JWT firmado con HMAC-SHA256
4. Token incluye: {role, sub (email), iat, exp}
5. Frontend guarda token en localStorage
6. Cada request protegido envía: Authorization: Bearer <token>
7. JwtAuthFilter valida firma y expiración en cada microservicio
```

---

# Base de Datos

**PostgreSQL 15** — una sola instancia compartida, con tablas independientes por microservicio.

| Microservicio | Tablas |
|---------------|--------|
| ms-users | `users`, `pedidos`, `job_applications` |
| ms-products | `category`, `products` |
| ms-orders | `orders`, `order_items` |

El esquema es gestionado por **Liquibase** — no se ejecutan scripts SQL manualmente. Al arrancar, cada microservicio aplica automáticamente sus changesets pendientes.

---
## 🖥️ Frontend

Aplicación web en **HTML5, CSS3 y JavaScript vanilla** (sin frameworks). Se comunica con el backend exclusivamente a través del API Gateway.

| Página | Descripción |
|--------|-------------|
| `index.html` | Landing con slider, carrusel de productos y sección institucional |
| `catalog.html` | Catálogo dinámico conectado a ms-products |
| `cart.html` | Carrito persistente y checkout con autocompletado |
| `login.html` | Inicio de sesión con JWT |
| `register.html` | Registro de nuevos usuarios |
| `dashboard.html` | Panel por rol (SELLER / ADMIN) |
| `trabaja-con-nosotros.html` | Reclutamiento con formulario y CV adjunto |

---

## 📦 Módulo de Reclutamiento

El sistema incluye un módulo completo de gestión de postulaciones laborales:

- Formulario público en `trabaja-con-nosotros.html`
- Adjunto de hoja de vida (PDF/DOC) con almacenamiento persistente en volumen Docker
- Panel ADMIN en el dashboard para ver candidatos, filtrar y descargar CVs
- Endpoint `GET /job-applications/{id}/download` con autenticación JWT

---
## 🚀 Levantar el Sistema

### Requisitos previos

- Docker Desktop instalado y corriendo
- Puerto 8090, 8081, 8082, 8083, 5434 y 5050 disponibles

### Comando único

```bash
docker-compose up --build
```

### Verificar que todo está corriendo

```bash
docker-compose ps
```

### Servicios disponibles

| Servicio | URL |
|---------|-----|
| Frontend | Abrir `frontend/index.html` en el navegador |
| API Gateway | http://localhost:8090 |
| ms-users | http://localhost:8081 |
| ms-products | http://localhost:8082 |
| ms-orders | http://localhost:8083 |
| pgAdmin | http://localhost:5050 |

### Credenciales pgAdmin

```
Email:      admin@admin.com
Contraseña: admin123
```

### Crear usuario administrador (Postman)

```json
POST http://localhost:8090/auth/register
{
  "cedula": "111222333",
  "telefono": "3001234567",
  "name": "Admin Principal",
  "email": "admin@pulpapp.com",
  "password": "admin123",
  "direccion": "Calle 1 # 2-3",
  "role": "ROLE_ADMIN"
}
```

---

## 🛠️ Stack Tecnológico

### Backend

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| Java | 17 | Lenguaje base |
| Spring Boot | 4.0.3 | Framework de microservicios |
| Spring Security | Incluido | Autenticación y autorización |
| Spring Cloud Gateway | 2023.0.1 | API Gateway reactivo |
| Spring Data JPA | Incluido | Persistencia con Hibernate |
| JJWT | 0.12.6 | Generación y validación de JWT |
| Liquibase | Incluido | Versionado del esquema de BD |
| Lombok | 1.18.38 | Reducción de boilerplate |
| MapStruct | 1.5.5.Final | Mapeo entre entidades y DTOs |
| BCrypt | Incluido | Encriptación de contraseñas |

### Base de datos e infraestructura

| Tecnología | Versión | Uso |
|-----------|---------|-----|
| PostgreSQL | 15 | Base de datos relacional |
| pgAdmin | 4 | Administración visual de BD |
| Docker | — | Contenedorización |
| Docker Compose | — | Orquestación de servicios |

### Frontend

| Tecnología | Uso |
|-----------|-----|
| HTML5 | Estructura de páginas |
| CSS3 | Estilos y diseño responsivo |
| JavaScript ES6+ | Lógica de la aplicación |
| Fetch API | Comunicación con el backend |
| localStorage | Persistencia de sesión y carrito |
| Canvas API | Efecto confeti en botón CTA |

---

## 📐 Patrones y Principios Aplicados

| Patrón | Descripción |
|--------|-------------|
| **Microservicios** | Cada dominio es un servicio independiente con su propia BD |
| **API Gateway** | Punto de entrada único para todo el tráfico |
| **MVC** | Separación en Controller, Service y Repository en cada microservicio |
| **DTO Pattern** | Objetos de transferencia separados de las entidades de dominio |
| **Repository Pattern** | Abstracción del acceso a datos con Spring Data JPA |
| **RBAC** | Control de acceso basado en roles (ROLE_ADMIN, ROLE_SELLER) |
| **Stateless Auth** | Autenticación sin sesión usando JWT |
| **Passthrough JWT** | El gateway reenvía el token sin validarlo — cada servicio lo valida |
| **Precio histórico** | ms-orders captura el precio al momento de la compra |
| **Idempotencia** | Changesets de Liquibase con `onFail: MARK_RAN` |

---


## � Propuesta de Valor Comercial

> *PulpApp no es solo un proyecto académico — es una solución lista para ser adoptada por empresas del sector agroindustrial y de alimentos naturales que quieran digitalizar su operación de ventas.*

### ¿A quién va dirigido?

| Segmento | Descripción |
|----------|-------------|
| 🏭 Productores de pulpas | Empresas que procesan y distribuyen pulpas de fruta a nivel local o regional |
| 🛒 Tiendas naturistas | Negocios que venden productos saludables y necesitan una vitrina digital |
| 🍽️ Restaurantes y hoteles | Establecimientos que compran pulpas en volumen para preparación de bebidas |
| 🏪 Distribuidores mayoristas | Intermediarios que gestionan pedidos de múltiples clientes |

### ¿Qué problema resuelve?

Muchas empresas del sector de alimentos naturales en Colombia y Latinoamérica aún gestionan sus ventas por WhatsApp, llamadas telefónicas o planillas de Excel. Esto genera:

- Pérdida de pedidos por falta de trazabilidad
- Errores en precios y stock
- Imposibilidad de escalar sin contratar más personal
- Cero visibilidad del historial de compras por cliente

**PulpApp digitaliza y automatiza todo ese proceso.**

### ¿Qué ofrece el sistema?

| Funcionalidad | Beneficio comercial |
|---------------|---------------------|
| Catálogo digital en línea | Los clientes compran 24/7 sin necesidad de un vendedor |
| Carrito persistente | El cliente no pierde su selección al navegar |
| Pedidos con precio histórico | Trazabilidad total — siempre se sabe qué se cobró y cuándo |
| Panel de administración | El dueño gestiona productos, precios y stock desde cualquier lugar |
| Control de roles | Vendedores y administradores con permisos diferenciados |
| Módulo de reclutamiento | Gestión de talento humano integrada en la misma plataforma |
| Autenticación segura JWT | Protección profesional de datos de clientes y operaciones |

### ¿Por qué elegir PulpApp?

- **Arquitectura escalable** — crece con el negocio sin reescribir el sistema
- **Tecnología moderna** — Spring Boot, Docker, JWT, PostgreSQL — stack empresarial real
- **Despliegue sencillo** — un solo comando levanta todo el sistema
- **Código organizado** — fácil de mantener, extender y auditar
- **Seguridad profesional** — autenticación JWT con roles, contraseñas encriptadas con BCrypt

### Posibilidades de expansión

- 📱 Aplicación móvil (React Native o Flutter) consumiendo la misma API
- 📊 Dashboard de analítica de ventas con reportes por período
- 💳 Integración con pasarelas de pago (PSE, Nequi, Wompi)
- 📧 Notificaciones por email al confirmar pedidos
- 🚚 Módulo de logística y seguimiento de entregas
- ☁️ Despliegue en la nube (AWS, GCP, Azure) con Kubernetes

---

## 🔄 Estado del Sistema

```
████████████████████████████████████ 100%
```

| Componente | Estado |
|-----------|--------|
| Backend distribuido (3 microservicios) | ✅ Funcional |# 🏢 Zentrix — Plataforma SaaS Multi-Tenant

> Desarrollado por **Red Matrix Solutions**

Zentrix es una plataforma SaaS multi-tenant basada en microservicios, diseñada para la gestión integral de negocios inmobiliarios, restaurantes y comercios. Permite a múltiples empresas (tenants) operar de forma aislada sobre una misma infraestructura.

---

## 📐 Arquitectura

```
┌─────────────┐     ┌──────────────┐     ┌─────────────────────────────────┐
│   Frontend  │────▶│  API Gateway │────▶│  Microservicios                 │
│   (Nginx)   │     │  (Spring     │     │  ┌───────────┐ ┌─────────────┐  │
│   :3000     │     │   Cloud GW)  │     │  │ ms-users  │ │ ms-products │  │
└─────────────┘     │   :8080      │     │  │ :8081     │ │ :8082       │  │
                    └──────────────┘     │  └───────────┘ └─────────────┘  │
                                         │  ┌─────────────┐                │
                                         │  │ ms-orders   │                │
                                         │  │ :8083       │                │
                                         │  └─────────────┘                │
                                         └─────────────────────────────────┘
                                                        │
                                                        ▼
                                              ┌──────────────────┐
                                              │   PostgreSQL 15   │
                                              │   (Cloud SQL /    │
                                              │    Local Docker)  │
                                              └──────────────────┘
```

### Microservicios

| Servicio | Puerto | Responsabilidad |
|----------|--------|-----------------|
| `api-gateway` | 8080 | Enrutamiento, CORS, punto de entrada único |
| `ms-users` | 8081 | Autenticación JWT, usuarios, tenants, RBAC, ERP (clientes, inventario, facturación) |
| `ms-products` | 8082 | Catálogo de productos multi-tenant |
| `ms-orders` | 8083 | Gestión de pedidos y pagos |
| `frontend` | 80 (3000 en host) | SPA servida por Nginx con proxy al gateway |

### Tecnologías

- **Backend**: Spring Boot 4.0.3, Spring Cloud Gateway 2023.0.1
- **Base de datos**: PostgreSQL 15 + Liquibase (migraciones versionadas)
- **Seguridad**: JWT (HMAC-SHA256), Spring Security, RBAC Multi-Tenant
- **Contenedores**: Docker, Docker Compose
- **Cloud**: Google Cloud Run, Google Cloud SQL
- **Frontend**: HTML5, CSS3, JavaScript vanilla, Nginx

---

## 🚀 Inicio Rápido

### Requisitos

- Docker y Docker Compose
- Git

### Desarrollo Local (Docker Compose)

```bash
# 1. Clonar el repositorio
git clone https://github.com/julianguerra1231186-crypto/PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales.git
cd PulpApp-Sistema-Distribuido-de-Venta-Online-de-Pulpas-Naturales

# 2. Copiar variables de entorno
cp .env.example .env

# 3. Construir y levantar todos los servicios
docker compose up --build

# 4. Acceder
# Frontend:    http://localhost:3000
# API Gateway: http://localhost:8090
# pgAdmin:     http://localhost:5050
```

### Desarrollo Local (Sin Docker)

```bash
# Requisitos: Java 17, Maven, PostgreSQL local en puerto 5434

# Configurar variables de entorno:
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5434/pulpapp_db
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=1234

# Iniciar cada servicio:
cd ms-users && ./mvnw spring-boot:run
cd ms-products && ./mvnw spring-boot:run
cd ms-orders && ./mvnw spring-boot:run
cd api-gateway && ./mvnw spring-boot:run
```

---

## ⚙️ Variables de Entorno

| Variable | Descripción | Valor Local | Valor Cloud |
|----------|-------------|-------------|-------------|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://postgres:5432/pulpapp_db` | `jdbc:postgresql://CLOUD_SQL_IP:5432/zentrix` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de BD | `postgres` | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de BD | `1234` | `[secreto]` |
| `PORT` | Puerto del servicio (Cloud Run) | Definido por docker-compose | Inyectado por Cloud Run |
| `JWT_SECRET` | Clave HMAC para firmar tokens | Base64 por defecto | `[secreto en Secret Manager]` |
| `JWT_EXPIRATION_MS` | Expiración del token (ms) | `86400000` (24h) | `86400000` |
| `TENANT_DEFAULT_ID` | ID del tenant por defecto | `1` | `1` |
| `GATEWAY_MS_USERS_URI` | URL interna de ms-users | `http://ms-users:8081` | URL de Cloud Run |
| `GATEWAY_MS_PRODUCTS_URI` | URL interna de ms-products | `http://ms-products:8082` | URL de Cloud Run |
| `GATEWAY_MS_ORDERS_URI` | URL interna de ms-orders | `http://ms-orders:8083` | URL de Cloud Run |

---

## 🐳 Docker

### Estructura de Imágenes

Todos los microservicios usan multi-stage build optimizado:
- **Builder**: `maven:3.9.9-eclipse-temurin-17` (compilación)
- **Runtime**: `eclipse-temurin:17-jre-alpine` (~150MB vs ~400MB con JDK)

### Comandos Útiles

```bash
# Construir todo
docker compose build

# Levantar en background
docker compose up -d

# Ver logs de un servicio
docker compose logs -f ms-users

# Reconstruir un servicio específico
docker compose up -d --build ms-users

# Detener y eliminar volúmenes
docker compose down -v
```

---

## ☁️ Despliegue en Google Cloud Run

### Prerequisitos

- Google Cloud SDK (`gcloud`)
- Proyecto de GCP con Cloud Run y Cloud SQL habilitados
- Artifact Registry configurado

### Paso a Paso

```bash
# 1. Autenticarse
gcloud auth login
gcloud config set project YOUR_PROJECT_ID

# 2. Construir y subir imagen (ejemplo: ms-users)
gcloud builds submit --tag gcr.io/YOUR_PROJECT_ID/ms-users ./ms-users

# 3. Desplegar en Cloud Run
gcloud run deploy ms-users \
  --image gcr.io/YOUR_PROJECT_ID/ms-users \
  --platform managed \
  --region us-central1 \
  --set-env-vars "SPRING_DATASOURCE_URL=jdbc:postgresql://CLOUD_SQL_IP:5432/zentrix" \
  --set-env-vars "SPRING_DATASOURCE_USERNAME=postgres" \
  --set-env-vars "SPRING_DATASOURCE_PASSWORD=YOUR_PASSWORD" \
  --set-env-vars "JWT_SECRET=YOUR_JWT_SECRET" \
  --allow-unauthenticated

# Repetir para ms-products, ms-orders, api-gateway
```

### Cloud SQL

La base de datos PostgreSQL está en Google Cloud SQL:
- **IP**: Configurada en las variables de entorno de Cloud Run
- **Base de datos**: `zentrix`
- **Puerto**: 5432

---

## 🔐 Seguridad

- **JWT**: Tokens firmados con HMAC-SHA256, expiración 24h
- **RBAC**: Roles `ROLE_ADMIN`, `ROLE_SELLER`, `ROLE_CLIENT`
- **Multi-Tenant**: Aislamiento de datos por `tenant_id` en todas las tablas
- **CORS**: Configurado en SecurityConfig (backend) y API Gateway
- **Contraseñas**: BCrypt con factor 12

---

## 🗄️ Base de Datos (Liquibase)

Las migraciones se ejecutan automáticamente al iniciar cada microservicio.

| Microservicio | Tablas principales |
|---------------|-------------------|
| `ms-users` | users, tenants, user_tenant_roles, payments, clients, inventory_items, suppliers, invoices, cash_movements, audit_logs |
| `ms-products` | products, category |
| `ms-orders` | orders, order_items |

---

## 📁 Estructura del Proyecto

```
zentrix/
├── api-gateway/          # Spring Cloud Gateway
├── ms-users/             # Microservicio de usuarios y ERP
├── ms-products/          # Microservicio de productos
├── ms-orders/            # Microservicio de pedidos
├── frontend/             # SPA + Nginx
├── docker-compose.yml    # Orquestación local
├── .env.example          # Template de variables
└── README.md             # Este archivo
```

---

## 🔄 Flujo de Desarrollo

1. Crear rama desde `develop`: `git checkout -b feature/mi-feature`
2. Desarrollar y probar con `docker compose up --build`
3. Commit con formato: `feat(module): description`
4. Push y crear PR hacia `develop`
5. Merge a `develop` → despliegue a staging
6. Merge a `main` → despliegue a producción

---

## 🐛 Troubleshooting

| Problema | Solución |
|----------|----------|
| Error 401 en API | Verificar que el token JWT se envía en header `Authorization: Bearer <token>` |
| CORS bloqueado | Verificar que el gateway tiene CORS configurado y que el frontend usa la URL correcta |
| Liquibase falla | Verificar que la BD existe y las credenciales son correctas |
| Puerto ocupado | Cambiar el mapeo en docker-compose.yml (ej: `8091:8081`) |
| Cloud Run timeout | Verificar que el servicio arranca en menos de 300s |

---

## 📝 Changelog de Refactorización

### Cloud-Native (Mayo 2026)

- ✅ Estandarización de configuración con variables de entorno
- ✅ Eliminación de localhost hardcodeado en backend
- ✅ Optimización de Dockerfiles (JDK → JRE Alpine, -60% tamaño)
- ✅ Docker Compose apuntando a postgres local (no Cloud SQL)
- ✅ Frontend con detección automática de entorno (nginx proxy vs local)
- ✅ API Gateway con puerto dinámico `${PORT:8080}`
- ✅ Healthchecks en todos los servicios
- ✅ .env.example para onboarding rápido
- ✅ .dockerignore para builds más rápidos
- ✅ Formato consistente (application.yml) en todos los microservicios
- ✅ Compatibilidad simultánea: Local / Docker / Cloud Run

---

*© 2026 Zentrix — Red Matrix Solutions. Todos los derechos reservados.*
