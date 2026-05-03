# 🏢 PulpApp — Transformación SaaS Multi-Tenant

## Índice

- [Visión General](#visión-general)
- [Estrategia Elegida](#estrategia-elegida)
- [Estado del Proyecto](#estado-del-proyecto)
- [Fase 1 — Soporte Multi-Tenant Básico en ms-users](#fase-1--soporte-multi-tenant-básico-en-ms-users)
- [Fase 2 — Aislamiento Multi-Tenant en ms-products](#fase-2--aislamiento-multi-tenant-en-ms-products)
- [Fases Futuras](#fases-futuras)
- [Diagrama de Arquitectura Actual](#diagrama-de-arquitectura-actual)
- [Glosario](#glosario)

---

## Visión General

Este documento registra el proceso incremental de transformación de PulpApp desde un sistema e-commerce **single-tenant** (una sola tienda) hacia una arquitectura **SaaS Multi-Tenant** donde múltiples negocios pueden operar de forma aislada sobre la misma infraestructura.

**Principio fundamental:** cada fase se implementa sin romper la funcionalidad existente. El frontend y los endpoints públicos siguen funcionando sin cambios después de cada fase.

---

## Estrategia Elegida

**Shared Database, Shared Schema** — Una sola base de datos (`pulpapp_db`), una sola estructura de tablas, aislamiento lógico por columna `tenant_id`.

| Estrategia | Aislamiento | Costo | Complejidad | Elegida |
|---|---|---|---|---|
| Database per Tenant | Alto | Alto | Alta | ❌ |
| Schema per Tenant | Medio | Medio | Media | ❌ |
| **Shared Schema (tenant_id)** | **Lógico** | **Bajo** | **Baja** | **✅** |

**¿Por qué esta estrategia?**
- PulpApp está en etapa temprana — no necesita aislamiento físico aún
- Reduce costos de infraestructura (una sola BD)
- Permite migrar a schema-per-tenant o database-per-tenant en el futuro sin cambiar la lógica de negocio
- Las migraciones Liquibase son incrementales y seguras

---

## Estado del Proyecto

| Fase | Microservicio | Estado | Commit |
|---|---|---|---|
| Fase 1 | ms-users | ✅ Completada | `feat(multi-tenant): Fase 1 - Soporte Multi-Tenant basico en ms-users` |
| Fase 2 | ms-products | ✅ Completada | `feat(multi-tenant): Fase 2 - Aislamiento Multi-Tenant en ms-products` |
| Fase 3 | ms-orders | ✅ Completada | `feat(multi-tenant): Fase 3 - Aislamiento Multi-Tenant en ms-orders` |
| Fase 4 | Frontend (registro con tenant) | ⏳ Pendiente | — |
| Fase 5 | Admin por tenant | ⏳ Pendiente | — |
| Fase 6 | Configuración por tenant | ⏳ Pendiente | — |

---

## Fase 1 — Soporte Multi-Tenant Básico en ms-users

**Fecha:** Mayo 2026  
**Rama:** `pruebas-apis`  
**Objetivo:** Introducir el concepto de "tenant" en el sistema, asociar usuarios a tenants e incluir el `tenantId` en el JWT.

### ¿Qué es un Tenant?

Un tenant representa un negocio o tienda que usa la plataforma. Cada tenant tiene sus propios usuarios, y en fases posteriores, sus propios productos y pedidos aislados.

### Cambios Realizados

#### Archivos Nuevos (8)

| Archivo | Descripción |
|---|---|
| `ms-users/.../entity/Tenant.java` | Entidad JPA: id, name, status (ACTIVE/INACTIVE), createdAt |
| `ms-users/.../entity/TenantStatus.java` | Enum con estados ACTIVE e INACTIVE |
| `ms-users/.../repository/TenantRepository.java` | Repositorio JPA para operaciones CRUD de tenants |
| `ms-users/.../service/TenantService.java` | Lógica de negocio: CRUD + tenant por defecto automático |
| `ms-users/.../controller/TenantController.java` | API REST `/tenants/**` (solo ROLE_ADMIN) |
| `ms-users/.../dto/TenantRequestDTO.java` | DTO de entrada para crear tenants |
| `ms-users/.../dto/TenantResponseDTO.java` | DTO de salida con datos del tenant |
| `ms-users/.../tenant/TenantContext.java` | ThreadLocal que almacena tenantId por request |

#### Archivos Modificados (9)

| Archivo | Cambio |
|---|---|
| `ms-users/.../entity/User.java` | +campo `tenantId` (Long) + relación `@ManyToOne` a Tenant |
| `ms-users/.../security/JwtService.java` | +método `generateToken(userDetails, role, tenantId)` + `extractTenantId()` |
| `ms-users/.../security/JwtAuthFilter.java` | +extracción de tenantId del JWT → `TenantContext.setTenantId()` |
| `ms-users/.../service/AuthService.java` | Login y registro ahora asignan tenant e incluyen tenantId en el JWT |
| `ms-users/.../dto/AuthResponseDTO.java` | +campo `tenantId` en la respuesta de login/registro |
| `ms-users/.../dto/UserResponseDTO.java` | +campo `tenantId` en la respuesta de usuario |
| `ms-users/.../mapper/UserMapper.java` | +mapeo de `tenantId` en `toResponseDto()` |
| `ms-users/.../config/SecurityConfig.java` | +regla `/tenants/**` → solo ROLE_ADMIN |
| `api-gateway/.../application.yml` | +ruta `/tenants/**` → ms-users:8081 |

#### Migraciones Liquibase (6 changeSets)

| ID | Acción |
|---|---|
| `10-create-tenants-table` | Crea tabla `tenants` (id, name, status, created_at) |
| `11-seed-default-tenant` | Inserta tenant "PulpApp" como tenant por defecto |
| `12-add-tenant-id-to-users` | Agrega columna `tenant_id` a `users` (nullable inicialmente) |
| `13-migrate-users-to-default-tenant` | Asigna tenant PulpApp a todos los usuarios existentes |
| `14-add-fk-users-to-tenants` | Crea FK `users.tenant_id` → `tenants.id` con RESTRICT |
| `15-index-users-tenant-id` | Índice en `users.tenant_id` para optimizar consultas |

### Flujo de Funcionamiento

```
REGISTRO DE USUARIO:
  1. POST /auth/register
  2. AuthService obtiene el tenant por defecto ("PulpApp")
     → Si no existe, lo crea automáticamente
  3. Se crea el User con tenantId = tenant.id
  4. Se genera JWT con claims: { sub: email, role: ROLE_CLIENT, tenantId: 1 }
  5. Se retorna AuthResponseDTO con tenantId incluido

LOGIN:
  1. POST /auth/login
  2. AuthService autentica con email + password
  3. Lee user.tenantId de la BD
  4. Genera JWT con claims: { sub: email, role: ROLE_CLIENT, tenantId: 1 }

REQUEST AUTENTICADO:
  1. JwtAuthFilter extrae el JWT del header Authorization
  2. Valida firma y expiración
  3. Establece SecurityContext (autenticación Spring)
  4. Extrae tenantId del JWT → TenantContext.setTenantId(1)
  5. Cualquier servicio puede llamar TenantContext.getTenantId()
  6. Al terminar el request, TenantContext se limpia automáticamente
```

### Decisiones de Diseño

1. **Tenant por defecto automático** — El tenant "PulpApp" se crea vía Liquibase (seed) y también por código (`getOrCreateDefaultTenant()`) como doble seguridad.

2. **TenantContext dentro de JwtAuthFilter** — Se extrae el tenantId en el mismo filtro que valida el JWT, evitando un filtro separado y problemas de orden.

3. **Compatibilidad con tokens antiguos** — `extractTenantId()` retorna null si el claim no existe. Los endpoints públicos no se ven afectados.

4. **ThreadLocal para el contexto** — Patrón estándar en Spring MVC (Servlet). Cada hilo tiene su propio tenantId, se limpia en `finally` para evitar memory leaks.

### Ejemplo de JWT Generado

```json
{
  "sub": "usuario@email.com",
  "role": "ROLE_CLIENT",
  "tenantId": 1,
  "iat": 1714700000,
  "exp": 1714786400
}
```

---

## Fase 2 — Aislamiento Multi-Tenant en ms-products

**Fecha:** Mayo 2026  
**Rama:** `pruebas-apis`  
**Objetivo:** Que los productos y categorías estén aislados por tenant. Un tenant NO puede ver ni modificar datos de otro tenant.

### Desafío Principal

ms-products **no tiene Spring Security**. No puede validar JWT como ms-users. La solución fue agregar un filtro HTTP ligero (`TenantJwtFilter`) que solo decodifica el JWT para extraer el `tenantId`, sin necesidad de todo el framework de seguridad.

### Cambios Realizados

#### Archivos Nuevos (2)

| Archivo | Descripción |
|---|---|
| `ms-products/.../tenant/TenantContext.java` | ThreadLocal para almacenar tenantId por request (mismo patrón que ms-users) |
| `ms-products/.../tenant/TenantJwtFilter.java` | Filtro HTTP que decodifica JWT usando JJWT directamente |

#### Archivos Modificados (10)

| Archivo | Cambio |
|---|---|
| `ms-products/.../entity/Product.java` | +campo `tenantId` (Long) |
| `ms-products/.../entity/Category.java` | +campo `tenantId` (Long) |
| `ms-products/.../repository/ProductRepository.java` | +4 métodos filtrados por tenant |
| `ms-products/.../repository/CategoryRepository.java` | +2 métodos filtrados por tenant |
| `ms-products/.../service/ProductService.java` | Reescrito: todas las operaciones filtran/asignan por tenant |
| `ms-products/.../dto/ProductResponseDTO.java` | +campo `tenantId` |
| `ms-products/pom.xml` | +dependencias JJWT (api, impl, jackson) |
| `ms-products/.../application.properties` | +propiedades `jwt.secret` y `tenant.default-id` |
| `ms-products/.../changelog-master.yml` | +6 changeSets para multi-tenant |
| `docker-compose.yml` | +variables `JWT_SECRET` y `TENANT_DEFAULT_ID` en ms-products |

#### Migraciones Liquibase (6 changeSets)

| ID | Acción |
|---|---|
| `6-add-tenant-id-to-products` | Agrega columna `tenant_id` a `products` (nullable) |
| `7-add-tenant-id-to-category` | Agrega columna `tenant_id` a `category` (nullable) |
| `8-migrate-products-to-default-tenant` | Asigna tenant PulpApp a productos existentes |
| `9-migrate-categories-to-default-tenant` | Asigna tenant PulpApp a categorías existentes |
| `10-index-products-tenant-id` | Índice en `products.tenant_id` |
| `11-index-category-tenant-id` | Índice en `category.tenant_id` |

#### Métodos Nuevos en Repositorios

```java
// ProductRepository
List<Product> findAllByTenantId(Long tenantId);
Optional<Product> findByIdAndTenantId(Long id, Long tenantId);
boolean existsByNameIgnoreCaseAndTenantId(String name, Long tenantId);
boolean existsByNameIgnoreCaseAndTenantIdAndIdNot(String name, Long tenantId, Long id);

// CategoryRepository
List<Category> findAllByTenantId(Long tenantId);
Optional<Category> findByIdAndTenantId(Long id, Long tenantId);
```

### Flujo de Funcionamiento

```
GET /products (PÚBLICO — sin JWT):
  1. TenantJwtFilter: no hay JWT → TenantContext queda vacío
  2. ProductService.resolveTenantId() → usa defaultTenantId (1)
  3. findAllByTenantId(1) → productos del tenant PulpApp
  4. Frontend funciona igual que antes ✅

POST /products (ADMIN — con JWT):
  1. TenantJwtFilter: decodifica JWT → TenantContext.setTenantId(1)
  2. ProductService.create(): resolveTenantId() → 1
  3. Producto se guarda con tenant_id = 1
  4. Validación de nombre único: solo dentro del mismo tenant ✅

GET /products/5 (usuario de tenant 2 intenta acceder a producto de tenant 1):
  1. TenantJwtFilter: tenantId = 2
  2. findByIdAndTenantId(5, 2) → producto 5 es de tenant 1
  3. Resultado: 404 Not Found (NO 403) ✅
```

### Decisiones de Diseño

1. **Filtro ligero sin Spring Security** — ms-products usa `TenantJwtFilter` que solo decodifica el JWT con JJWT. No necesita toda la infraestructura de Spring Security.

2. **Fallback al tenant por defecto** — Los endpoints públicos (`GET /products`) no requieren JWT. Cuando no hay token, se usa `tenant.default-id=1`. Esto garantiza compatibilidad con el frontend actual.

3. **404 en lugar de 403** — Si un tenant intenta acceder a un producto de otro tenant, la respuesta es 404 (no encontrado), no 403 (prohibido). Esto evita que un atacante pueda enumerar recursos de otros tenants.

4. **Unicidad de nombre por tenant** — Dos tenants diferentes pueden tener un producto llamado "Pulpa de Mango". La validación cambió de `existsByNameIgnoreCase` a `existsByNameIgnoreCaseAndTenantId`.

5. **Misma clave JWT compartida** — ms-products usa la misma clave secreta que ms-users para verificar tokens. En producción, esta clave debería venir de un config server o vault compartido.

---

## Fase 3 — Aislamiento Multi-Tenant en ms-orders

**Fecha:** Mayo 2026  
**Rama:** `pruebas-apis`  
**Objetivo:** Que los pedidos y sus items estén aislados por tenant. Un tenant NO puede ver, modificar ni aprobar pedidos de otro tenant.

### Desafío Principal

ms-orders, al igual que ms-products, **no tiene Spring Security**. Además, ms-orders se comunica con ms-products (para validar productos) y con ms-users (para enriquecer datos de clientes). El aislamiento debe garantizar que estas consultas inter-servicio respeten el contexto del tenant.

### Cambios Realizados

#### Archivos Nuevos (2)

| Archivo | Descripción |
|---|---|
| `ms-orders/.../tenant/TenantContext.java` | ThreadLocal para almacenar tenantId por request |
| `ms-orders/.../tenant/TenantJwtFilter.java` | Filtro HTTP que decodifica JWT usando JJWT directamente |

#### Archivos Modificados (10)

| Archivo | Cambio |
|---|---|
| `ms-orders/.../entity/Order.java` | +campo `tenantId` (Long) |
| `ms-orders/.../entity/OrderItem.java` | +campo `tenantId` (Long) — hereda del pedido padre |
| `ms-orders/.../repository/OrderRepository.java` | +`findAllByTenantId`, `findByIdAndTenantId`, `findFrequentClientsByTenantId` |
| `ms-orders/.../service/OrderService.java` | Reescrito: create, findAll, findById filtran por tenant |
| `ms-orders/.../service/PaymentService.java` | Reescrito: markAsPaid, approve, reject, findPending filtran por tenant |
| `ms-orders/.../service/SellerOrderService.java` | findAllForSeller filtra por tenant |
| `ms-orders/.../service/FrequentClientService.java` | findFrequentClients filtra por tenant |
| `ms-orders/.../dto/OrderResponseDTO.java` | +campo `tenantId` |
| `ms-orders/pom.xml` | +dependencias JJWT |
| `ms-orders/.../application.properties` | +propiedades `jwt.secret` y `tenant.default-id` |
| `ms-orders/.../changelog-master.yml` | +6 changeSets para multi-tenant |
| `docker-compose.yml` | +variables `JWT_SECRET` y `TENANT_DEFAULT_ID` en ms-orders |

#### Migraciones Liquibase (6 changeSets)

| ID | Acción |
|---|---|
| `7-add-tenant-id-to-orders` | Agrega columna `tenant_id` a `orders` (nullable) |
| `8-add-tenant-id-to-order-items` | Agrega columna `tenant_id` a `order_items` (nullable) |
| `9-migrate-orders-to-default-tenant` | Asigna tenant PulpApp a pedidos existentes |
| `10-migrate-order-items-to-default-tenant` | Asigna tenant PulpApp a items existentes |
| `11-index-orders-tenant-id` | Índice en `orders.tenant_id` |
| `12-index-order-items-tenant-id` | Índice en `order_items.tenant_id` |

### Flujo de Funcionamiento

```
POST /orders (PÚBLICO — sin JWT):
  1. TenantJwtFilter: no hay JWT → TenantContext vacío
  2. OrderService.resolveTenantId() → usa defaultTenantId (1)
  3. Order y OrderItems se guardan con tenant_id = 1
  4. Frontend funciona igual que antes ✅

GET /orders (ADMIN/SELLER — con JWT):
  1. TenantJwtFilter: JWT tiene tenantId=1 → TenantContext.set(1)
  2. OrderService.findAll() → findAllByTenantId(1)
  3. Solo pedidos del tenant 1 ✅

PUT /orders/5/approve (ADMIN de tenant 2 intenta aprobar pedido de tenant 1):
  1. TenantJwtFilter: tenantId = 2
  2. PaymentService.findOrderByTenant(5) → findByIdAndTenantId(5, 2)
  3. Pedido 5 es de tenant 1 → 404 Not Found ✅
```

### Servicios Afectados

| Servicio | Operaciones con tenant |
|---|---|
| **OrderService** | `createOrder` (asigna tenant), `findAll` (filtra), `findById` (filtra), `assignUniqueAmount` (solo compara dentro del tenant) |
| **PaymentService** | `markAsPaid`, `approvePayment`, `rejectPayment`, `findPendingApproval`, `getPaymentStatus` — todos filtran por tenant |
| **SellerOrderService** | `findAllForSeller` — solo pedidos del tenant actual |
| **FrequentClientService** | `findFrequentClients` — solo clientes del tenant actual |

### Decisiones de Diseño

1. **OrderItem hereda tenantId del Order** — Cuando se crea un pedido, cada item recibe el mismo tenantId que el pedido padre. Esto permite consultas directas sobre items sin necesidad de hacer JOIN con orders.

2. **uniqueAmount aislado por tenant** — La generación de montos únicos para transferencias ahora solo compara contra pedidos activos del mismo tenant. Dos tenants pueden tener el mismo uniqueAmount sin conflicto.

3. **Mismo patrón que Fase 2** — TenantContext + TenantJwtFilter + resolveTenantId() con fallback. Consistencia total entre los 3 microservicios.

4. **Clientes frecuentes por tenant** — La query JPQL ahora incluye `WHERE o.tenantId = :tenantId`. Un admin solo ve los clientes frecuentes de su propio tenant.

---

## Fases Futuras

### Fase 3 — Aislamiento Multi-Tenant en ms-orders (Pendiente)

- Agregar `tenantId` a `Order` y `OrderItem`
- Replicar `TenantContext` + `TenantJwtFilter` en ms-orders
- Filtrar pedidos por tenant en todos los repositorios y servicios
- Mismo patrón que Fase 2

### Fase 4 — Registro con Selección de Tenant (Pendiente)

- Permitir que un usuario elija un tenant existente o cree uno nuevo al registrarse
- Cambios en frontend (formulario de registro) y AuthService

### Fase 5 — Admin por Tenant (Pendiente)

- Cada tenant tiene su propio administrador
- Cambios en RBAC: ROLE_ADMIN se convierte en ROLE_TENANT_ADMIN
- Super admin global para gestionar todos los tenants

### Fase 6 — Configuración por Tenant (Pendiente)

- Aislar `app_config` y `notifications` por tenant
- Cada tenant puede personalizar su configuración

### Fase 7 — Billing y Suscripciones (Pendiente)

- Módulo nuevo para gestionar planes y pagos por tenant
- Límites por plan (cantidad de productos, usuarios, pedidos)

---

## Diagrama de Arquitectura Actual

```
┌──────────────────────────────────────────────────────────────────┐
│                    FRONTEND (Nginx:3000)                         │
│              HTML + CSS + Vanilla JS (sin cambios)               │
└──────────────────────┬───────────────────────────────────────────┘
                       │
┌──────────────────────▼───────────────────────────────────────────┐
│           API GATEWAY (Spring Cloud Gateway:8090)                │
│     Rutas: /auth, /users, /tenants, /products, /orders           │
└──────┬───────────────────┬───────────────────┬───────────────────┘
       │                   │                   │
┌──────▼──────┐     ┌──────▼──────┐     ┌──────▼──────┐
│  ms-users   │     │ ms-products │     │  ms-orders  │
│   :8081     │     │   :8082     │     │   :8083     │
│             │     │             │     │             │
│ ✅ Tenant   │     │ ✅ Filtro   │     │ ✅ Filtro   │
│ ✅ JWT+tid  │     │ ✅ Aislam.  │     │ ✅ Aislam.  │
│ ✅ Context  │     │ ✅ Context  │     │ ✅ Context  │
└──────┬──────┘     └──────┬──────┘     └──────┬──────┘
       │                   │                   │
┌──────▼───────────────────▼───────────────────▼──────┐
│                    pulpapp_db                        │
│                                                      │
│  tenants ✅           products.tenant_id ✅           │
│  users.tenant_id ✅   category.tenant_id ✅           │
│                       orders.tenant_id ✅ (Fase 3)    │
│                       order_items.tenant_id ✅         │
└──────────────────────────────────────────────────────┘
```

---

## Glosario

| Término | Definición |
|---|---|
| **Tenant** | Un negocio o tienda que usa la plataforma PulpApp. Cada tenant tiene sus propios datos aislados. |
| **Multi-Tenant** | Arquitectura donde múltiples clientes (tenants) comparten la misma infraestructura pero con datos aislados. |
| **SaaS** | Software as a Service — modelo donde el software se ofrece como servicio en la nube, accesible por múltiples clientes. |
| **Shared Schema** | Estrategia donde todos los tenants comparten las mismas tablas, diferenciados por una columna `tenant_id`. |
| **TenantContext** | Objeto ThreadLocal que almacena el `tenantId` del request actual para que cualquier capa pueda consultarlo. |
| **JWT** | JSON Web Token — token firmado que transporta información del usuario (email, rol, tenantId) entre servicios. |
| **Liquibase** | Herramienta de versionado de esquema de base de datos. Ejecuta migraciones incrementales al arrancar cada microservicio. |
| **Idempotente** | Una operación que puede ejecutarse múltiples veces sin cambiar el resultado. Las migraciones Liquibase usan `MARK_RAN` para ser idempotentes. |
