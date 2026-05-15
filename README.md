# 🏢 Zentrix — Plataforma SaaS Multi-Tenant

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
