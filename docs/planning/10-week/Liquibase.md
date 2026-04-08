# PupaApp - Versionado de Base de Datos con Liquibase

## Estado del Proyecto

Se realizó la integración completa de **Liquibase** en los microservicios del sistema, garantizando control de versiones del esquema de base de datos, trazabilidad y estabilidad en entornos distribuidos.

---

##  Verificación General

Todos los componentes fueron revisados y se encuentran correctamente configurados:

| Archivo | Estado |
|--------|--------|
| pom.xml (todos los servicios) | liquibase-core integrado ✅ |
| application.properties / yml | ddl-auto=none, liquibase activo ✅ |
| changelog-master.yml | ChangeSets definidos correctamente ✅ |
| Category.java | Entidad creada ✅ |
| CategoryRepository.java | Repositorio creado ✅ |
| Product.java | Relación con Category implementada ✅ |
| Dockerfile (ms-users) | Multi-stage build corregido ✅ |

---

##  Problema Inicial

El sistema utilizaba:

```properties
spring.jpa.hibernate.ddl-auto=update


# 📦 PulpApp — Integración de Liquibase y Versionado de Base de Datos

## 👥 Integrantes
- Julian Guerra  
- Edwin Menendez  
- Grupo 5  

---

## 📌 Introducción

Se implementó **Liquibase** en el sistema PulpApp con el objetivo de controlar, versionar y documentar todos los cambios en el esquema de base de datos, eliminando la dependencia de Hibernate (`ddl-auto=update`) y mejorando la trazabilidad del sistema.

---

## 🧠 ¿Qué es Liquibase y por qué lo usamos?

Liquibase es una herramienta de versionado de base de datos que funciona de manera similar a Git.

Cada cambio en la base de datos se define como un **changeSet** con:
- ID único  
- Autor  
- Descripción  

Liquibase registra estos cambios en la tabla:

```sql
databasechangelog
🚨 Problema anterior (Hibernate)

Antes se usaba:

spring.jpa.hibernate.ddl-auto=update

Esto generaba:

❌ Cambios sin control
❌ Sin historial
❌ Riesgo en producción
❌ Sin trazabilidad
✅ Solución

Con Liquibase ahora:

✔ Todos los cambios quedan versionados
✔ Existe historial de ejecución
✔ Cambios reproducibles
✔ Mayor control del sistema
🏗️ Arquitectura del sistema

El sistema sigue una arquitectura de microservicios:

Frontend → API Gateway (:8090)
          → ms-users (:8081)
          → ms-products (:8082)
          → ms-orders (:8083)
                    ↓
              PostgreSQL (:5432)

Todos los servicios usan la base de datos:

pulpapp_db
🧱 Entidades y relaciones

Se implementaron 6 entidades distribuidas en los microservicios:

ms-users:
  users (1) ───────── pedidos (N)

ms-products:
  category (1) ───── products (N)

ms-orders:
  orders (1) ─────── order_items (N)
🔗 Relaciones

Relaciones físicas (FK):

products.category_id → category.id
order_items.order_id → orders.id
pedidos.user_id → users.id

Relaciones lógicas (entre microservicios):

order_items.product_id → ms-products
orders.user_id → ms-users
⚙️ Implementación
🔹 1. Dependencia Liquibase

Se agregó en todos los microservicios:

<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
🔹 2. Configuración
spring.jpa.hibernate.ddl-auto=none
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/changelog-master.yml
🔹 3. Bean explícito (Spring Boot 4)
@Bean
public SpringLiquibase liquibase(DataSource dataSource) {
    SpringLiquibase liquibase = new SpringLiquibase();
    liquibase.setDataSource(dataSource);
    liquibase.setChangeLog(changeLog);
    liquibase.setShouldRun(true);
    return liquibase;
}
🔹 4. Changelogs

Ubicación:

src/main/resources/db/changelog/changelog-master.yml
📦 ms-products (4 changeSets)
Crear category
Crear products
FK products → category
Insertar categorías
📦 ms-orders (4 changeSets)
Crear orders
Crear order_items
FK order_items → orders
Índice
📦 ms-users (3 changeSets)
Crear users
Crear pedidos
FK pedidos → users
🔹 5. Dockerfile corregido

ms-users ahora usa build multi-stage:

FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /build
COPY pom.xml ./
COPY src src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
🔹 6. Nueva entidad Category
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
}
🚨 Problemas encontrados
❌ 1. Dependencia incorrecta
spring-boot-starter-webmvc ❌

✔ Solución:

spring-boot-starter-web ✅
❌ 2. Error en YAML
# INCORRECTO
preConditions:
  - onFail: MARK_RAN

✔ Correcto:

preConditions:
  onFail: MARK_RAN
❌ 3. URL incorrecta
pulpapp_users_db ❌

✔ Corregido a:

pulpapp_db ✅
❌ 4. Auto-configure de Spring Boot 4

✔ Solución: Bean manual de Liquibase

🚀 Ejecución
docker compose down -v
docker compose up --build
🧪 Verificación
🔹 Logs
docker compose logs ms-products

Debe aparecer:

Liquibase: Update has been successful
🔹 pgAdmin
SELECT * FROM databasechangelog ORDER BY dateexecuted;
🔹 Tablas
SELECT table_name FROM information_schema.tables;

Resultado esperado:

category
products
orders
order_items
users
pedidos
databasechangelog
databasechangeloglock
🔹 API Gateway
GET  http://localhost:8090/products
POST http://localhost:8090/products
GET  http://localhost:8090/orders
POST http://localhost:8090/orders
📚 Historia de Usuario
✅ HU1 — Gestión de esquema versionado

✔ Liquibase ejecuta automáticamente
✔ Existe historial en databasechangelog
✔ No se repiten changeSets
✔ Control total del esquema

🏁 Conclusión

Se logró implementar un sistema de versionado de base de datos robusto usando Liquibase, garantizando:

✔ Control de cambios
✔ Trazabilidad
✔ Estabilidad
✔ Arquitectura profesional

El sistema ahora está preparado para entornos reales y despliegues controlados.




