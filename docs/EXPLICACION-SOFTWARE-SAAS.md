# Zentrix — Explicación del Software SaaS
## Arquitectura Orientada a Servicios (SOA)

**Desarrollado por Red Matrix Solutions**

---

## 1. ¿Qué es Zentrix?

Zentrix es una plataforma SaaS (Software as a Service) multi-tenant diseñada para la gestión integral de negocios. Permite que múltiples empresas (tenants) operen de forma aislada sobre una misma infraestructura compartida, cada una con su propio espacio de datos, configuración y branding.

Actualmente soporta dos tipos de negocio:
- **Inmobiliaria**: Gestión de propiedades, arriendos, facturación, contratos
- **Restaurante**: Gestión de menú, pedidos y delivery

---

## 2. Arquitectura Orientada a Servicios (SOA)

El sistema está construido bajo el paradigma de **Arquitectura Orientada a Servicios**, donde cada componente del sistema es un servicio independiente con responsabilidades bien definidas que se comunican entre sí a través de interfaces estandarizadas (APIs REST).

### 2.1 Servicios del Sistema

| Servicio | Responsabilidad | Tecnología |
|----------|----------------|------------|
| **API Gateway** | Punto de entrada único, enrutamiento, CORS | Spring Cloud Gateway |
| **Servicio de Usuarios** | Autenticación, autorización, tenants, ERP | Spring Boot + Spring Security |
| **Servicio de Productos** | Catálogo de productos multi-tenant | Spring Boot |
| **Servicio de Pedidos** | Gestión de órdenes y pagos | Spring Boot |
| **Frontend** | Interfaz de usuario SPA | HTML5 + JavaScript + Nginx |

### 2.2 Diagrama de Arquitectura

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTE (Navegador)                        │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTPS
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    FRONTEND (Cloud Run + Nginx)                   │
│                    Archivos estáticos + SPA                       │
└─────────────────────────────┬───────────────────────────────────┘
                              │ HTTPS (API calls)
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY (Cloud Run)                      │
│              Enrutamiento + CORS + Filtros globales               │
└──────┬──────────────────────┬───────────────────────┬───────────┘
       │                      │                       │
       ▼                      ▼                       ▼
┌──────────────┐    ┌──────────────┐    ┌──────────────────┐
│  Servicio de │    │  Servicio de │    │   Servicio de    │
│   Usuarios   │    │  Productos   │    │    Pedidos       │
│  (Cloud Run) │    │  (Cloud Run) │    │   (Cloud Run)    │
└──────┬───────┘    └──────┬───────┘    └────────┬─────────┘
       │                   │                     │
       └───────────────────┼─────────────────────┘
                           │
                           ▼
              ┌──────────────────────┐
              │   PostgreSQL 15      │
              │   (Google Cloud SQL) │
              │   Base de datos      │
              │   compartida         │
              └──────────────────────┘
```

### 2.3 Principios SOA Aplicados

1. **Servicios autónomos**: Cada servicio se despliega, escala y actualiza de forma independiente
2. **Contratos de servicio**: Comunicación via APIs REST con contratos JSON definidos
3. **Bajo acoplamiento**: Los servicios no dependen de la implementación interna de otros
4. **Reutilización**: El servicio de usuarios es reutilizado por productos y pedidos para validar tokens
5. **Composición**: El gateway compone los servicios para ofrecer una API unificada al frontend
6. **Descubrimiento**: El gateway conoce las URLs de los servicios via variables de entorno

---

## 3. Flujo Multi-Tenant

### 3.1 ¿Qué es Multi-Tenant?

Cada empresa que se registra en Zentrix es un "tenant" (inquilino). Todos los tenants comparten la misma infraestructura pero sus datos están completamente aislados.

### 3.2 Flujo de Registro

```
1. Usuario se registra → Crea cuenta + Tenant automáticamente
2. Se asigna ROLE_CLIENT al usuario
3. Se crea registro en tabla tenants (status: ACTIVE)
4. Se genera JWT con tenantId embebido
5. Usuario accede a su dashboard aislado
```

### 3.3 Aislamiento de Datos

Todas las tablas principales tienen una columna `tenant_id`:
- `users.tenant_id`
- `products.tenant_id`
- `orders.tenant_id`
- `clients.tenant_id`
- `inventory_items.tenant_id`
- `invoices.tenant_id`

Cada consulta filtra por `tenant_id` para garantizar que un tenant nunca vea datos de otro.

---

## 4. Autenticación y Seguridad

### 4.1 JWT (JSON Web Token)

El sistema usa JWT para autenticación stateless:

```
Login → Genera JWT con: { sub: email, role: ROLE_X, tenantId: N, exp: 24h }
     → Se guarda en localStorage del navegador
     → Se envía en cada petición: Authorization: Bearer <token>
```

### 4.2 Roles

| Rol | Acceso |
|-----|--------|
| `ROLE_ADMIN` | Super administrador — gestiona todos los tenants |
| `ROLE_CLIENT` | Usuario de un tenant — accede solo a su negocio |

### 4.3 Flujo de Seguridad

```
Frontend → API Gateway → Servicio de Usuarios (JwtAuthFilter)
                                    │
                                    ├── Extrae token del header Authorization
                                    ├── Valida firma con JWT_SECRET
                                    ├── Verifica expiración
                                    ├── Carga usuario de la BD
                                    ├── Establece SecurityContext
                                    └── Permite o deniega acceso
```

---

## 5. Gestión de Suscripciones

### 5.1 Estados del Tenant

| Estado | Significado |
|--------|-------------|
| `ACTIVE` | Operación normal |
| `INACTIVE` | Suspendido por el admin — sin acceso |

### 5.2 Flujo de Suspensión

```
1. Admin hace clic en "Suspender" → PATCH /tenants/{id}/status?status=INACTIVE
2. El tenant cambia a INACTIVE en la BD
3. El dashboard del cliente verifica cada 30 segundos el estado
4. Si detecta INACTIVE → Muestra pantalla de "Cuenta Suspendida"
5. El cliente no puede usar el sistema hasta que el admin lo reactive
```

### 5.3 Flujo de Reactivación

```
1. Admin hace clic en "Activar" → PATCH /tenants/{id}/status?status=ACTIVE
2. El tenant cambia a ACTIVE en la BD
3. El cliente ve mensaje "Sesión actualizada — inicia sesión para continuar"
4. El cliente inicia sesión → Acceso normal restaurado
```

---

## 6. Módulos del Sistema

### 6.1 Panel de Super Administrador

- Gestión de tenants (crear, suspender, activar, eliminar)
- Gestión de usuarios del sistema
- Pagos pendientes de aprobación
- Productos y pedidos globales
- Creación de otros super administradores

### 6.2 Dashboard Inmobiliaria

- **Propiedades**: Registro de inmuebles con propietario, dirección, valor arriendo
- **Clientes**: Propietarios, inquilinos y codeudores
- **Facturación**: 
  - Factura Inquilino (canon completo + intereses por mora)
  - Liquidación Propietario (canon - comisión 11%)
  - Cobro por Mantenimiento/Reparaciones
  - Cobro por Servicios Públicos (prorrateo por días)
- **Contratos**: Subida de PDFs con datos del propietario/inquilino
- **Pagos/Nómina**: Control de pagos a propietarios y colaboradores
- **Reportes**: Estadísticas del negocio
- **Configuración**: Logo, nombre, NIT, dirección (persistido en la nube)

### 6.3 Dashboard Restaurante

- Gestión de menú
- Pedidos y delivery
- Control de inventario

---

## 7. Infraestructura Cloud

### 7.1 Google Cloud Platform

| Servicio GCP | Uso |
|-------------|-----|
| **Cloud Run** | Hosting de todos los servicios (serverless containers) |
| **Cloud SQL** | Base de datos PostgreSQL 15 gestionada |
| **Container Registry** | Almacenamiento de imágenes Docker |
| **Cloud Build** | Construcción automática de imágenes |

### 7.2 Docker

Cada servicio tiene su propio `Dockerfile` optimizado:
- **Backend**: Multi-stage build (Maven → JRE Debian)
- **Frontend**: Nginx Alpine con entrypoint dinámico

### 7.3 Variables de Entorno

La configuración es completamente desacoplada del código:
- `SPRING_DATASOURCE_URL` — Conexión a PostgreSQL
- `JWT_SECRET` — Clave para firmar tokens
- `GATEWAY_MS_USERS_URI` — URL del servicio de usuarios
- `PORT` — Puerto dinámico (inyectado por Cloud Run)

---

## 8. Base de Datos

### 8.1 Liquibase (Migraciones)

El esquema de la BD se gestiona con Liquibase — migraciones versionadas que se ejecutan automáticamente al arrancar cada servicio.

### 8.2 Tablas Principales

```
users                  → Usuarios del sistema
tenants                → Empresas/negocios registrados
user_tenant_roles      → Relación usuario-tenant-rol (RBAC)
products               → Catálogo de productos
orders / order_items   → Pedidos y detalle
clients                → Clientes del negocio (ERP)
inventory_items        → Propiedades/inventario
invoices / invoice_items → Facturación
suppliers              → Proveedores
cash_movements         → Caja y movimientos
audit_logs             → Auditoría
payments               → Pagos de onboarding SaaS
tenant_config          → Configuración visual por tenant
```

---

## 9. Despliegue

### 9.1 Flujo de Despliegue

```
1. Desarrollador hace cambios en el código
2. git commit + git push origin develop
3. En Cloud Shell:
   - git pull origin develop
   - gcloud builds submit (construye imagen Docker)
   - gcloud run deploy (despliega en Cloud Run)
4. El servicio se actualiza sin downtime
```

### 9.2 Comandos de Despliegue

```bash
# Frontend
gcloud builds submit --tag gcr.io/PROJECT_ID/zentrix-frontend ./frontend
gcloud run deploy zentrix-frontend --image gcr.io/PROJECT_ID/zentrix-frontend --region us-central1 --port 8080 --allow-unauthenticated

# Servicio de Usuarios
gcloud builds submit --tag gcr.io/PROJECT_ID/ms-users ./ms-users
gcloud run deploy ms-users --image gcr.io/PROJECT_ID/ms-users --region us-central1 --port 8081 --memory 1Gi --set-env-vars "..." --allow-unauthenticated

# API Gateway
gcloud builds submit --tag gcr.io/PROJECT_ID/api-gateway ./api-gateway
gcloud run deploy api-gateway --image gcr.io/PROJECT_ID/api-gateway --region us-central1 --port 8080 --set-env-vars "..." --allow-unauthenticated
```

---

## 10. Tecnologías Utilizadas

| Capa | Tecnología |
|------|-----------|
| Frontend | HTML5, CSS3, JavaScript Vanilla, Nginx |
| Backend | Java 17, Spring Boot 4.0.3, Spring Security, Spring Cloud Gateway |
| Base de datos | PostgreSQL 15, Liquibase |
| Autenticación | JWT (HMAC-SHA256) |
| Contenedores | Docker, Docker Compose |
| Cloud | Google Cloud Run, Cloud SQL, Cloud Build |
| Control de versiones | Git, GitHub |

---

## 11. Buenas Prácticas Implementadas

- **Separación de responsabilidades**: Cada servicio tiene una función específica
- **Configuración externalizada**: Variables de entorno, sin hardcoding
- **Seguridad por capas**: CORS en gateway + JWT en servicios + RBAC
- **Persistencia en la nube**: Facturas, configuración y datos en Cloud SQL
- **Migraciones versionadas**: Liquibase para evolución del esquema
- **Despliegue sin downtime**: Cloud Run con revisiones automáticas
- **Multi-tenant por diseño**: Aislamiento de datos desde la arquitectura

---

## 12. Equipo

- **Red Matrix Solutions** — Desarrollo y arquitectura
- **Zentrix Platform** — Producto SaaS

---

*Documento generado el 20 de mayo de 2026*
*Versión: 1.0*
